package com.git.hui.offer.util;

/**
 * @author YiHui
 * @date 2025/7/18
 */
public class FileTypeUtil {
    public static String getFileType(String fileType) {
        return switch (fileType.toLowerCase()) {
            case "webp" -> "image/webp";
            case "png" -> "image/png";
            case "jpg", "jpeg" -> "image/jpeg";
            case "gif" -> "image/gif";
            case "bmp" -> "image/bmp";
            case "csv" -> "text/csv";
            case "xls", "xlsx" -> "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            case "txt" -> "text/plain";
            case "json" -> "application/json";
            default -> "application/octet-stream"; // 默认二进制流
        };
    }

    public static String contentType2fileType(String contentType) {
        return switch (contentType.toLowerCase()) {
            case "image/webp" -> "webp";
            case "image/png" -> "png";
            case "image/jpeg" -> "jpg";
            case "image/gif" -> "gif";
            case "image/bmp" -> "bmp";
            case "text/csv" -> "csv";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet" -> "xlsx";
            case "text/plain" -> "txt";
            case "application/json" -> "json";
            default -> "unknow";
        };
    }
}
