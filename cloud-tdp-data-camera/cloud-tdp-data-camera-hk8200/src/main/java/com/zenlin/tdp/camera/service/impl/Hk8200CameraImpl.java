package com.zenlin.tdp.camera.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hikvision.artemis.sdk.ArtemisHttpUtil;
import com.zenlin.cloud.tdp.entity.camera.CameraDevice;
import com.zenlin.cloud.tdp.entity.camera.CameraGroup;
import com.zenlin.cloud.tdp.utils.QueryUtils;
import com.zenlin.tdp.camera.entity.Hk8200Param;
import com.zenlin.tdp.camera.service.Hk8200CameraService;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/4/10  11:21.
 */
@Service
public class Hk8200CameraImpl implements Hk8200CameraService {

    private static final Logger LOGGER = LoggerFactory.getLogger(Hk8200CameraImpl.class);
    /**
     * 分页获取组织树
     */
    private static final String FIND_CONTROL_UNIT_PAGE = "/artemis/api/common/v1/remoteControlUnitRestService/findControlUnitPage";
    /**
     * 根据组织编号分页获取监控点信息
     */
    private static final String FIND_CAMERA_INFO_PAGE_BY_TREE_NODE = "/artemis/api/common/v1/remoteControlUnitRestService/findCameraInfoPageByTreeNode";
    /**
     * 分页获取监控点信息
     */
    private static final String FIND_CAMERA_INFO_PAGE = "/artemis/api/common/v1/remoteCameraInfoRestService/findCameraInfoPage";

    @Autowired
    private QueryUtils queryUtils;
    @Autowired
    private ThreadPoolTaskExecutor executor;
    @Autowired
    private MongoTemplate mongoTemplate;
    /**
     * 接口地址
     */
    private String url;
    /**
     * 用户名
     */
    private String appKey;
    /**
     * 密钥
     */
    private String secret;
    /**
     * 成功状态码
     */
    private static final String CODE = "200";


    @Override
    public boolean syncCamera(JSONObject params) {
        if (!initParam(params)) {
            return false;
        }
        Map map = cameraGroupAndCameraDevice();
        if (map.isEmpty()) {
            return false;
        }
        boolean save = save(map);
        return save;
    }

    public boolean initParam(JSONObject params) {
        DeviceSpecification deviceSpecificationByParams = queryUtils.getDeviceSpecificationByParams(params);
        if (null != deviceSpecificationByParams) {
            String config = deviceSpecificationByParams.getConfig();
            Hk8200Param hk8200Param = JSON.parseObject(config, Hk8200Param.class);
            url = hk8200Param.getUrl();
            appKey = hk8200Param.getAppKey();
            secret = hk8200Param.getAppSecret();
            LOGGER.info("获取配置参数成功,url={},appKey={},secret={}", url, appKey, secret);
            return true;
        } else {
            LOGGER.info("参数有误,factoryModelName 不存在,factoryModelName={}", "HK8200_CAMERA");
            return false;
        }
    }

