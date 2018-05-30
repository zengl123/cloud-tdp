package com.zenlin.tdp.camera.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.entity.EsEntity;
import com.zenlin.cloud.tdp.entity.RestMessage;
import com.zenlin.cloud.tdp.entity.camera.CameraGroup;
import com.zenlin.cloud.tdp.utils.EsUtils;
import com.zenlin.cloud.tdp.utils.HttpclientUtil;
import com.zenlin.cloud.tdp.utils.ResultUtil;
import com.zenlin.tdp.camera.service.Hk8700CameraService;
import com.zenlin.tdp.camera.utils.Hk8700SignUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述:
 * 项目名:cloud-tdp
 *
 * @Author:ZENLIN
 * @Created 2018/5/17  11:08
 */
@Service
public class Hk8700CameraImpl implements Hk8700CameraService {

    /**
     * 方岩配置参数
     */
    private String host = "http://192.168.10.21";
    private String appKey = "fac30958";
    private String secret = "40b4809c4f204f80a9d81a288e5a4d8c";

    /**
     * 获取默认用户UUID的接口地址
     */
    private static final String GET_DEFAULT_USER_UUID = "/openapi/service/base/user/getDefaultUserUuid";
    /**
     * 获取子系统
     */
    private static final String GET_PLAT_SUB_SYSTEM = "/openapi/service/base/res/getPlatSubsytem";
    /**
     * 获取默认控制中心
     */
    private static final String GET_DEFAULT_UNIT = "/openapi/service/base/org/getDefaultUnit";
    /**
     * 根据中心UUID分页获取下级区域
     */
    private static final String GET_REGION_BY_UNIT_UUID = "/openapi/service/base/org/getRegionsByUnitUuid";
    /**
     * 分页获取编码设备
     */
    private static final String GET_ENCODER_DEVICES_EX = "/openapi/service/vss/res/getEncoderDevicesEx";
    /**
     * 根据区域UUID集分页获取监控点
     */
    private static final String GET_CAMERA_BY_REGION_UUID = "/openapi/service/vss/res/getCamerasByRegionUuids";
    /**
     * 分页获取监控点
     */
    private static final String GET_CAMERA = "/openapi/service/vss/res/getCameras";
    /**
     * 获取所有网域
     */
    private static final String GET_NET_ZONES = "/openapi/service/base/netZone/getNetZones";
    /**
     * 根据监控点UUID集和网域UUID分页获取录像计划
     */
    private static final String GET_RECORD_PLAN_BY_CAMERA_UUID = "/openapi/service/vss/playback/getRecordPlansByCameraUuids";
    /**
     * 根据录像计划UUID和网域UUID获取回放参数
     */
    private static final String GET_PLAY_BACK_PARAM_BY_PLAN_UUID = "/openapi/service/vss/playback/getPlaybackParamByPlanUuid";
    /**
     * 根据监控点UUID和网域UUID获取预览参数
     */
    private static final String GET_PREVIEW_PARAM_BY_CAMERA_UUID = "/openapi/service/vss/preview/getPreviewParamByCameraUuid";

    /**
     * 视频系统id
     */
    private final String SUB_SYSTEM_UUID = "2097152";
    @Autowired
    private EsUtils esUtils;
    @Autowired
    private ResultUtil resultUtil;

    private final String INDEX_NAME = "indexcameragroup";
    private final String TYPE = "camera_group";

    @Override
    public RestMessage syncCamera() {
        String userUuid = getDefaultUserUuid();
        JSONObject platSubsytem = getPlatSubsytem(userUuid);
        String subSystemCode = platSubsytem.getString("subSystemUuid");
        JSONObject defaultUnit = getDefaultUnit(userUuid, subSystemCode);
        String unitUuid = defaultUnit.getString("unitUuid");
        List<CameraGroup> regionsByUnitUuid = getRegionsByUnitUuid(userUuid, unitUuid, 1);
        RestMessage restMessage = saveOrUpdateCameraGroup(regionsByUnitUuid);
        System.out.println("restMessage = " + restMessage);
        return null;
    }

