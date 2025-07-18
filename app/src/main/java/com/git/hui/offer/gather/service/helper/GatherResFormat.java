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
        // 干掉开头的标记
        if (text.startsWith("```json")) {
            text = text.substring("```json".length()).trim();
        } else if (text.startsWith("```")) {
            text = text.replaceAll("```", "").trim();
        }
        if (text.endsWith("```")) {
            // 干掉末尾的标记
            text = text.substring(0, text.length() - "```".length()).trim();
        }

        // 如果 remain 最后是 `",` 下面接着的不是 { 开头，则表示需要向前补全,
        final String FIELD_END_TAG = "\"";
        int len = remain.length();
        int index = remain.lastIndexOf(FIELD_END_TAG);
        if (index > 0 && index == len - FIELD_END_TAG.length()) {
            // 表示remain中的存储的被截断的数据，是从一个完整的json中间截取的，且是当前这个对象的最后一个键值对（因为后面没有跟着`,`， 如方法说明示例中的 `"remarks": "-"`）
            // 如果后面跟着的是 `{` 开头，则表示是一个新的json对象，我们可以将当前这个对象的结束符 `}` 添加到remain中，并返回
            // 如果后面跟着的不是 `{` 开头，则表示remain中最后这个键值对不是这个json对象的最后一个，我们需要补全 `,`，用于拼接后面的内容

            if (text.startsWith("{") || text.startsWith("[")) {
                remain.append("\n},");
            } else {
                // 当前键值对非json对象的最后一个成员，需要添加 `,`   用于添加后面的键值对
                remain.append(",");
            }
        }

        remain.append(text);
        return PartialJsonExtractor.extractCompleteElements(remain);
    }

    public static List<String> extact(String text) {
        StringBuilder builder = new StringBuilder(text);
        return extact(builder, "");
    }
}