    /**
     * 获取监控分组列表
     *
     * @return
     */
    public List listCameraGroup() {
        Map<String, String> query = new HashMap<>(2);
        query.put("start", "0");
        query.put("size", "1000");
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", FIND_CONTROL_UNIT_PAGE);
            }
        };
        List list = new ArrayList<>();
        try {
            String doGetArtemis = ArtemisHttpUtil.doGetArtemis(path, query, null, null);
            if (StringUtils.isNotEmpty(doGetArtemis)) {
                JSONObject json = JSONObject.parseObject(doGetArtemis);
                String code = json.getString("code");
                String msg = json.getString("msg");
                if (CODE.equals(code)) {
                    //将array数组转换成字符串
                    String data = JSONObject.toJSONString(json.getJSONArray("data"), SerializerFeature.WriteClassName);
                    list = JSONObject.parseArray(data);
                } else {
                    LOGGER.error(msg);
                }
            } else {
                LOGGER.error("doGetArtemis={}", doGetArtemis);
            }
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("接口请求异常,error={}", e.getMessage());
        }
        return list;
    }

    public Map<String, Object> cameraGroupAndCameraDevice() {
        List<CameraDevice> cameraDevices = new ArrayList<>();
        List<CameraGroup> cameraGroups = new ArrayList<>();
        List list = listCameraGroup();
        Map<String, Object> map = new HashMap<>(2);
        if (CollectionUtils.isEmpty(list)) {
            LOGGER.error("获取监控组织失败,listCameraGroup={}", list);
            return map;
        }
        int size = list.size();
        CompletableFuture[] futures = new CompletableFuture[size];
        for (int i = 0; i < list.size(); i++) {
            JSONObject group = (JSONObject) list.get(i);
            //列表名称
            String name = group.getString("name");
            //列表编号
            String indexCode = group.getString("indexCode");
            futures[i] = CompletableFuture.runAsync(() -> {
                List cameras = findCameraInfoPageByTreeNode(indexCode);
                if (CollectionUtils.isNotEmpty(cameras)) {
                    CameraGroup cameraGroup = new CameraGroup();
                    cameraGroup.setCameraGroupNo(indexCode);
                    cameraGroup.setCameraGroupName(name);
                    cameraGroup.setSeries("HK8200");
                    cameraGroups.add(cameraGroup);
                }
                for (int j = 0; j < cameras.size(); j++) {
                    JSONObject camera = (JSONObject) cameras.get(j);
                    CameraDevice cameraDevice = new CameraDevice();
                    String cameraNameAndNetwork = camera.getString("name");
                    //因为接口没有返回ip地址所以添加设备时名称后面必须跟一个ip地址以“_"隔开,若不包含”_“视为无效数据
                    if (StringUtils.isEmpty(cameraNameAndNetwork) || !cameraNameAndNetwork.contains("_")) {
                        LOGGER.info("监控点：" + cameraNameAndNetwork + " 未配置ip,请先配置ip");
                        continue;
                    }
                    String cameraName = cameraNameAndNetwork.substring(0, cameraNameAndNetwork.lastIndexOf("_"));
                    String network = cameraNameAndNetwork.substring(cameraNameAndNetwork.lastIndexOf("_") + 1);
                    //上级设备编码
                    String parentIndexCode = camera.getString("parentIndexCode");
                    //在线状态
                    String isOnline = camera.getString("isOnline");
                    //设备编码
                    String cameraIndexCode = camera.getString("indexCode");
                    //设备编号
                    String cameraId = camera.getString("cameraId");
                    //通道号
                    Integer chanNum = camera.getInteger("chanNum");
                    cameraDevice.setDeviceName(cameraName);
                    cameraDevice.setDeviceChannelNo(chanNum);
                    cameraDevice.setDeviceGroupId(parentIndexCode);
                    cameraDevice.setDeviceId(cameraIndexCode);
                    cameraDevice.setDeviceId(cameraId);
                    cameraDevice.setIsOnline(isOnline);
                    cameraDevice.setDeviceIp(network);
                    cameraDevices.add(cameraDevice);
                }
            }, executor);
        }
        CompletableFuture.allOf(futures).join();
        map.put("cameraGroup", cameraGroups);
        map.put("cameraDevice", cameraDevices);
        return map;
    }

    /**
     * 根据组织编号分页获取监控点信息
     *
     * @return
     */
    private List findCameraInfoPageByTreeNode(String treeNode) {
        //get请求的查询参数
        Map<String, String> query = new HashMap<>(3);
        //第几页开始，起始值0
        query.put("start", "0");
        //每页大小
        query.put("size", "1000");
        //组织编号
        query.put("treeNode", treeNode);
        List list = findCamera(FIND_CAMERA_INFO_PAGE_BY_TREE_NODE, query);
        return list;
    }


    /**
     * 获取监控点信息共用方法
     *
     * @param url
     * @param query
     * @return
     */
    private List findCamera(final String url, Map<String, String> query) {
        //收集获取的监控点
        List list = new ArrayList<>();
        Map<String, String> path = new HashMap<String, String>(2) {
            {
                put("https://", url);
            }
        };
        String doGetArtemis = ArtemisHttpUtil.doGetArtemis(path, query, null, null);
        JSONObject json = JSONObject.parseObject(doGetArtemis);
        if (json == null) {
            return list;
        }
        String msg = json.getString("msg");
        String code = json.getString("code");
        if (CODE.equals(code)) {
            //将array数组转换成字符串
            String data = JSONObject.toJSONString(json.getJSONArray("data"), SerializerFeature.WriteClassName);
            list = JSONObject.parseArray(data);
        } else {
            LOGGER.info(msg);
        }
        return list;
    }

    public boolean save(Map<String, Object> map) {
        CameraGroup cameraGroup = (CameraGroup) map.get("cameraGroup");
        CameraDevice cameraDevice = (CameraDevice) map.get("cameraDevice");
        try {
            mongoTemplate.save(cameraGroup);
            mongoTemplate.save(cameraDevice);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*@Test
    public void test() {
        ArtemisConfig.host = "";
        ArtemisConfig.appKey = "";
        ArtemisConfig.appSecret = "";
        List list = listCameraGroup();
        List<CameraDevice> cameraDevices = new ArrayList<>();
        List<CameraGroup> cameraGroups = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            JSONObject group = (JSONObject) list.get(i);
            String name = group.getString("name");//列表名称
            String indexCode = group.getString("indexCode");//列表编号
            List cameras = findCameraInfoPageByTreeNode(indexCode);
            if (CollectionUtils.isNotEmpty(cameras)) {
                CameraGroup cameraGroup = new CameraGroup();
                cameraGroup.setCameraGroupNo(indexCode);
                cameraGroup.setCameraGroupName(name);
                cameraGroup.setSeries("HK8200");
                cameraGroups.add(cameraGroup);
            }
            for (int j = 0; j < cameras.size(); j++) {
                JSONObject jsonObject = (JSONObject) cameras.get(j);
                CameraDevice cameraDevice = new CameraDevice();
                String cameraNameAndNetwork = jsonObject.getString("name");
                if (StringUtils.isEmpty(cameraNameAndNetwork) || !cameraNameAndNetwork.contains("_")) {
                    LOGGER.info("监控点：" + cameraNameAndNetwork + " 未配置ip,请先配置ip");
                    continue;//因为接口没有返回ip地址所以添加设备时名称后面必须跟一个ip地址以“_"隔开,若不包含”_“视为无效数据
                }
                String cameraName = cameraNameAndNetwork.substring(0, cameraNameAndNetwork.lastIndexOf("_"));
                String network = cameraNameAndNetwork.substring(cameraNameAndNetwork.lastIndexOf("_") + 1);
                String parentIndexCode = jsonObject.getString("parentIndexCode");//
                String isOnline = jsonObject.getString("isOnline");
                String cameraIndexCode = jsonObject.getString("indexCode");
                String cameraId = jsonObject.getString("cameraId");
                //Integer chanNum = json.getInteger("chanNum");
                cameraDevice.setDeviceName(cameraName);
                //cameraDevice.setDeviceChannelNo(chanNum);
                cameraDevice.setDeviceGroupId(parentIndexCode);
                cameraDevice.setDeviceId(cameraIndexCode);
                cameraDevice.setDeviceId(cameraId);
                cameraDevice.setIsOnline(isOnline);
                cameraDevice.setDeviceIp(network);
                cameraDevices.add(cameraDevice);
            }
        }
        System.out.println("cameraGroups = " + cameraGroups);
        System.out.println("cameraDevices = " + cameraDevices);
    }*/
}