    /**
     * 获取默认用户UUID
     *
     * @return {"errorCode":0,"errorMessage":null,"data":"0be83d40695011e7981e0f190ed6d2e7"}
     */
    public String getDefaultUserUuid() {
        Long time = System.currentTimeMillis();
        String path = GET_DEFAULT_USER_UUID;
        JSONObject params = new JSONObject();
        params.put("appkey", appKey);
        params.put("time", time);
        String buildToken = Hk8700SignUtil.postBuildToken(host, path, params, secret);
        String httpPost = HttpclientUtil.httpPost(buildToken, params);
        JSONObject object = JSON.parseObject(httpPost);
        String data = object.getString("data");
        return data;
    }

    /**
     * 获取视频子系统(2097152)
     *
     * @param opUserUuid 0be83d40695011e7981e0f190ed6d2e7
     * @return {"errorCode":0,"errorMessage":null,"data":[{"subSystemUuid":"1048576","subSystemName":"基础应用"},{"subSystemUuid":"2097152","subSystemName":"视频"},{"subSystemUuid":"25165824","subSystemName":"人脸"},{"subSystemUuid":"26214400","subSystemName":"客流"},{"subSystemUuid":"27262976","subSystemName":"热度"},{"subSystemUuid":"28311552","subSystemName":"卡口"},{"subSystemUuid":"3145728","subSystemName":"门禁"},{"subSystemUuid":"5242880","subSystemName":"入侵报警"},{"subSystemUuid":"6291456","subSystemName":"停车场"},{"subSystemUuid":"9437184","subSystemName":"可视对讲"},{"subSystemUuid":"11534336","subSystemName":"考勤"},{"subSystemUuid":"10485760","subSystemName":"访客"},{"subSystemUuid":"4194304","subSystemName":"巡查"},{"subSystemUuid":"29360128","subSystemName":"车底检测"},{"subSystemUuid":"8388608","subSystemName":"消费"},{"subSystemUuid":"7340032","subSystemName":"梯控"},{"subSystemUuid":"20971520","subSystemName":"动环"},{"subSystemUuid":"14680064","subSystemName":"电子地图"},{"subSystemUuid":"12582912","subSystemName":"事件中心"},{"subSystemUuid":"13631488","subSystemName":"运维管理"}]}
     */
    public JSONObject getPlatSubsytem(String opUserUuid) {
        Long time = System.currentTimeMillis();
        String path = GET_PLAT_SUB_SYSTEM;
        JSONObject params = new JSONObject();
        params.put("appkey", appKey);
        params.put("time", time);
        params.put("opUserUuid", opUserUuid);
        String buildToken = Hk8700SignUtil.postBuildToken(host, path, params, secret);
        String httpPost = HttpclientUtil.httpPost(buildToken, params);
        JSONObject cameraSubSystem;
        if (StringUtils.isNotEmpty(httpPost)) {
            JSONObject jsonObject = JSON.parseObject(httpPost);
            JSONArray data = jsonObject.getJSONArray("data");
            cameraSubSystem = data.stream().map(object -> {
                JSONObject result = JSON.parseObject(JSON.toJSONString(object));
                String subSystemUuid = result.getString("subSystemUuid");
                if (SUB_SYSTEM_UUID.equals(subSystemUuid)) {
                    return result;
                } else {
                    return new JSONObject();
                }
            }).filter(result -> !result.isEmpty()).collect(Collectors.toList()).get(0);
        } else {
            cameraSubSystem = new JSONObject();
        }
        return cameraSubSystem;
    }

    /**
     * 获取默认控制中心
     *
     * @param opUserUuid    0be83d40695011e7981e0f190ed6d2e7
     * @param subSystemCode 2097152
     * @return {"errorCode":0,"errorMessage":null,"data":{"unitUuid":"1048576","name":"监控","parentUuid":null,"isParent":1,"createTime":1513324322749,"updateTime":1516583614500,"remark":""}}
     */
    public JSONObject getDefaultUnit(String opUserUuid, String subSystemCode) {
        Long time = System.currentTimeMillis();
        String path = GET_DEFAULT_UNIT;
        JSONObject params = new JSONObject();
        params.put("appkey", appKey);
        params.put("time", time);
        params.put("opUserUuid", opUserUuid);
        params.put("subSystemCode", subSystemCode);
        String buildToken = Hk8700SignUtil.postBuildToken(host, path, params, secret);
        String httpPost = HttpclientUtil.httpPost(buildToken, params);
        JSONObject data;
        if (StringUtils.isNotEmpty(httpPost)) {
            JSONObject object = JSON.parseObject(httpPost);
            data = object.getJSONObject("data");
        } else {
            data = new JSONObject();
        }
        return data;
    }

