package com.git.hui.offer.util;


import cn.hutool.core.util.NumberUtil;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author YiHui
 * @date 2022/8/15
 */
public class CodeGenerateUtil {
    public static final Integer CODE_LEN = 4;

    private static final Random random = new Random();

    private static final List<String> specialCodes = Arrays.asList(
            "6666", "8888", "0000", "9999", "5555", "2222", "3333", "7777",
            "2345", "3456", "4567", "5678", "6789", "7890"
    );

    public static String genCode(int cnt) {
        if (cnt >= specialCodes.size()) {
            int num = random.nextInt(1000);
            if (num >= 100 && num <= 200) {
                // 100-200之间的数字作为关键词回复，不用于验证码
                return genCode(cnt);
            }
            return String.format("%0" + CODE_LEN + "d", num);
        } else {
            return specialCodes.get(cnt);
        }
    }

    public static boolean isVerifyCode(String content) {
        if (!NumberUtil.isNumber(content) || content.length() != CodeGenerateUtil.CODE_LEN) {
            return false;
        }

        int num = Integer.parseInt(content);
        return num < 1000 || num > 2000;
    }
}
