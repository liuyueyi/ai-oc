package com.git.hui.offer.util.net;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 * 请求工具类
 *
 * @author YiHui
 * @date 2023/04/23
 */
@Slf4j
public class HttpRequestHelper {

    /**
     * readData
     *
     * @param request request
     * @return result
     */
    // CHECKSTYLE:OFF:InnerAssignment
    public static String readReqData(HttpServletRequest request) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(request.getInputStream(), StandardCharsets.UTF_8));
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("请求参数解析异常! {}", request.getRequestURI(), e);
                }
            }
        }
    }
}