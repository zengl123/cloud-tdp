package com.zenlin.cloud.tdp.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * 描述:日期时间工具类
 * 项目名:tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2018/1/17  19:07.
 */
@Component
public class DateTimeUtil {

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YYYY_MM_DD = "yyyy-MM-dd";

    private static final String SPACE_KEY = " ";


    /**
     * @param pattern 日期格式
     * @return
     */
    public static DateTimeFormatter dtf(String pattern) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern(pattern);
        return dtf;
    }

   /*TODO 基本转换*/

    /**
     * Date转String
     *
     * @param date    Wed May 30 14:22:21 CST 2018
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 2018-05-30 14:21:08
     */
    public static String DateToLocalDateTime(Date date, String pattern) {
        Instant instant = date.toInstant();
        ZoneId zone = ZoneId.systemDefault();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone).withNano(0);
        String format = localDateTime.format(dtf(pattern));
        return format;
    }

    /**
     * 将日期时间字符串转为毫秒数
     *
     * @param dateTimeString 日期时间字符串 2018-01-01 00:00:00 or 日期字符串 2018-01-01
     * @return 1514736000000 or 1514736000000
     */
    public static String stringToMilli(String dateTimeString) {
        if (dateTimeString.contains(SPACE_KEY)) {
            LocalDateTime localDateTime = LocalDateTime.parse(dateTimeString, dtf(YYYY_MM_DD_HH_MM_SS));
            long epochSecond = localDateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
            return String.valueOf(epochSecond);
        } else {
            LocalDate localDate = LocalDate.parse(dateTimeString, dtf(YYYY_MM_DD));
            long epochSecond = LocalDateToUDate(localDate).getTime();
            return String.valueOf(epochSecond);
        }
    }

    /**
     * LocalDate 转UDate
     *
     * @param localDate
     * @return
     */
    public static Date LocalDateToUDate(LocalDate localDate) {
        ZoneId zone = ZoneId.systemDefault();
        Instant instant = localDate.atStartOfDay().atZone(zone).toInstant();
        Date date = Date.from(instant);
        return date;
    }

    /**
     * 将毫秒转换为时间字符串
     *
     * @param ms
     * @param pattern
     * @return
     */
    public static String msToDateStr(String ms, String pattern) {
        Instant instant = Instant.ofEpochMilli(Long.valueOf(ms.trim()));
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(dtf(pattern));
    }

    /**
     * date转string
     *
     * @param date
     * @param pattern
     * @return
     */
    public static String dateToString(Date date, String pattern) {
        if (null == date) {
            return null;
        }
        if (StringUtils.isEmpty(pattern)) {
            pattern = YYYY_MM_DD_HH_MM_SS;
        }
        Instant instant = date.toInstant();
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        return dateTime.format(dtf(pattern));
    }

    /**
     * string转date
     *
     * @return
     */
    public static Date stringToDate(String time, String pattern) {
        if (StringUtils.isEmpty(time) || StringUtils.isEmpty(pattern)) {
            return null;
        }
        DateTimeFormatter dtf = dtf(pattern);
        Instant instant;
        if (time.contains(SPACE_KEY)) {
            LocalDateTime localDateTime = LocalDateTime.parse(time, dtf);
            instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        } else {
            time += " 00:00:00";
            LocalDateTime localDateTime = LocalDateTime.parse(time, dtf);
            instant = localDateTime.atZone(ZoneId.systemDefault()).toInstant();
        }
        return Date.from(instant);
    }

    /**
     * 获取今年第一天
     *
     * @param pattern yyyy-MM-dd||yyyy-MM-dd HH:mm:ss
     * @return 2018-01-01||2018-01-01 00:00:00
     */
    public static String firstDayTimeOfYear(String pattern) {
        LocalDateTime today;
        LocalDate localDate;
        String dateString;
        if (!pattern.contains(SPACE_KEY)) {
            localDate = LocalDate.now();
            LocalDate firstDayOfThisYear = localDate.with(TemporalAdjusters.firstDayOfYear());
            dateString = firstDayOfThisYear.format(dtf(pattern));
        } else {
            today = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
            LocalDateTime firstDayOfThisYear = today.with(TemporalAdjusters.firstDayOfYear());
            dateString = firstDayOfThisYear.format(dtf(pattern));
        }
        return dateString;
    }

    /**
     * 获取本月第一天
     *
     * @return 2018-01-01
     */
    public static String firstDayOfMonth() {
        LocalDate today = LocalDate.now();
        LocalDate firstDayOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth());
        return firstDayOfThisMonth.toString();
    }

    /**
     * 本月最后一天
     *
     * @return 2018-01-31
     */
    public static String lastDayOfMonth() {
        LocalDate today = LocalDate.now();
        LocalDate lastDayOfThisMonth = today.with(TemporalAdjusters.lastDayOfMonth());
        return lastDayOfThisMonth.toString();
    }

    /**
     * 比较时间字符串大小
     *
     * @param str1
     * @param str2
     * @return
     */
    public static boolean compareDate(String str1, String str2) {
        long longStr1 = Long.valueOf(str1.replaceAll("[-\\s:]", ""));
        long longStr2 = Long.valueOf(str2.replaceAll("[-\\s:]", ""));
        return longStr1 > longStr2;
    }

    /**
     * 获取指定时间time 前/后 --second秒 时间
     *
     * @param time    2018-01-01 00:00:00
     * @param second  1
     * @param pattern yyyy-MM-dd HH:mm:ss
     * @return 2018-01-01 00:00:01
     */
    public static String plusMinusSeconds(String time, Integer second, String pattern) {
        if (StringUtils.isEmpty(time) || null == second || StringUtils.isEmpty(pattern)) {
            return null;
        } else {
            DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = second > 0 ? LocalDateTime.parse(time, f).plusSeconds(second) : LocalDateTime.parse(time, f).minusSeconds(Math.abs(second));
            return localDateTime.format(f);
        }
    }

    /**
     * 获取指定时间time 前/后 --minute分钟 时间
     *
     * @param time
     * @param minute  分钟数
     * @param pattern 格式
     * @return
     */
    public static String plusMinusMinutes(String time, Integer minute, String pattern) {
        if (StringUtils.isEmpty(time) || null == minute || StringUtils.isEmpty(pattern)) {
            return null;
        } else {
            DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = minute > 0 ? LocalDateTime.parse(time, f).plusMinutes(minute) : LocalDateTime.parse(time, f).minusMinutes(Math.abs(minute));
            return localDateTime.format(f);
        }
    }

    /**
     * 获取指定时间time 前/后  --hour小时 时间
     *
     * @param time
     * @param hour    小时数
     * @param pattern 格式
     * @return
     */
    public static String plusMinusHours(String time, Integer hour, String pattern) {
        if (StringUtils.isEmpty(time) || null == hour || StringUtils.isEmpty(pattern)) {
            return null;
        } else {
            DateTimeFormatter f = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime localDateTime = hour > 0 ? LocalDateTime.parse(time, f).plusHours(hour) : LocalDateTime.parse(time, f).minusHours(Math.abs(hour));
            return localDateTime.format(f);
        }
    }

    /**
     * 获取当前整10分钟时间的前十分钟
     *
     * @param pattern 时间格式
     * @return
     */
    public static String getNowMinuteBefore10(String pattern) {
        LocalDateTime localDateTime = LocalDateTime.now().withNano(0);
        int minute = localDateTime.getMinute();
        //计算10的整数分钟
        minute = Math.round(minute / 10 * 10);
        String time = localDateTime.withMinute(minute).withSecond(0).minusMinutes(10).format(DateTimeUtil.dtf(pattern));
        return time;
    }

    /**
     * 获取一天指定时间段的各个时间
     *
     * @return
     */
    public static List<String> listHour(int begin, int end) {
        List<String> list = Arrays.asList("00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23");
        List<String> hours = new ArrayList<>();
        for (int i = begin; i <= end; i++) {
            hours.add(list.get(i));
        }
        return hours;
    }

    /**
     * 遍历获取两个日期之间天数集合
     *
     * @param startDate 格式yyyy-MM-dd
     * @param endDate   格式yyyy-MM-dd
     * @return 含头含尾
     */
    public static List<String> listDay(String startDate, String endDate) {
        List<String> dateList = new ArrayList<>();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        long d = ChronoUnit.DAYS.between(start, end);
        dateList.add(start.toString());
        for (long i = 1; i <= d; i++) {
            dateList.add(start.plusDays(i).toString());
        }
        return dateList;
    }

    /**
     * 遍历获取两个日期之间月份集合
     *
     * @param startDate 2018-01-01
     * @param endDate   2018-03-07
     * @return 含头含尾 [2018-01,2018-02,2018-03]
     */
    public static List<String> listMonth(String startDate, String endDate) {
        List<String> monthList = new ArrayList<>();
        LocalDate start = LocalDate.parse(startDate);
        LocalDate end = LocalDate.parse(endDate);
        long m = ChronoUnit.MONTHS.between(start, end);
        for (long i = 0; i <= m; i++) {
            String toString = start.plusMonths(i).toString();
            monthList.add(toString.substring(0, toString.lastIndexOf("-")));
        }
        return monthList;
    }
}
