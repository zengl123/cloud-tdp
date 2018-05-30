package com.zenlin.cloud.tdp.service;

import com.drore.cloud.sdk.client.CloudQueryRunner;
import com.drore.cloud.sdk.common.resp.RestMessage;
import com.drore.cloud.sdk.domain.Pagination;
import com.zenlin.cloud.tdp.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * Created by zz on 2017/7/6.
 * 同里湿地环境监测统计分析
 */
@Service
public class EmServiceImpl {
    @Resource
    private CloudQueryRunner runner;

    public RestMessage MonthView(String coda_site) {
        RestMessage restMessage = new RestMessage();
        Map<String, String> datestr = DateTimeUtils.printsWeeks();
        List<Map<String, Object>> list = new ArrayList<>();
        for (int i = 1; i < 5; i++) {
            if (StringUtils.isNotEmpty(datestr.get(String.valueOf(i)).toString())) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("week", i);
                String sql = "select MAX(a.coda_value) coda_value ,b.moin_name from environment_monitoring a " +
                        " LEFT JOIN monitoringindicators b ON a.coda_Indicator=b.id " +
                        " where a.coda_site='" + coda_site + "' and " +
                        " a.coda_datetime  >= '" + datestr.get(String.valueOf(i)).toString().split(",")[0] + " 00:00:00'  AND coda_datetime < '" + datestr.get(String.valueOf(i)).toString().split(",")[1] + " 23:59:59' " +
                        "GROUP BY a.coda_Indicator";
                List<Map> resultlist;
                try {
                    Pagination<Map> execute = runner.sql(sql,1,1000);
                    if (execute.getCount() > 0) {
                        resultlist = execute.getData();
                    } else {
                        resultlist = new ArrayList<>();
                    }
                } catch (Exception e) {
                    resultlist = new ArrayList<>();
                }
                map.put("val", resultlist.size() == 0 ? 0 : resultlist);
                list.add(map);
            }
        }
        restMessage.setData(list);
        restMessage.setErrCode(8200);
        return restMessage;
    }
}
