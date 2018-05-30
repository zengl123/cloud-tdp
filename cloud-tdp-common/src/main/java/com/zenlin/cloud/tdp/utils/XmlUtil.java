package com.zenlin.cloud.tdp.utils;

import com.alibaba.fastjson.JSONObject;
import org.json.XML;
/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/23  10:12.
 */
public class XmlUtil {
    /**
     * 将xml字符串转换成json对象
     *
     * @param xmlString
     * @return
     */
    public static JSONObject xml2json(String xmlString) {
        JSONObject object;
        try {
            object = JSONObject.parseObject(String.valueOf(XML.toJSONObject(xmlString)));
        } catch (Exception e) {
            e.printStackTrace();
            object = new JSONObject();
        }
        return object;
    }
}
