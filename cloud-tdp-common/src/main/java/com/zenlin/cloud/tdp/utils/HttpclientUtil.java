package com.zenlin.cloud.tdp.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

/**
 * 描述:
 * 项目名:tdp-data-interface
 *
 * @Author:ZENLIN
 * @Created 2018/1/17  22:40.
 */
public class HttpclientUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpclientUtil.class);

    public static String httpGet(String getUrl) {
        String result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            URL url = new URL(getUrl);
            URI uri = new URI(url.getProtocol(), (url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort())), url.getPath(), url.getQuery(), null);
            HttpGet httpGet = new HttpGet(uri);
            CloseableHttpResponse response;
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (Exception e) {
            LOGGER.error("httpGet()异常,error=", e);
        }
        return result;
    }

    /**
     * @param getUrl
     * @param socketTimeout
     * @param connectTimeout
     * @return
     */
    public static String httpGet(String getUrl, int socketTimeout, int connectTimeout) {
        String result = null;
        CloseableHttpClient httpclient = HttpClients.createDefault();
        try {
            URL url = new URL(getUrl);
            URI uri = new URI(url.getProtocol(), (url.getHost() + (url.getPort() == -1 ? "" : ":" + url.getPort())), url.getPath(), url.getQuery(), null);
            HttpGet httpGet = new HttpGet(uri);
            //设置请求和传输超时时间
            RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(socketTimeout).setConnectTimeout(connectTimeout).build();
            httpGet.setConfig(requestConfig);
            CloseableHttpResponse response;
            response = httpclient.execute(httpGet);
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity);
        } catch (Exception e) {
            LOGGER.error("httpGet()异常,error=", e);
        }
        return result;
    }

    public static String httpPost(String url, JSONObject param) {
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String result = null;
        try {
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(new StringEntity(JSON.toJSONString(param), "utf-8"));
            httpPost.setHeader("Content-Type", "application/json");
            CloseableHttpResponse response;
            response = httpclient.execute(httpPost);
            HttpEntity entity = response.getEntity();
            //防止中文乱码，统一utf-8格式
            result = EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            LOGGER.error("httpPost()异常,error=", e);
        }
        return result;
    }
}
