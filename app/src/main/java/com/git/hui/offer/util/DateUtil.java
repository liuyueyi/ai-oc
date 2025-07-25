package com.git.hui.offer.util;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author YiHui
 * @date 2022/8/25
 */
public class DateUtil {
    public static final DateTimeFormatter UTC_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    public static final DateTimeFormatter DB_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
    public static final DateTimeFormatter DB_DAY_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter CH_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy年MM月dd日 HH:mm");

    public static final DateTimeFormatter CH_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy年MM月dd日");


    // 微信支付日期格式
    public static final DateTimeFormatter WX_PAY_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'+08:00'");


    /**
     * 一天对应的毫秒数
     */
    public static final Long ONE_DAY_MILL = 86400_000L;
    public static final Long ONE_DAY_SECONDS = 86400L;
    public static final Long ONE_MONTH_SECONDS = 31 * 86400L;


    public static final Long THREE_DAY_MILL = 3 * ONE_DAY_MILL;

    /**
     * 毫秒转日期
     *
     * @param timestamp
     * @return
     */
    public static String time2day(long timestamp) {
        return format(CH_TIME_FORMAT, timestamp);
    }

    public static String time2day(Timestamp timestamp) {
        return time2day(timestamp.getTime());
    }

    public static LocalDateTime time2LocalTime(long timestamp) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    public static String time2utc(long timestamp) {
        return format(UTC_FORMAT, timestamp);
    }

    public static String time2date(long timestamp) {
        return format(CH_DATE_FORMAT, timestamp);
    }

    public static String time2date(Timestamp timestamp) {
        return time2date(timestamp.getTime());
    }


    public static String format(DateTimeFormatter format, long timestamp) {
        LocalDateTime time = time2LocalTime(timestamp);
        return format.format(time);
    }

    /**
     * 微信的支付时间，转时间戳 "2018-06-08T10:34:56+08:00"
     *
     * @param day
     * @return
     */
    public static Long wxDayToTimestamp(String day) {
        LocalDateTime parse = LocalDateTime.parse(day, WX_PAY_FORMATTER);
        return LocalDateTime.from(parse).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }


    public static boolean skipDay(long last, long now) {
        last = last / ONE_DAY_MILL;
        now = now / ONE_DAY_MILL;
        return last != now;
    }

    // 2025-7-11的字符串转date
    public static Date toDateOrNow(String day) {
        try {
            // 2025-7-11 格式的转换
            LocalDate parse = LocalDate.parse(day, DB_DAY_FORMAT);
            return Date.from(parse.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
        } catch (Exception e) {
            // 中文日期格式的转换
            return chineseDate(day);
        }
    }

    private static Date chineseDate(String day) {
        int year;
        int startIndex = 0;
        int yearEndIndex = day.indexOf("年");
        if (yearEndIndex < 0) {
            year = LocalDate.now().getYear();
        } else {
            year = Integer.parseInt(day.substring(startIndex, yearEndIndex));
            startIndex = yearEndIndex + 1;
            if (year < 100) {
                year += 2000;
            }
        }


        int month;
        int monthEndIndex = day.indexOf("月");
        if (monthEndIndex < 0) {
            month = LocalDate.now().getMonthValue() + 1;
        } else {
            month = Integer.parseInt(day.substring(startIndex, monthEndIndex));
            startIndex = monthEndIndex + 1;
        }

        int d;
        int dEndIndex = day.indexOf("日");
        if (dEndIndex < 0) {
            d = LocalDate.now().getDayOfMonth();
        } else {
            d = Integer.parseInt(day.substring(startIndex, dEndIndex));
        }
        return Date.from(LocalDate.of(year, month, d).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }
}
