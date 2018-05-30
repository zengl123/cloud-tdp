package com.zenlin.cloud.tdp.webservice.client;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zenlin.cloud.tdp.entity.EsEntity;
import com.zenlin.cloud.tdp.entity.RestMessage;
import com.zenlin.cloud.tdp.entity.park.ParkBayonetRecord;
import com.zenlin.cloud.tdp.enums.CarType;
import com.zenlin.cloud.tdp.enums.IndexName;
import com.zenlin.cloud.tdp.enums.RedisKey;
import com.zenlin.cloud.tdp.enums.TypeName;
import com.zenlin.cloud.tdp.utils.EsUtils;
import com.zenlin.cloud.tdp.utils.RedisUtils;
import com.zenlin.cloud.tdp.utils.ResultUtil;
import com.zenlin.cloud.tdp.utils.XmlUtil;
import com.zenlin.cloud.tdp.webservice.server.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

import javax.xml.bind.JAXBElement;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * @author zhangz
 * @date 2018/1/23
 * Java调用HTTPS协议的webservice 说明
 * 1、根据InstallCert类 生成2个证书放在JDK的jre\lib\security路径下
 * 2、配置pom.xml文件  加入plugin   注意修改相应路径
 * <plugin>
 * <groupId>org.jvnet.jaxb2.maven2</groupId>
 * <artifactId>maven-jaxb2-plugin</artifactId>
 * <version>0.12.3</version>
 * <executions>
 * <execution>
 * <goals>
 * <goal>generate</goal>
 * </goals>
 * </execution>
 * </executions>
 * <configuration>
 * <schemaLanguage>WSDL</schemaLanguage>
 * <generatePackage>com.drore.cloud.tdp.gps.webservice</generatePackage>
 * <generateDirectory>${basedir}/src/main/java</generateDirectory>
 * <schemas>
 * <schema>
 * <fileset>
 * <directory>${basedir}/src/main/resources/</directory>
 * <includes>
 * <include>*.wsdl</include>
 * </includes>
 * </fileset>
 * </schema>
 * </schemas>
 * </configuration>
 * </plugin>
 * 3.IE浏览器打开wsdl路径  页面另存为WSDL文件，把生成的文件拷贝到resource（pom配置的路径）下
 * 4、运行maven install  会在pom配置的<generatePackage>路径下生成相应的类
 * 5、在本类中加入静态方法setDefaultHostnameVerifier 配置IP或域名
 * https://www.cnblogs.com/enenen/p/6238504.html
 */
@Component
public class WsClient extends WebServiceGatewaySupport {

    private static final Logger LOGGER = LoggerFactory.getLogger(WsClient.class);

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private EsUtils esUtils;
    @Autowired
    private ResultUtil resultUtil;

    private String url = "https://52.109.3.192/bms/services/ThirdBayonetService?wsdl";


    static {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((hostname, sslSession) -> {
            //域名或ip地址
            if (hostname.equals("52.109.3.192")) {
                return true;
            } else {
                return false;
            }
        });
        //第二个参数为证书的路径
        //System.setProperty("javax.net.ssl.trustStore", "C:\\Program Files\\Java\\jdk1.8.0_131\\jre\\lib\\security\\jssecacerts");
        //String property = System.getProperty("java.home") + "/lib/security/jssecacerts";
        String property = System.getProperty("java.home") + "\\lib\\security\\jssecacerts";
        System.setProperty("javax.net.ssl.trustStore", property);
        LOGGER.info("property={}", property);
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
    }

    /**
     * 登陆
     *
     * @return
     */
    public String login() {
        String xml = "<?xml  version=\"1.0\"  encoding=\"UTF-8\"?><loginParam><cmsUrl>52.109.3.192</cmsUrl><userName>admin</userName><passwd>Hik12345</passwd></loginParam>";
        InitSystem initSystem = new InitSystem();
        initSystem.setArg0(xml);
        JAXBElement<InitSystemResponse> xmlString = (JAXBElement<InitSystemResponse>) getWebServiceTemplate().marshalSendAndReceive(url, initSystem);
        System.out.println(xmlString.getValue().getReturn());
        String aReturn = xmlString.getValue().getReturn();
        JSONObject xml2json = XmlUtil.xml2json(aReturn).getJSONObject("root");
        Integer code = xml2json.getInteger("code");
        System.out.println("code = " + code);
        String sessionId = "";
        if (0 == code) {
            sessionId = xml2json.getString("sessionId");
        }
        return sessionId;
    }

