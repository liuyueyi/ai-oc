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
import com.git.hui.offer.gather.convert.Draft2EntityConvert;
import com.git.hui.offer.gather.model.GatherOcDraftBo;
import com.git.hui.offer.oc.service.OcService;
import com.git.hui.offer.util.json.JsonUtil;
import com.git.hui.offer.web.model.req.GatherReq;
import com.google.common.base.Joiner;
import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * @author YiHui
 * @date 2025/7/14
 */
@Slf4j
@Service
public class OfferGatherService {
    // 默认六行数据一组
    private static final Integer SPLIT_LEN = 6;

    private final GatherAiAgent gatherAiAgent;

    private final OcService gatherService;

    @Autowired
    public OfferGatherService(GatherAiAgent gatherAiAgent, OcService gatherService) {
        this.gatherAiAgent = gatherAiAgent;
        this.gatherService = gatherService;
    }

    public List<GatherOcDraftBo> gatherInfo(GatherReq req, MultipartFile file) throws IOException {
        Function<GatherReq, List<GatherOcDraftBo>> func = switch (req.type()) {
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
        log.info("返回结果是：{}", JsonUtil.toStr(list));
        gatherService.saveDraftDataList(Draft2EntityConvert.convert(list));
        return list;
    }

    /**
     * 传入文件进行数据提取的场景，我们直接读取文件，做好分页调用大模型，避免大模型返回数据截断的问题
     * 1. 支持传入 csv, excel； 要求第一行为标题，第二行开始为数据
     *
     * @return
     * @throws IOException
     */
    public List<GatherOcDraftBo> gatherFileInfo(GatherReq req, MultipartFile file) throws IOException {
        Pair<String, List<String>> pair;
        if (req.type() == GatherTargetTypeEnum.CSV_FILE) {
            pair = parseContentsFromCsv(file);
        } else if (req.type() == GatherTargetTypeEnum.EXCEL_FILE) {
            pair = parseContentsFromExcel(file);
        } else {
            throw new BizException(StatusEnum.UNEXPECT_ERROR, "不支持的文件类型");
        }

        List<GatherOcDraftBo> res = new ArrayList<>();
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
                log.info("返回结果是：{}", JsonUtil.toStr(list));
                gatherService.saveDraftDataList(Draft2EntityConvert.convert(list));
                res.addAll(list);
            } catch (Exception e) {
                log.error("解析失败，请检查大模型是否正常", e);
            }
        }

        return res;
    }

    /**
     * 直接根据用户传入的文本，进行解析获取职位信息
     *
     * @return
     */
    private Function<GatherReq, List<GatherOcDraftBo>> gatherByText(String txt) {
        String testText = StringUtils.isBlank(txt) ? ResourceUtil.readUtf8Str("data/oc-text.txt") : txt;
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
        try {
            // 做一个url的合法性判断
            URI uri = URI.create(filePath.trim());
            if (uri.getScheme() == null || !uri.getScheme().startsWith("http")) {
                // 使用默认的网页进行兜底
                throw new BizException(StatusEnum.UNEXPECT_ERROR, "请输入合法的url地址");
            }
        } catch (Exception e) {
            throw new BizException(StatusEnum.UNEXPECT_ERROR, "请输入合法的url地址");
        }

        return (s) -> {
            return gatherAiAgent.gatherByAutoSplit(filePath);
        };
    }

    private Function<GatherReq, List<GatherOcDraftBo>> gatherByImg(MultipartFile file) throws IOException {
        byte[] bytes;
        MimeType type;
        if (file == null || file.isEmpty()) {
            // 使用默认的图片进行兜底
            Resource resource = new ClassPathResource("data/oc-img.jpg");
            bytes = resource.getContentAsByteArray();
            type = MimeTypeUtils.IMAGE_JPEG;
        } else {
            bytes = file.getBytes();
            type = MimeTypeUtils.parseMimeType(file.getContentType());
        }
        return (s) -> {
            return gatherAiAgent.gatherByImg(type, bytes);
        };
    }

    private Pair<String, List<String>> parseContentsFromCsv(MultipartFile file) throws IOException {
        byte[] bytes;
        if (file == null || file.isEmpty()) {
            // 使用默认的图片进行兜底
            Resource resource = new ClassPathResource("data/oc.csv");
            bytes = resource.getContentAsByteArray();
        } else {
            bytes = file.getBytes();
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

    private Pair<String, List<String>> parseContentsFromExcel(MultipartFile file) throws IOException {
        byte[] bytes;
        if (file == null || file.isEmpty()) {
            // 使用默认的图片进行兜底
            Resource resource = new ClassPathResource("data/oc.xlsx");
            bytes = resource.getContentAsByteArray();
        } else {
            bytes = file.getBytes();
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
