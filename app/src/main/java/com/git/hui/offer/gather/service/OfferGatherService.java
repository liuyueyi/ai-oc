package com.git.hui.offer.gather.service;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.idev.excel.ExcelReader;
import cn.idev.excel.FastExcel;
import cn.idev.excel.context.AnalysisContext;
import cn.idev.excel.metadata.data.ReadCellData;
import cn.idev.excel.read.listener.ReadListener;
import com.git.hui.offer.components.bizexception.BizException;
import com.git.hui.offer.components.bizexception.StatusEnum;
import com.git.hui.offer.constants.gather.GatherTargetTypeEnum;
import com.git.hui.offer.gather.model.GatherFileBo;
import com.git.hui.offer.gather.model.GatherOcDraftBo;
import com.git.hui.offer.gather.model.GatherTaskProcessBo;
import com.git.hui.offer.gather.model.GatherTaskResultBo;
import com.git.hui.offer.gather.model.TaskChangeListener;
import com.git.hui.offer.oc.convert.DraftConvert;
import com.git.hui.offer.oc.dao.entity.OcDraftEntity;
import com.git.hui.offer.oc.service.GatherService;
import com.git.hui.offer.util.json.IntBaseEnum;
import com.git.hui.offer.util.json.JsonUtil;
import com.git.hui.offer.web.model.req.GatherReq;
import com.git.hui.offer.web.model.res.GatherVo;
import com.google.common.base.Joiner;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author YiHui
 * @date 2025/7/14
 */
@Slf4j
@Service
public class OfferGatherService {
    // 默认六行数据一组
    private static final Integer SPLIT_LEN = 6;

    private static final AtomicBoolean SCHEDULE_LOCK = new AtomicBoolean(false);

    private final GatherAiAgent gatherAiAgent;

    private final GatherService gatherService;

    private final GatherTaskService gatherTaskService;

    @Autowired
    public OfferGatherService(GatherAiAgent gatherAiAgent, GatherService gatherService, GatherTaskService gatherTaskService) {
        this.gatherAiAgent = gatherAiAgent;
        this.gatherService = gatherService;
        this.gatherTaskService = gatherTaskService;
    }

    /**
     * 监听任务变更事件
     *
     * @param listener
     */
    @Async
    @EventListener(TaskChangeListener.class)
    public void taskListener(TaskChangeListener listener) {
        switch (listener.getState()) {
            // 触发任务执行
            case INIT -> scheduleToLoadTask();
            case SUCCEED, FAILED, PROCESSING -> {
                log.info("任务执行，可以给用户发送一个消息通知<任务编号:{} 状态:{}>", listener.getTaskId(), listener.getState());
            }
        }
    }

    /**
     * 任务驱动方式，用于执行采集任务
     */
    @Scheduled(cron = "0 0/5 * * * ?")
    public void scheduleToLoadTask() {
        if (SCHEDULE_LOCK.get()) {
            log.info("任务已经在执行中了，当前只支持任务的单线程调度~");
            return;
        }

        SCHEDULE_LOCK.set(true);
        try {
            while (true) {
                GatherTaskProcessBo bo = gatherTaskService.pickUnProcessTaskToProcess();
                if (bo == null) {
                    return;
                }

                GatherReq req = new GatherReq(bo.content(), bo.type().getValue(), bo.model());
                try {
                    GatherFileBo file = null;
                    if (bo.type() == GatherTargetTypeEnum.CSV_FILE || bo.type() == GatherTargetTypeEnum.EXCEL_FILE
                            || bo.type() == GatherTargetTypeEnum.IMAGE) {
                        // 文件，需要先下来
                        file = gatherTaskService.loadTmpFile(bo.content());
                    }
                    GatherVo vo = gatherInfo(req, file);

                    // 保存任务执行结果
                    GatherTaskResultBo res = new GatherTaskResultBo(
                            GatherTaskResultBo.SUCCESS
                            , vo.getInsertList().stream().map(OcDraftEntity::getId).collect(Collectors.toList())
                            , vo.getUpdateList().stream().map(OcDraftEntity::getId).collect(Collectors.toList())
                    );
                    gatherTaskService.saveTaskResult(bo.taskId(), res);
                } catch (Exception e) {
                    log.error("gather task error: {}", bo, e);
                    // 任务执行失败，同样需要保存结果
                    gatherTaskService.saveTaskResult(bo.taskId(), new GatherTaskResultBo(e.getMessage(), List.of(), List.of()));
                }
            }
        } finally {
            SCHEDULE_LOCK.set(false);
        }
    }

