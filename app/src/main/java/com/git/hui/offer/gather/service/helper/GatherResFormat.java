package com.git.hui.offer.gather.service.helper;

import java.util.List;

/**
 * 抓取的结果进行格式化处理
 *
 * @author YiHui
 * @date 2025/7/16
 */
public class GatherResFormat {

    /***
     * 处理大模型返回截断的数据
     * ```json
     * [
     *   {
     *     "companyName": "中兴终端",
     *     "companyType": "央国企",
     *     "jobLocation": "上海市,深圳市,西安市,成都市",
     *     "recruitmentType": "秋招提前批",
     *     "position": "AI算法工程师,软件开发工程师射频开发工程师,结构设计工程师",
     *     "deadline": "招满为止",
     *     "deliveryProgress": "未投递",
     *     "lastUpdatedTime": "2025-07-16",
     *     "jobAnnouncement": "https://mp.weixin.qq.com/s/WB8AzWth5B5ZChCFJPOdKw",
     *     "relatedLink": "https://job.zte.com.cn/cn/",
     *     "internalReferralCode": "-",
     *     "remarks": "-"
     *   },
     *   {
     *     "companyName": "昂瑞微",
     *     "companyType": "民企",
     *     "jobLocation": "北京大连上海深圳苏州",
     *     "recruitmentType": "秋招",
     *     "position": "管理培训生、嵌入式软件工程师、数字验证工程师、现场应用工程师、应用工程师、交付工程师、新产品导入工程师、客户质量工程、师产品质量工程师、质量体系工程师",
     *     "deadline": "招满为止",
     *     "deliveryProgress": "未投递",
     *     "lastUpdatedTime": "2025-07-16",
     *     "jobAnnouncement": "https://mp.weixin.qq.com/s/l2Lq53Wcn83VkIs
     * @param text
     * @return
     */
    public static List<String> extact(StringBuilder remain, String text) {
        if (text.startsWith("```json")) {
            text = text.substring("```json".length()).trim();
        } else if (text.startsWith("```")) {
            text = text.replaceAll("```", "").trim();
        }

        // 如果 remain 最后是 "， 下面接着的不是 { 开头，则表示需要向前补全,
        int len = remain.length();
        int index = remain.lastIndexOf("\"");
        if (index > 0 && index == len - 1) {
            // 已经是最后一个了
            if (!text.startsWith("{")) {
                remain.append(",");
            }
        }

        remain.append(text);
        return PartialJsonExtractor.extractCompleteElements(remain);
    }

}
