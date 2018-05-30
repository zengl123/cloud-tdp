package com.zenlin.cloud.tdp.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.sdk.domain.Pagination;
import com.zenlin.cloud.tdp.utils.RedisUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 描述:
 * 项目名:cloud-tdp-moudles
 *
 * @Author:ZENLIN
 * @Created 2017/12/26  16:30.
 */
@Component
public class EmAnalysisImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmAnalysisImpl.class);

    @Autowired
    private CloudQueryRunner runner;
    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    @Qualifier(value = "redis10Template")
    private RedisTemplate redisTemplate;


    /**
     * 每天各时段环境监测统计
     *
     * @param beginTime
     * @param endTime
     * @param codaSite
     * @return
     */
    public RestMessage day(String beginTime, String endTime, String codaSite) {
        return data(beginTime, endTime, codaSite, "day", Calendar.HOUR_OF_DAY, "yyyy-MM-dd HH");
    }

    public RestMessage week(String beginTime, String endTime, String codaSite) {
        Object object = redisUtils.getObject(redisTemplate, "week" + codaSite);
        RestMessage restMessage;
        if (null != object) {
            restMessage = (RestMessage) object;
        } else {
            restMessage = data(beginTime, endTime, codaSite, "week", Calendar.DAY_OF_MONTH, "yyyy-MM-dd");
            redisUtils.setObject(redisTemplate, "week" + codaSite, restMessage, null);
        }
        return restMessage;
    }

    public RestMessage year(String beginTime, String endTime, String codaSite) {
        Object object = redisUtils.getObject(redisTemplate, "year" + codaSite);
        RestMessage restMessage;
        if (null != object) {
            restMessage = (RestMessage) object;
        } else {
            restMessage = data(beginTime, endTime, codaSite, "year", Calendar.MONTH, "yyyy-MM");
            redisUtils.setObject(redisTemplate, "year" + codaSite, restMessage, null);
        }
        return restMessage;
    }

    public RestMessage data(String beginTime, String endTime, String codaSite, String type, int filed, String pattern) {
        List<String> betweenTime = getBetweenTime(beginTime, endTime, pattern, filed);
        List<Map> emMark = getEmMark(codaSite);
        Map<String, Object> mapData = getDataByTime(beginTime, endTime, codaSite, type);
        JSONArray legend = new JSONArray();
        JSONArray lineSeries = new JSONArray();
        JSONArray xAxis = new JSONArray();
        for (int i = 0; i < emMark.size(); i++) {
            Map<String, Object> map = emMark.get(i);
            String moinName = String.valueOf(map.get("moinName"));
            legend.add(moinName);
            JSONArray array = new JSONArray();
            for (int j = 0; j < betweenTime.size(); j++) {
                String time = "";
                switch (type) {
                    case "day":
                        time = betweenTime.get(j).split(" ")[1];
                        break;
                    case "week":
                        time = betweenTime.get(j);
                        break;
                    case "year":
                        time = betweenTime.get(j).split("-")[1];
                        break;
                    default:
                        break;
                }
                if (i == emMark.size() - 1) {
                    xAxis.add(time);
                }
                if (null == mapData.get(time + moinName)) {
                    array.add(0);
                } else {
                    Object codeValue = mapData.get(time + moinName);
                    array.add(codeValue);
                }
            }
            JSONObject object = new JSONObject();
            object.put("data", array);
            object.put("name", moinName);
            object.put("type", "line");
            lineSeries.add(object);
        }
        Map<String, Object> linkedMap = new LinkedHashMap<>();
        linkedMap.put("xAxis", xAxis);
        linkedMap.put("legend", legend);
        linkedMap.put("lineSeries", lineSeries);
        RestMessage restMessage = new RestMessage();
        restMessage.setData(linkedMap);
        restMessage.setErrCode(8200);
        return restMessage;
    }

    public Map<String, Object> getDataByTime(String beginTime, String endTime, String codaSite, String type) {
        List<Map> data = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        switch (type) {
            case "day":
                builder.append("SELECT DATE_FORMAT(coda_datetime,'%H') AS codaDateTime ,moin_name AS moinName,MAX(coda_value) AS codeValue FROM environment_monitoring AS T1 LEFT JOIN monitoringIndicators AS T2 ON T1.coda_Indicator=T2.id WHERE ")
                        .append(" coda_datetime >= '")
                        .append(beginTime)
                        .append("' AND coda_datetime < '")
                        .append(endTime).append("' AND coda_site = '")
                        .append(codaSite)
                        .append("' GROUP BY HOUR(coda_datetime),moin_name ORDER BY coda_datetime");
                break;
            case "week":
                builder.append("SELECT DATE_FORMAT(coda_datetime,'%Y-%m-%d') AS codaDateTime ,moin_name AS moinName,MAX(coda_value) AS codeValue FROM environment_monitoring AS T1 LEFT JOIN monitoringIndicators AS T2 ON T1.coda_Indicator=T2.id WHERE ")
                        .append(" coda_datetime >= '")
                        .append(beginTime)
                        .append("' AND coda_datetime < '")
                        .append(endTime).append("' AND coda_site = '")
                        .append(codaSite)
                        .append("' GROUP BY DAY(coda_datetime),moin_name ORDER BY coda_datetime");
                break;
            case "year":
                builder.append("SELECT DATE_FORMAT(coda_datetime,'%m') AS codaDateTime ,moin_name AS moinName,MAX(coda_value) AS codeValue FROM environment_monitoring AS T1 LEFT JOIN monitoringIndicators AS T2 ON T1.coda_Indicator=T2.id WHERE ")
                        .append(" coda_datetime >= '")
                        .append(beginTime)
                        .append("' AND coda_datetime < '")
                        .append(endTime).append("' AND coda_site = '")
                        .append(codaSite)
                        .append("' GROUP BY MONTH(coda_datetime),moin_name ORDER BY coda_datetime ASC");
                break;
            default:
                break;
        }
        Pagination<Map> execute = runner.sql(builder.toString(), 1, 1000);
        if (execute.getCount() > 0) {
            data = execute.getData();
        }
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < data.size(); i++) {
            String codaDateTime = data.get(i).get("codaDateTime").toString();
            Object codeValue = data.get(i).get("codeValue");
            Object moinName = data.get(i).get("moinName");
            map.put(codaDateTime + moinName, codeValue);
        }
        return map;
    }

    /**
     * 根据监控点获取该监控点的所有指标
     *
     * @param codaSite
     * @return
     */
    private List<Map> getEmMark(String codaSite) {
        StringBuilder builder = new StringBuilder();
        builder.append("SELECT moin_name AS moinName FROM monitoringindicators WHERE moin_site='")
                .append(codaSite)
                .append("' AND is_deleted='N'");
        List<Map> data = new ArrayList<>();
        try {
            Pagination<Map> execute = runner.sql(builder.toString(), 1, 1000);
            data = execute.getData();
        } catch (Exception e) {
            LOGGER.error("获取监控点指标异常;");
        }
        return data;
    }


    /**
     * 获取指点时间段内的天数
     *
     * @param begin
     * @param end
     * @return
     */
    private static List getBetweenTime(String begin, String end, String pattern, int filed) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        Date dBegin = null;
        Date dEnd = null;
        try {
            dBegin = sdf.parse(begin);
            dEnd = sdf.parse(end);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        List<String> list = new ArrayList<>();
        List<Date> dates = findDates(dBegin, dEnd, filed);
        for (Date date : dates) {
            list.add(sdf.format(date));
        }
        return list;
    }

    //JAVA获取某段时间内的所有日期
    public static List<Date> findDates(Date dBegin, Date dEnd, int filed) {
        List lDate = new ArrayList();
        lDate.add(dBegin);
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(filed, 1);
            lDate.add(calBegin.getTime());
        }
        return lDate;
    }
}