    public GatherVo gatherInfo(GatherReq req, GatherFileBo file) throws IOException {
        GatherTargetTypeEnum targetTypeEnum = IntBaseEnum.getEnumByCode(GatherTargetTypeEnum.class, req.type());
        Assert.notNull(targetTypeEnum, "不支持的gather类型");
        Function<GatherReq, List<GatherOcDraftBo>> func = switch (targetTypeEnum) {
            case TEXT -> gatherByText(req.content());
            case HTML_TEXT -> gatherByHtmlText(req.content());
            case HTTP_URL -> gatherByHttpUrl(req.content());
            case IMAGE -> gatherByImg(file);
            default -> null;
        };
        if (func == null) {
            return gatherFileInfo(req, file);
        }
        List<GatherOcDraftBo> list = func.apply(req);
        log.info("大模型提取后业务对象是：{}", JsonUtil.toStr(list));
        GatherVo res = gatherService.saveDraftDataList(DraftConvert.convert(list));
        return res;
    }

    /**
     * 传入文件进行数据提取的场景，我们直接读取文件，做好分页调用大模型，避免大模型返回数据截断的问题
     * 1. 支持传入 csv, excel；
     * 2. 推荐的输入是第一行为标题，第二行开始为数据
     *
     * @return
     * @throws IOException
     */
    public GatherVo gatherFileInfo(GatherReq req, GatherFileBo file) throws IOException {
        Pair<String, List<String>> pair;
        if (req.type().equals(GatherTargetTypeEnum.CSV_FILE.getValue())) {
            pair = parseContentsFromCsv(file);
        } else if (req.type().equals(GatherTargetTypeEnum.EXCEL_FILE.getValue())) {
            pair = parseContentsFromExcel(file);
        } else {
            throw new BizException(StatusEnum.UNEXPECT_ERROR, "不支持的文件类型");
        }

        List<OcDraftEntity> insert = new ArrayList<>();
        List<OcDraftEntity> update = new ArrayList<>();
        StringBuilder builder;
        int index = 0;
        while (index < pair.getSecond().size()) {
            builder = new StringBuilder();
            builder.append(pair.getFirst()).append("\n");
            List<String> items = pair.getSecond().subList(index, Math.min(index + SPLIT_LEN, pair.getSecond().size()));
            builder.append(Joiner.on("\n").join(items));
            index += SPLIT_LEN;

            try {
                // 文本解析
                List<GatherOcDraftBo> list = gatherAiAgent.gatherByText(builder.toString());
                log.info("大模型提取后业务对象是：{}", JsonUtil.toStr(list));
                GatherVo tmp = gatherService.saveDraftDataList(DraftConvert.convert(list));
                insert.addAll(tmp.getInsertList());
                update.addAll(tmp.getUpdateList());
            } catch (Exception e) {
                log.error("解析失败，请检查大模型是否正常", e);
            }
        }

        return new GatherVo().setInsertList(insert).setUpdateList(update);
    }


    /**
     * 直接根据用户传入的文本，进行解析获取职位信息
     *
     * @return
     */
    private Function<GatherReq, List<GatherOcDraftBo>> gatherByText(String txt) {
        String testText = StringUtils.isBlank(txt) ? ResourceUtil.readUtf8Str("data/oc.txt") : txt;
        return (s) -> {
            return gatherAiAgent.gatherByText(testText);
        };
    }