    /**
     * 根据中心UUID分页获取下级区域
     *
     * @param opUserUuid  0be83d40695011e7981e0f190ed6d2e7
     * @param parentUuid  1048576
     * @param allChildren 0-直接子中心||1-下级所有子区域
     * @return 0-{"errorCode":0,"errorMessage":null,"data":{"total":22,"pageNo":1,"pageSize":400,"list":[{"regionUuid":"45e2d921e2094c59b76bd496dcda239b","name":"前山监控","parentUuid":"1048576","parentNodeType":1,"isParent":1,"createTime":1513324493820,"updateTime":1513399530115,"remark":""},{"regionUuid":"586d5867f2ed4db894d5ca2a40607c94","name":"后山监控","parentUuid":"1048576","parentNodeType":1,"isParent":1,"createTime":1513398868319,"updateTime":1513398868319,"remark":""},{"regionUuid":"50dc82a481b64344858c5075444f35c3","name":"外围监控","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1513399555770,"updateTime":1513399555770,"remark":""},{"regionUuid":"e5e53ae767c64a0e964ef66cbc43b8e1","name":"方岩监控","parentUuid":"1048576","parentNodeType":1,"isParent":1,"createTime":1513399585783,"updateTime":1513399585783,"remark":""},{"regionUuid":"a7066cd5422e4c8c9f4319fd3181bdb6","name":"南岩监控","parentUuid":"1048576","parentNodeType":1,"isParent":1,"createTime":1513399615312,"updateTime":1513399615312,"remark":""},{"regionUuid":"d3c3d2b774d64111bce679b0dfb6e01d","name":"石鼓寮监控","parentUuid":"1048576","parentNodeType":1,"isParent":1,"createTime":1513399640462,"updateTime":1525152395106,"remark":""},{"regionUuid":"0ae4a873134b46728b05a4bc5a3e0ff6","name":"电脑高清信号","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1513409777233,"updateTime":1513409777233,"remark":""},{"regionUuid":"a8aa3663af4e4142a4db83839a392bb6","name":"五峰书院","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1513496459718,"updateTime":1513496459718,"remark":""},{"regionUuid":"8f13b9681ab64c3d9341f1872f6bcace","name":"前山特殊设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514021731840,"updateTime":1514021731840,"remark":""},{"regionUuid":"7e0beb0652d84df382fcdba264ad7a76","name":"后山特殊设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514021773437,"updateTime":1514021773437,"remark":""},{"regionUuid":"ecdfb4cdbc3f4974931ad00f13296e0c","name":"石鼓寮特殊设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514021795765,"updateTime":1514021795765,"remark":""},{"regionUuid":"82c082d97dff44d7ab386e3c0555bf1f","name":"南岩特殊设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514021809627,"updateTime":1514021809627,"remark":""},{"regionUuid":"5a7677559b274810af8316a7b203e1d5","name":"方岩特殊设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514021828688,"updateTime":1514021828688,"remark":""},{"regionUuid":"e9481f49499d4cb1b34232ac76b2c90f","name":"五峰特殊设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514021851717,"updateTime":1514021851717,"remark":""},{"regionUuid":"df75123868d2440b9976a16af886b80e","name":"车载设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514191127087,"updateTime":1514191127087,"remark":""},{"regionUuid":"30d968d4011e4821aca51c931bbb79fe","name":"人员定位设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514211683414,"updateTime":1514211683414,"remark":""},{"regionUuid":"9dcccf13c5324051ad24d51133124856","name":"SOS设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1515125443367,"updateTime":1515125443367,"remark":""},{"regionUuid":"e918dc827dee47c5953814f41c5b292b","name":"周界防范","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1517829492013,"updateTime":1517829492013,"remark":""},{"regionUuid":"a5afbb13697b410cb9bd5d338dfe8260","name":"橙鹿停车场","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1517995493914,"updateTime":1517995493914,"remark":""},{"regionUuid":"d3b547969ee04def9de9ff1297a9fcff","name":"岩下停车场","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1517995512610,"updateTime":1517995512610,"remark":""},{"regionUuid":"e27bcdf949fc4139a7d3a10fb5844718","name":"悬空栈道","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1518073278637,"updateTime":1518073278637,"remark":""},{"regionUuid":"cbfda7fbb40645f7a2a3ffe36424eeec","name":"各分机房监控","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1521427007044,"updateTime":1521427007044,"remark":""}]}}
     * 1-{"errorCode":0,"errorMessage":null,"data":{"total":38,"pageNo":1,"pageSize":400,"list":[{"regionUuid":"45e2d921e2094c59b76bd496dcda239b","name":"前山监控","parentUuid":"1048576","parentNodeType":1,"isParent":1,"createTime":1513324493820,"updateTime":1513399530115,"remark":""},{"regionUuid":"586d5867f2ed4db894d5ca2a40607c94","name":"后山监控","parentUuid":"1048576","parentNodeType":1,"isParent":1,"createTime":1513398868319,"updateTime":1513398868319,"remark":""},{"regionUuid":"50dc82a481b64344858c5075444f35c3","name":"外围监控","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1513399555770,"updateTime":1513399555770,"remark":""},{"regionUuid":"e5e53ae767c64a0e964ef66cbc43b8e1","name":"方岩监控","parentUuid":"1048576","parentNodeType":1,"isParent":1,"createTime":1513399585783,"updateTime":1513399585783,"remark":""},{"regionUuid":"a7066cd5422e4c8c9f4319fd3181bdb6","name":"南岩监控","parentUuid":"1048576","parentNodeType":1,"isParent":1,"createTime":1513399615312,"updateTime":1513399615312,"remark":""},{"regionUuid":"d3c3d2b774d64111bce679b0dfb6e01d","name":"石鼓寮监控","parentUuid":"1048576","parentNodeType":1,"isParent":1,"createTime":1513399640462,"updateTime":1525152395106,"remark":""},{"regionUuid":"0ae4a873134b46728b05a4bc5a3e0ff6","name":"电脑高清信号","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1513409777233,"updateTime":1513409777233,"remark":""},{"regionUuid":"a8aa3663af4e4142a4db83839a392bb6","name":"五峰书院","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1513496459718,"updateTime":1513496459718,"remark":""},{"regionUuid":"8f13b9681ab64c3d9341f1872f6bcace","name":"前山特殊设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514021731840,"updateTime":1514021731840,"remark":""},{"regionUuid":"7e0beb0652d84df382fcdba264ad7a76","name":"后山特殊设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514021773437,"updateTime":1514021773437,"remark":""},{"regionUuid":"ecdfb4cdbc3f4974931ad00f13296e0c","name":"石鼓寮特殊设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514021795765,"updateTime":1514021795765,"remark":""},{"regionUuid":"82c082d97dff44d7ab386e3c0555bf1f","name":"南岩特殊设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514021809627,"updateTime":1514021809627,"remark":""},{"regionUuid":"5a7677559b274810af8316a7b203e1d5","name":"方岩特殊设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514021828688,"updateTime":1514021828688,"remark":""},{"regionUuid":"e9481f49499d4cb1b34232ac76b2c90f","name":"五峰特殊设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514021851717,"updateTime":1514021851717,"remark":""},{"regionUuid":"df75123868d2440b9976a16af886b80e","name":"车载设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514191127087,"updateTime":1514191127087,"remark":""},{"regionUuid":"30d968d4011e4821aca51c931bbb79fe","name":"人员定位设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1514211683414,"updateTime":1514211683414,"remark":""},{"regionUuid":"9dcccf13c5324051ad24d51133124856","name":"SOS设备","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1515125443367,"updateTime":1515125443367,"remark":""},{"regionUuid":"e918dc827dee47c5953814f41c5b292b","name":"周界防范","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1517829492013,"updateTime":1517829492013,"remark":""},{"regionUuid":"a5afbb13697b410cb9bd5d338dfe8260","name":"橙鹿停车场","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1517995493914,"updateTime":1517995493914,"remark":""},{"regionUuid":"d3b547969ee04def9de9ff1297a9fcff","name":"岩下停车场","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1517995512610,"updateTime":1517995512610,"remark":""},{"regionUuid":"e27bcdf949fc4139a7d3a10fb5844718","name":"悬空栈道","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1518073278637,"updateTime":1518073278637,"remark":""},{"regionUuid":"cbfda7fbb40645f7a2a3ffe36424eeec","name":"各分机房监控","parentUuid":"1048576","parentNodeType":1,"isParent":0,"createTime":1521427007044,"updateTime":1521427007044,"remark":""},{"regionUuid":"9e134e4159ba4e5eb45d609aaf7a21e9","name":"检票","parentUuid":"45e2d921e2094c59b76bd496dcda239b","parentNodeType":2,"isParent":0,"createTime":1525152177324,"updateTime":1525152177324,"remark":""},{"regionUuid":"52f0951789de4dfcbb8b5266ff0e3368","name":"售票","parentUuid":"45e2d921e2094c59b76bd496dcda239b","parentNodeType":2,"isParent":0,"createTime":1525152183746,"updateTime":1525152183746,"remark":""},{"regionUuid":"0f6bea8afc604142bad6d3903c6a3246","name":"检票","parentUuid":"586d5867f2ed4db894d5ca2a40607c94","parentNodeType":2,"isParent":0,"createTime":1525152363366,"updateTime":1525152363366,"remark":""},{"regionUuid":"89d8fbd0cc844a39bf1076f4103e8774","name":"售票","parentUuid":"586d5867f2ed4db894d5ca2a40607c94","parentNodeType":2,"isParent":0,"createTime":1525152369154,"updateTime":1525152369154,"remark":""},{"regionUuid":"fbc5591563da4196b5da49de471b3e7f","name":"检票","parentUuid":"d3c3d2b774d64111bce679b0dfb6e01d","parentNodeType":2,"isParent":0,"createTime":1525152379682,"updateTime":1525152379682,"remark":""},{"regionUuid":"24e820a3326b4856985bf5cea1114338","name":"售票","parentUuid":"d3c3d2b774d64111bce679b0dfb6e01d","parentNodeType":2,"isParent":0,"createTime":1525152405185,"updateTime":1525152405185,"remark":""},{"regionUuid":"9acbe5c92151404ca4e9df86f16b3c11","name":"前山","parentUuid":"45e2d921e2094c59b76bd496dcda239b","parentNodeType":2,"isParent":0,"createTime":1525152429504,"updateTime":1525152429504,"remark":""},{"regionUuid":"1ec78b4ba86f418a9e33ac143db1754c","name":"后山","parentUuid":"586d5867f2ed4db894d5ca2a40607c94","parentNodeType":2,"isParent":0,"createTime":1525152436750,"updateTime":1525152436750,"remark":""},{"regionUuid":"d787cf617f4248d8891f5f6372e7383c","name":"石鼓寮","parentUuid":"d3c3d2b774d64111bce679b0dfb6e01d","parentNodeType":2,"isParent":0,"createTime":1525152447169,"updateTime":1525152447169,"remark":""},{"regionUuid":"6c5d64c666da4cce89b535f190152b28","name":"胡公祠","parentUuid":"e5e53ae767c64a0e964ef66cbc43b8e1","parentNodeType":2,"isParent":0,"createTime":1526260463317,"updateTime":1526260463317,"remark":""},{"regionUuid":"74904756c7a74b38917be7dcfa700938","name":"其他监控","parentUuid":"e5e53ae767c64a0e964ef66cbc43b8e1","parentNodeType":2,"isParent":0,"createTime":1526260657645,"updateTime":1526261561094,"remark":""},{"regionUuid":"466ae46f82294c15b67a914af881d2f8","name":"上山景点","parentUuid":"e5e53ae767c64a0e964ef66cbc43b8e1","parentNodeType":2,"isParent":0,"createTime":1526260754974,"updateTime":1526260754974,"remark":""},{"regionUuid":"468f599dd84e4ec8865ab3f6356c4ccd","name":"服务中心","parentUuid":"d3c3d2b774d64111bce679b0dfb6e01d","parentNodeType":2,"isParent":0,"createTime":1526261910838,"updateTime":1526261963152,"remark":""},{"regionUuid":"c68a92747cae441dbd93279c86f07e2e","name":"影视基地","parentUuid":"d3c3d2b774d64111bce679b0dfb6e01d","parentNodeType":2,"isParent":0,"createTime":1526354012371,"updateTime":1526354012371,"remark":""},{"regionUuid":"47f189d51c0c437797b48a24719626e7","name":"平安殿","parentUuid":"a7066cd5422e4c8c9f4319fd3181bdb6","parentNodeType":2,"isParent":0,"createTime":1526354142200,"updateTime":1526358537672,"remark":""},{"regionUuid":"1c16c7f49de7470da74d84071436928f","name":"南岩","parentUuid":"a7066cd5422e4c8c9f4319fd3181bdb6","parentNodeType":2,"isParent":0,"createTime":1526358544092,"updateTime":1526358544092,"remark":""}]}}
     */
    public List<CameraGroup> getRegionsByUnitUuid(String opUserUuid, String parentUuid, Integer allChildren) {
        Long time = System.currentTimeMillis();
        String path = GET_REGION_BY_UNIT_UUID;
        JSONObject params = new JSONObject();
        params.put("appkey", appKey);
        params.put("time", time);
        params.put("opUserUuid", opUserUuid);
        params.put("pageNo", 1);
        params.put("pageSize", 400);
        params.put("parentUuid", parentUuid);
        params.put("allChildren", allChildren);
        String buildToken = Hk8700SignUtil.postBuildToken(host, path, params, secret);
        String httpPost = HttpclientUtil.httpPost(buildToken, params);
        List<CameraGroup> listResult = new ArrayList<>();
        if (StringUtils.isNotEmpty(httpPost)) {
            JSONObject object = JSON.parseObject(httpPost);
            JSONObject data = object.getJSONObject("data");
            JSONArray list = data.getJSONArray("list");
            for (Object o : list) {
                CameraGroup cameraGroup = new CameraGroup();
                JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(o));
                String regionUuid = jsonObject.getString("regionUuid");
                String name = jsonObject.getString("name");
                String parentNodeType = jsonObject.getString("parentUuid");
                cameraGroup.setSeries("HK8700");
                cameraGroup.setCameraGroupNo(regionUuid);
                cameraGroup.setCameraGroupName(name);
                cameraGroup.setCameraGroupParentNo(parentNodeType);
                listResult.add(cameraGroup);
            }
        }
        return listResult;
    }

    /**
     * 保存监控列表
     *
     * @param cameraGroups
     * @return
     */
    public RestMessage saveOrUpdateCameraGroup(List<CameraGroup> cameraGroups) {
        List<CameraGroup> batchAdd = new ArrayList<>();
        List<CameraGroup> batchUpdate = new ArrayList<>();
        try {
            for (CameraGroup cameraGroup : cameraGroups) {
                List<CameraGroup> list = esUtils.findEntity("cameraGroupNo", cameraGroup.getCameraGroupNo(), INDEX_NAME, TYPE, CameraGroup.class);
                if (CollectionUtils.isNotEmpty(list)) {
                    batchUpdate.add(cameraGroup);
                } else {
                    cameraGroup.setCreateTime(LocalDateTime.now().withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    cameraGroup.setDefaultTime(System.currentTimeMillis());
                    batchAdd.add(cameraGroup);
                }
            }
            RestMessage restMessage;
            Boolean flagAdd = true;
            Boolean flagUpdate = true;
            StringBuilder builder = new StringBuilder();
            if (CollectionUtils.isNotEmpty(batchAdd)) {
                EsEntity esEntity = new EsEntity();
                esEntity.setIndexName(INDEX_NAME);
                esEntity.setType(TYPE);
                esEntity.setEntityList(batchAdd);
                restMessage = esUtils.saveEntityBatch(esEntity);
                boolean success = restMessage.isSuccess();
                if (success) {
                    builder.append("新增监控成功;");
                } else {
                    flagAdd = false;
                    builder.append("新增监控失败");
                }
            } else {
                builder.append("未新增监控列表;");
            }
            if (CollectionUtils.isNotEmpty(batchUpdate)) {
                EsEntity esEntity = new EsEntity();
                esEntity.setIndexName(INDEX_NAME);
                esEntity.setType(TYPE);
                esEntity.setEntityList(batchUpdate);
                restMessage = esUtils.updateEntityBatch(esEntity);
                boolean success = restMessage.isSuccess();
                if (success) {
                    builder.append("更新监控成功;");
                } else {
                    flagUpdate = false;
                    builder.append("更新监控失败");
                }
            } else {
                builder.append("未更新监控列表;");
            }
            if (flagAdd && flagUpdate) {
                return resultUtil.success(flagAdd && flagUpdate, builder.toString());
            } else {
                return resultUtil.error(builder.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
            return resultUtil.error("ERROR");
        }
    }
}
