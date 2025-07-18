package com.git.hui.offer.gather.service.helper;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PartialJsonExtractor {
    // 提取完整JSON对象并返回，剩余部分保留在StringBuilder中
    public static List<String> extractCompleteElements(StringBuilder jsonArrayBuilder) {
        List<String> completeElements = new ArrayList<>();
        int braceCount = 0;
        int startIndex = 0;
        boolean inString = false;
        char[] chars = jsonArrayBuilder.toString().toCharArray();

        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];

            // 处理字符串中的引号
            if (c == '"' && (i == 0 || chars[i - 1] != '\\')) {
                inString = !inString;
            }

            if (inString) {
                continue;
            }

            // 计算大括号嵌套层次
            if (c == '{') {
                if (braceCount == 0) {
                    startIndex = i;
                }
                braceCount++;
            } else if (c == '}') {
                braceCount--;
                if (braceCount == 0) {
                    // 找到完整的JSON对象
                    String jsonObject = jsonArrayBuilder.substring(startIndex, i + 1);
                    completeElements.add(jsonObject);

                    // 从StringBuilder中删除已提取的部分
                    jsonArrayBuilder.delete(0, i + 1);

                    // 重置索引和字符数组
                    i = -1;
                    chars = jsonArrayBuilder.toString().toCharArray();
                }
            }
        }

        // 处理剩余内容，移除不完整的属性
        removeIncompleteProperties(jsonArrayBuilder);

        return completeElements;
    }

    // 移除不完整的属性
    private static void removeIncompleteProperties(StringBuilder jsonBuilder) {
        // 正则表达式匹配不完整的属性: "key": value 格式，其中value不完整
        // 注意：这是一个简化的实现，实际JSON解析可能更复杂
        String regex = "\"[^\"]+\":\\s*[^,}\\]]*$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(jsonBuilder);

        if (matcher.find()) {
            int start = matcher.start();
            // 尝试找到属性前的逗号或开始的花括号
            int commaIndex = findPreviousCommaOrBrace(jsonBuilder, start);
            if (commaIndex >= 0) {
                jsonBuilder.delete(commaIndex, jsonBuilder.length());
            } else {
                // 如果找不到逗号，可能是第一个属性，直接删除整个不完整部分
                jsonBuilder.delete(start, jsonBuilder.length());
            }
        }

        // 清理尾部的逗号和空白字符
        cleanTrailingCharacters(jsonBuilder);
    }

    // 查找属性前的逗号或开始的花括号
    private static int findPreviousCommaOrBrace(StringBuilder jsonBuilder, int startIndex) {
        for (int i = startIndex - 1; i >= 0; i--) {
            char c = jsonBuilder.charAt(i);
            if (c == ',' || c == '{') {
                // 如果是逗号，删除逗号及其后的空格
                if (c == ',') {
                    return i;
                }
                // 如果是花括号，不删除花括号本身
                return i + 1;
            }
        }
        return -1;
    }

    // 清理尾部的逗号和空白字符
    private static void cleanTrailingCharacters(StringBuilder jsonBuilder) {
        while (!jsonBuilder.isEmpty()) {
            char lastChar = jsonBuilder.charAt(jsonBuilder.length() - 1);
            if (lastChar == ',' || Character.isWhitespace(lastChar)) {
                jsonBuilder.deleteCharAt(jsonBuilder.length() - 1);
            } else {
                break;
            }
        }
    }
}