    private Function<GatherReq, List<GatherOcDraftBo>> gatherByHtmlText(String text) {
        String testText = StringUtils.isBlank(text) ? ResourceUtil.readUtf8Str("data/oc-html.txt") : text;
        return (s) -> {
            return gatherAiAgent.gatherByAutoSplit(testText);
        };
    }

    private Function<GatherReq, List<GatherOcDraftBo>> gatherByHttpUrl(String filePath) {
        URI uri;
        try {
            // 做一个url的合法性判断
            uri = URI.create(filePath.trim());
        } catch (Exception e) {
            throw new BizException(StatusEnum.UNEXPECT_ERROR, "请输入合法的url地址");
        }
        if (uri.getScheme() == null || !uri.getScheme().startsWith("http")) {
            // 使用默认的网页进行兜底
            throw new BizException(StatusEnum.UNEXPECT_ERROR, "请输入合法的url地址");
        }

        return (s) -> {
            return gatherAiAgent.gatherByAutoSplit(filePath);
        };
    }

    private Function<GatherReq, List<GatherOcDraftBo>> gatherByImg(GatherFileBo file) throws IOException {
        byte[] bytes;
        MimeType type;
        if (file == null) {
            // 使用默认的图片进行兜底
            Resource resource = new ClassPathResource("data/oc-img2.jpg");
            bytes = resource.getContentAsByteArray();
            type = MimeTypeUtils.IMAGE_JPEG;
        } else {
            bytes = file.bytes();
            type = MimeTypeUtils.parseMimeType(file.contentType());
        }
        return (s) -> {
            return gatherAiAgent.gatherByImgAutoSplit(type, bytes);
        };
    }

    private Pair<String, List<String>> parseContentsFromCsv(GatherFileBo file) throws IOException {
        byte[] bytes;
        if (file == null) {
            // 使用默认的图片进行兜底
            Resource resource = new ClassPathResource("data/oc.csv");
            bytes = resource.getContentAsByteArray();
        } else {
            bytes = file.bytes();
        }

        // 读取数据，进行拆分，用于多次与大模型交互；避免大模型返回结果截断
        String datas = new String(bytes, "utf-8");
        String[] lines = org.apache.commons.lang3.StringUtils.splitByWholeSeparator(datas, "\n");
        String title = lines[0];
        List<String> contents = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            contents.add(lines[i]);
        }
        return Pair.of(title, contents);
    }

    private Pair<String, List<String>> parseContentsFromExcel(GatherFileBo file) throws IOException {
        byte[] bytes;
        if (file == null) {
            // 使用默认的图片进行兜底
            Resource resource = new ClassPathResource("data/oc.xlsx");
            bytes = resource.getContentAsByteArray();
        } else {
            bytes = file.bytes();
        }

        final String[] title = new String[1];
        List<String> contents = new ArrayList<>();
        ExcelReader reader = FastExcel.read(new ByteArrayInputStream(bytes), new ReadListener<LinkedHashMap>() {
            @Override
            public void invokeHead(Map headMap, AnalysisContext context) {
                StringBuilder builder = new StringBuilder();
                for (Object entry : headMap.entrySet()) {
                    ReadCellData val = (ReadCellData) ((Map.Entry) entry).getValue();
                    builder.append(val.getStringValue());
                    builder.append(",");
                }
                title[0] = builder.toString();
            }

            @Override
            public void invoke(LinkedHashMap map, AnalysisContext analysisContext) {
                StringBuilder builder = new StringBuilder();
                for (Object entry : map.entrySet()) {
                    String val = (String) ((Map.Entry) entry).getValue();
                    builder.append(val);
                    builder.append(",");
                }
                contents.add(builder.toString());
            }

            @Override
            public void doAfterAllAnalysed(AnalysisContext analysisContext) {
            }
        }).build();
        reader.readAll();
        reader.close();

        return Pair.of(title[0], contents);
    }
}
