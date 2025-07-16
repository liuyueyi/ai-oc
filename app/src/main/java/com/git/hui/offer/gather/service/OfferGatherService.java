package com.git.hui.offer.gather.service;

import cn.hutool.core.io.resource.ResourceUtil;
import com.git.hui.offer.components.bizexception.BizException;
import com.git.hui.offer.components.bizexception.StatusEnum;
import com.git.hui.offer.gather.convert.Draft2EntityConvert;
import com.git.hui.offer.gather.model.GatherOcDraftBo;
import com.git.hui.offer.oc.service.OcService;
import com.git.hui.offer.util.json.JsonUtil;
import com.git.hui.offer.web.model.req.GatherReq;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

/**
 * @author YiHui
 * @date 2025/7/14
 */
@Service
public class OfferGatherService {
    private static final Logger log = LoggerFactory.getLogger(OfferGatherService.class);

    private final GatherAiAgent gatherAiAgent;

    private final OcService gatherService;

    @Autowired
    public OfferGatherService(GatherAiAgent gatherAiAgent, OcService gatherService) {
        this.gatherAiAgent = gatherAiAgent;
        this.gatherService = gatherService;
    }

    public List<GatherOcDraftBo> gatherInfo(GatherReq req) {
        Function<GatherReq, List<GatherOcDraftBo>> func = switch (req.type()) {
            case TEXT -> gatherByText();
            case HTML_TEXT -> gatherByHtmlText(req.content());
            case HTTP_URL -> gatherByHttpUrl(req.content());
            default -> null;
        };
        if (func == null) {
            throw new BizException(StatusEnum.UNEXPECT_ERROR, "当前方式还未支持，敬请期待");
        }
        List<GatherOcDraftBo> list = func.apply(req);
        log.info("返回结果是：{}", JsonUtil.toStr(list));
        gatherService.saveDraftDataList(Draft2EntityConvert.convert(list));
        return list;
    }

    /**
     * 直接根据用户传入的文本，进行解析获取职位信息
     *
     * @return
     */
    private Function<GatherReq, List<GatherOcDraftBo>> gatherByText() {
        String testText = """
                公司名称	公司类型	工作地点	招聘类型	招聘对象	岗位(大都不限专业)	投递进度	更新时间	投递截止	相关链接	招聘公告	内推码	备注
                江苏智檬智能科技	民企	南京市	秋招提前批	2025和2026年毕业生	
                企宣助理,品牌运营,文化出海岗
                未投递	2025-07-14	招满为止	投递	公告	-	-
                中国有研科技	央国企	北京市,廊坊市,德州市,忻州市,青岛市上海市,合肥市,重庆市,新余市,	春招	2025年毕业生	
                研发工程师,工艺工程师,检测工程师,电气工程师,安全工程师,技工
                未投递	2025-07-14	招满为止	投递	公告	-	-
                东方航空食品	央国企	上海	春招	2025年毕业生	
                航食储备人才
                未投递	2025-07-14	招满为止	投递	公告	-	-
                星猿哲	外企	全国,国外	秋招提前批	2026年毕业生	
                硬件研发岗(机械、电气),系统研发岗,运动算法岗,视觉算法岗,仿真算法岗,软件测试岗,硬件测试岗
                未投递	2025-07-14	招满为止	投递	公告	-	-
                """;

        return (s) -> {
            return gatherAiAgent.gatherByText(testText);
        };
    }

    private Function<GatherReq, List<GatherOcDraftBo>> gatherByHtmlText(String text) {
        String testText = ResourceUtil.readUtf8Str("data/oc-html.txt");
        // fixme 需要考虑上下文长度溢出导致问题
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
}
