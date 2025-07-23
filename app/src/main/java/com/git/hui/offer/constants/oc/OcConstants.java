package com.git.hui.offer.constants.oc;

/**
 * @author YiHui
 * @date 2025/7/23
 */
public interface OcConstants {
    String APP = "oc";

    /**
     * 公司类型
     */
    String COMPANY_TYPE_KEY = "CompanyTypeEnum";

    /**
     * 招聘类型
     */
    String RECRUITMENT_TYPE_KEY = "RecruitmentTypeEnum";


    /**
     * 招聘对象
     */
    String RECRUITMENT_TARGET_KEY = "RecruitmentTargetEnum";

    String URL_PATTERN = "^(https?://)?([a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,}(:\\d+)?(/.*)?$";

    /**
     * url地址转换
     *
     * @param input
     * @return
     */
    static String urlCheck(String input) {
        if (input == null || input.isEmpty()) {
            return "";
        }

        // 使用正则表达式判断是否为 HTTP 链接
        // 支持：
        // 1. http:// 或 https:// 开头的链接
        // 2. 以域名形式（如 www.example.com 或 test.com）开头的链接
        if (input.matches(URL_PATTERN)) {
            // 如果缺少协议前缀，则添加 http:// 默认协议
            if (!input.startsWith("http://") && !input.startsWith("https://")) {
                return "http://" + input;
            }
            return input;
        } else {
            return "";
        }
    }

    static String textFormat(String text, String join) {
        if (text == null || text.isEmpty()) {
            return "";
        }

        // 判断是否包含英文字符
        boolean containsEnglish = text.matches(".*[a-zA-Z]+.*");

        if (containsEnglish) {
            // 如果包含英文字符，则以中文符号和英文符号作为分割符
            // 中文符号：， 。 ； ： ？ ！ “ ” ‘ ’ （ ） 【 】 《 》 、
            // 英文符号：, . ; : ? ! " ' ( ) [ ] < > / \ - _ + = * # @ $ % ^ & |
            String[] parts = text.split("[\\uFF0C\\u3002\\uFF1B\\uFF1A\\uFF1F\\uFF01\\u201C\\u201D\\u2018\\u2019\\uFF08\\uFF09\\u3010\\u3011\\u300A\\u300B\\u3001\\s,.;:?!\"'()\\[\\]<>\\\\/\\-_+=*#@\\$%\\^&|]+");
            // 过滤空字符串并用英文逗号拼接
            return String.join(join, java.util.Arrays.stream(parts)
                    .filter(part -> !part.isEmpty())
                    .toArray(String[]::new));
        } else {
            // 如果不包含英文字符，则以非中文字符进行分割
            String[] parts = text.split("[^\\u4e00-\\u9fa5]+");
            return String.join(join, parts);
        }
    }

    /**
     * 职位地点格式化
     *
     * @param position
     * @return
     */
    static String jobLocationFormat(String position) {
        return textFormat(position, ",");
    }

    /**
     * 职位格式化
     *
     * @param job
     * @return
     */
    static String positionFormat(String job) {
        return textFormat(job, "/");
    }

}
