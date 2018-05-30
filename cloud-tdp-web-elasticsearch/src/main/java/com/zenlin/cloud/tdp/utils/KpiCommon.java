package com.zenlin.cloud.tdp.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drore.cloud.sdk.common.security.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/10  9:51.
 */
public class KpiCommon {
    private static final Logger LOGGER = LoggerFactory.getLogger(KpiCommon.class);

    /**
     * kpi接口请求公共方法
     *
     * @param kpiId    KPI标识
     * @param spotId   景点标识
     * @param date     KPI时间
     * @param url      接口方法
     * @param username 用户名
     * @param password 密码
     * @return
     */
    public static JSONObject caseData(String kpiId, String spotId, String date, String url, String username, String password) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        MessageDigest md;
        try {
            md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            LOGGER.error("kpi 公共请求接口获取散列码异常,error={},e");
            return new JSONObject();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String createdate = sdf.format(new Date());
        md.update(("MgbcX81qJzPgjTCjtceMRUf0d" + createdate + password).getBytes());
        String pass = Base64.encode(md.digest());
        String authorization = "\"App_key\",Username=" + username + ",PasswordDigest=" + pass
                + ",Nonce=MgbcX81qJzPgjTCjtceMRUf0d,Created=" + createdate + "";
        HttpPost httpPost = new HttpPost(url);
        Map<String, Object> nvps = new HashMap<String, Object>();
        nvps.put("spot_id", spotId);
        nvps.put("kpi_id", kpiId);
        nvps.put("kpi_time", date);
        String nvpstr = JSON.toJSONString(nvps);
        httpPost.setEntity(new StringEntity(nvpstr, "utf-8"));
        httpPost.setHeader("Accept", "application/json");
        httpPost.setHeader("Content-Type", "application/json");
        httpPost.setHeader("Authorization", authorization);
        try {
            CloseableHttpResponse response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            String body = EntityUtils.toString(entity);
            JSONObject result = JSON.parseObject(body);
            EntityUtils.consume(entity);
            response.close();
            return result;
        } catch (IOException e) {
            return new JSONObject();
        }
    }
}