    private int pageSize = 100;
    private int currentPage = 1;
    private String beginTime;
    private String endTime;

    List<ParkBayonetRecord> listResult;


    public List<ParkBayonetRecord> listParkData() {
        if (1 == currentPage) {
            beginTime = getBeginTime();
            if (StringUtils.isEmpty(beginTime)) {
                beginTime = "2018-05-23 00:00:00";
            }
            endTime = LocalDateTime.now().withNano(0).minusMinutes(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            listResult = new ArrayList();
        }
        do {
            StringBuilder builder = new StringBuilder();
            builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><vehicleQueryParam><sessionId>3ef8ca10-fc50-40ff-905a-1ce030de93b8</sessionId><searchType>1</searchType><passTime>")
                    .append(beginTime)
                    .append("_")
                    .append(endTime)
                    .append("</passTime><carSpeedRange>0,100</carSpeedRange><start>").append(currentPage).append("</start><limit>100</limit></vehicleQueryParam>");
            SearchVehicleInfo searchVehicleInfo = new SearchVehicleInfo();
            searchVehicleInfo.setArg0(builder.toString());
            JAXBElement<SearchVehicleInfoResponse> xmlString = (JAXBElement<SearchVehicleInfoResponse>) getWebServiceTemplate().marshalSendAndReceive("https://52.109.3.192/bms/services/ThirdBayonetService?wsdl", searchVehicleInfo);
            String aReturn = xmlString.getValue().getReturn();
            if (StringUtils.isEmpty(aReturn)) {
                LOGGER.error("aReturn is empty");
                return new ArrayList();
            }
            JSONObject xml2json = XmlUtil.xml2json(aReturn).getJSONObject("root");
            if (null == xml2json) {
                LOGGER.error("xml2json 转换失败");
                return new ArrayList();
            }
            int code = xml2json.getInteger("code").intValue();
            if (0 != code) {
                LOGGER.error("code is not 0");
                return new ArrayList();
            }
            System.out.println(xml2json);
            Integer totalNum = xml2json.getInteger("totalNum");
            if (currentPage == 1) {
                currentPage = totalNum / pageSize + 1;
            } else {
                currentPage--;
            }
            JSONObject vehiclePassInfos = xml2json.getJSONObject("vehiclePassInfos");
            JSONArray vehiclePassInfosJSONArray = vehiclePassInfos.getJSONArray("com.hikvision.kapu.modules.bayonet.vo.VehiclePassInfoVO");
            List<ParkBayonetRecord> list = new ArrayList<>();
            for (int i = 0; i < vehiclePassInfosJSONArray.size(); i++) {
                JSONObject vehiclePassInfo = vehiclePassInfosJSONArray.getJSONObject(i);
                String crossingIndexCode = vehiclePassInfo.getString("crossingIndexCode");
                String crossingName = vehiclePassInfo.getString("crossingName");
                String laneId = vehiclePassInfo.getString("laneId");
                String laneName = vehiclePassInfo.getString("laneName");
                String alarmAction = vehiclePassInfo.getString("alarmAction");
                String alarmActionName = vehiclePassInfo.getString("alarmActionName");
                String directionName = vehiclePassInfo.getString("directionName");
                String plateInfo = vehiclePassInfo.getString("plateInfo");
                if ("车牌".equals(plateInfo)) {
                    continue;
                }
                String plateType = vehiclePassInfo.getString("plateType");
                String vehicleType = vehiclePassInfo.getString("vehicleType");
                if (StringUtils.isNotEmpty(vehicleType)) {
                    vehicleType = CarType.getType(Integer.parseInt(vehicleType));
                }
                String passTime = vehiclePassInfo.getString("passTime").split("\\.")[0];
                String picVehicle = vehiclePassInfo.getString("picVehicle");
                String vehicleColor = vehiclePassInfo.getString("vehicleColor");
                String vehicleModel = vehiclePassInfo.getString("vehicleModel");
                String vehicleModelName = vehiclePassInfo.getString("vehicleModelName");
                String vehicleSublogo = vehiclePassInfo.getString("vehicleSublogo");
                String vehicleSublogoName = vehiclePassInfo.getString("vehicleSublogoName");
                ParkBayonetRecord vehicleRecord = new ParkBayonetRecord();
                vehicleRecord.setCrossingIndexCode(crossingIndexCode);
                vehicleRecord.setCrossingName(crossingName);
                vehicleRecord.setLaneId(laneId);
                vehicleRecord.setLaneName(laneName);
                vehicleRecord.setDirectionName(directionName);
                vehicleRecord.setAlarmAction(alarmAction);
                vehicleRecord.setAlarmActionName(alarmActionName);
                vehicleRecord.setPassTime(passTime);
                vehicleRecord.setPicVehicle(picVehicle);
                vehicleRecord.setPlateInfo(plateInfo);
                vehicleRecord.setPlateType(plateType);
                vehicleRecord.setVehicleType(vehicleType);
                vehicleRecord.setVehicleColor(vehicleColor);
                vehicleRecord.setVehicleModel(vehicleModel);
                vehicleRecord.setVehicleModelName(vehicleModelName);
                vehicleRecord.setVehicleLogo(vehicleSublogo);
                vehicleRecord.setVehicleSubLogoName(vehicleSublogoName);
                list.add(vehicleRecord);
            }
            listResult.addAll(list);
        } while (currentPage > 1);
        return listResult;
    }


    public void listParkPoint() {
        SearchCrossingInfo searchCrossingInfo = new SearchCrossingInfo();
        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <crossingInfo> <sessionId>3ef8ca10-fc50-40ff-905a-1ce030de93b8</sessionId> <controlindexCode></controlindexCode> </crossingInfo> ";
        searchCrossingInfo.setArg0(xml);
        JAXBElement<SearchCrossingInfoResponse> xmlString = (JAXBElement<SearchCrossingInfoResponse>) getWebServiceTemplate().marshalSendAndReceive("https://52.109.3.192/bms/services/ThirdBayonetService?wsdl", searchCrossingInfo);
        String aReturn = xmlString.getValue().getReturn();
        if (StringUtils.isEmpty(aReturn)) {
            return;
        }
        JSONObject xml2json = XmlUtil.xml2json(aReturn).getJSONObject("root");
        if (null == xml2json) {
            return;
        }
        int code = xml2json.getInteger("code").intValue();
        if (0 != code) {
            return;
        }
        LOGGER.info("卡口设备信息 data={}", xml2json);
    }

    /**
     * 获取卡口同步时间
     *
     * @return
     */
    public String getBeginTime() {
        String beginTime = redisUtils.getString(redisTemplate, RedisKey.PARK_BAYONET_RECORD_SYNC_TIME.getValue());
        if (StringUtils.isEmpty(beginTime)) {
            //从es查询
        }
        return beginTime;
    }

    public RestMessage saveData(List<ParkBayonetRecord> list) {
        EsEntity esEntity = new EsEntity();
        esEntity.setIndexName(IndexName.PARK_INDEX_NAME.getValue());
        esEntity.setType(TypeName.PARK_BAYONET_RECORD_TYPE_NAME.getValue());
        esEntity.setEntityList(list);
        try {
            return esUtils.saveEntityBatch(esEntity);
        } catch (IOException e) {
            LOGGER.error("海康卡口8200数据新增异常,error={}", e);
            return resultUtil.error("ERROR");
        }
    }

    public void syncParkBayonetRecord() {
        List<ParkBayonetRecord> parkBayonetRecords = listParkData();
        if (CollectionUtils.isNotEmpty(parkBayonetRecords)) {
            RestMessage restMessage = saveData(parkBayonetRecords);
            LOGGER.info(restMessage.getData().toString());
        } else {
            LOGGER.error("海康卡口8200数据同步失败");
        }
    }
}
