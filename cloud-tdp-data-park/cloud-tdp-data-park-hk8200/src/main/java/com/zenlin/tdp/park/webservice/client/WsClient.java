package com.zenlin.tdp.park.webservice.client;

import com.zenlin.cloud.tdp.utils.InstallCertUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.ws.client.core.support.WebServiceGatewaySupport;

/**
 * Created by zhangz on 2018/1/23.
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

    private String url;

    public static void main(String[] args) {
        try {
            InstallCertUtil.installCert("jssecacerts");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


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
        //String property = System.getProperty("java.home") + "/lib/security/jssecacerts";//linux
        String property = System.getProperty("java.home") + "\\lib\\security\\jssecacerts";//windows
        System.setProperty("javax.net.ssl.trustStore", property);
        LOGGER.info("property={}", property);
        System.setProperty("javax.net.ssl.trustStorePassword", "changeit");
    }

//    /**
//     * 登陆
//     *
//     * @return
//     */
//    public String login() {
//        String xml = "<?xml  version=\"1.0\"  encoding=\"UTF-8\"?><loginParam><cmsUrl>52.109.3.192</cmsUrl><userName>admin</userName><passwd>Hik12345</passwd></loginParam>";
//        InitSystem initSystem = new InitSystem();
//        initSystem.setArg0(xml);
//        JAXBElement<InitSystemResponse> xmlString = (JAXBElement<InitSystemResponse>) getWebServiceTemplate().marshalSendAndReceive(url, initSystem);
//        System.out.println(xmlString.getValue().getReturn());
//        String aReturn = xmlString.getValue().getReturn();
//        JSONObject xml2json = XmlUtil.xml2json(aReturn).getJSONObject("root");
//        Integer code = xml2json.getInteger("code");
//        System.out.println("code = " + code);
//        String sessionId = "";
//        if (0 == code) {
//            sessionId = xml2json.getString("sessionId");
//        }
//        return sessionId;
//    }
//
//    private int pageSize = 100;
//    private int currentPage = 1;
//    private String beginTime;
//    private String endTime;
//    List listResult;
//
//    @Scheduled(cron = "0 0/1 * * * ?")
//    public void listParkData() {
//        if (1 == currentPage) {
//            Object object = redisUtils.get("parkPassBeginTime");
//            if (null != object) {
//                beginTime = String.valueOf(object);
//            }
//            if (null == object) {
//                beginTime = getBeginTime();
//            }
//            if (StringUtils.isEmpty(beginTime)) {
//                beginTime = "2018-04-04 00:00:00";
//            }
//            endTime = LocalDateTime.now().withNano(0).minusMinutes(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//            listResult = new ArrayList();
//        }
//        do {
//            StringBuilder builder = new StringBuilder();
//            builder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><vehicleQueryParam><sessionId>3ef8ca10-fc50-40ff-905a-1ce030de93b8</sessionId><searchType>1</searchType><passTime>")
//                    .append(beginTime)
//                    .append("_")
//                    .append(endTime)
//                    .append("</passTime><carSpeedRange>0,100</carSpeedRange><start>").append(currentPage).append("</start><limit>100</limit></vehicleQueryParam>");
//            SearchVehicleInfo searchVehicleInfo = new SearchVehicleInfo();
//            searchVehicleInfo.setArg0(builder.toString());
//            JAXBElement<SearchVehicleInfoResponse> xmlString = (JAXBElement<SearchVehicleInfoResponse>) getWebServiceTemplate().marshalSendAndReceive("https://52.109.3.192/bms/services/ThirdBayonetService?wsdl", searchVehicleInfo);
//            String aReturn = xmlString.getValue().getReturn();
//            if (StringUtils.isEmpty(aReturn)) return;
//            JSONObject xml2json = XmlUtil.xml2json(aReturn).getJSONObject("root");
//            if (null == xml2json) return;
//            int code = xml2json.getInteger("code").intValue();
//            if (0 != code) return;
//            System.out.println(xml2json);
//            Integer totalNum = xml2json.getInteger("totalNum");
//            if (currentPage == 1) {
//                currentPage = totalNum / pageSize + 1;
//            } else {
//                currentPage--;
//            }
//            JSONObject vehiclePassInfos = xml2json.getJSONObject("vehiclePassInfos");
//            JSONArray vehiclePassInfosJSONArray = vehiclePassInfos.getJSONArray("com.hikvision.kapu.modules.bayonet.vo.VehiclePassInfoVO");
//            List<VehicleRecord> list = new ArrayList<>();
//            for (int i = 0; i < vehiclePassInfosJSONArray.size(); i++) {
//                JSONObject vehiclePassInfo = vehiclePassInfosJSONArray.getJSONObject(i);
//                String crossingIndexCode = vehiclePassInfo.getString("crossingIndexCode");
//                String crossingName = vehiclePassInfo.getString("crossingName");
//                String laneId = vehiclePassInfo.getString("laneId");
//                String laneName = vehiclePassInfo.getString("laneName");
//                String alarmAction = vehiclePassInfo.getString("alarmAction");
//                String alarmActionName = vehiclePassInfo.getString("alarmActionName");
//                String directionName = vehiclePassInfo.getString("directionName");
//                String plateInfo = vehiclePassInfo.getString("plateInfo");
//                if ("车牌".equals(plateInfo)) continue;
//                String plateType = vehiclePassInfo.getString("plateType");
//                String vehicleType = vehiclePassInfo.getString("vehicleType");
//                if (StringUtils.isNotEmpty(vehicleType))
//                    vehicleType = CarTypeEnum.getType(Integer.parseInt(vehicleType));
//                String passTime = vehiclePassInfo.getString("passTime").split("\\.")[0];
//                String picVehicle = vehiclePassInfo.getString("picVehicle");
//                String vehicleColor = vehiclePassInfo.getString("vehicleColor");
//                String vehicleModel = vehiclePassInfo.getString("vehicleModel");
//                String vehicleModelName = vehiclePassInfo.getString("vehicleModelName");
//                String vehicleSublogo = vehiclePassInfo.getString("vehicleSublogo");
//                String vehicleSublogoName = vehiclePassInfo.getString("vehicleSublogoName");
//                VehicleRecord vehicleRecord = new VehicleRecord();
//                vehicleRecord.setCrossingIndexCode(crossingIndexCode);
//                vehicleRecord.setCrossingName(crossingName);
//                vehicleRecord.setLaneId(laneId);
//                vehicleRecord.setLaneName(laneName);
//                vehicleRecord.setDirectionName(directionName);
//                vehicleRecord.setAlarmAction(alarmAction);
//                vehicleRecord.setAlarmActionName(alarmActionName);
//                vehicleRecord.setPassTime(passTime);
//                vehicleRecord.setPicVehicle(picVehicle);
//                vehicleRecord.setPlateInfo(plateInfo);
//                vehicleRecord.setPlateType(plateType);
//                vehicleRecord.setVehicleType(vehicleType);
//                vehicleRecord.setVehicleColor(vehicleColor);
//                vehicleRecord.setVehicleModel(vehicleModel);
//                vehicleRecord.setVehicleModelName(vehicleModelName);
//                vehicleRecord.setVehicleLogo(vehicleSublogo);
//                vehicleRecord.setVehicleSubLogoName(vehicleSublogoName);
//                list.add(vehicleRecord);
//            }
//            listResult.addAll(list);
//        } while (currentPage > 1);
//        if (CollectionUtils.isNotEmpty(listResult)) {
//            RestMessage insertBatch = runner.insertBatch("vehicle_pass_record", JSON.toJSON(listResult));
//            if (insertBatch.isSuccess()) {
//                redisUtils.set("parkPassBeginTime", endTime);
//                LOGGER.info("数据新增成功,data.size()={}", listResult.size());
//            } else {
//                LOGGER.error("数据新增失败,error={}", insertBatch.getMessage());
//            }
//        }
//    }
//
//
//    public void listParkPoint() {
//        SearchCrossingInfo searchCrossingInfo = new SearchCrossingInfo();
//        String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?> <crossingInfo> <sessionId>3ef8ca10-fc50-40ff-905a-1ce030de93b8</sessionId> <controlindexCode></controlindexCode> </crossingInfo> ";
//        searchCrossingInfo.setArg0(xml);
//        JAXBElement<SearchCrossingInfoResponse> xmlString = (JAXBElement<SearchCrossingInfoResponse>) getWebServiceTemplate().marshalSendAndReceive("https://52.109.3.192/bms/services/ThirdBayonetService?wsdl", searchCrossingInfo);
//        String aReturn = xmlString.getValue().getReturn();
//        if (StringUtils.isEmpty(aReturn)) return;
//        JSONObject xml2json = XmlUtil.xml2json(aReturn).getJSONObject("root");
//        if (null == xml2json) return;
//        int code = xml2json.getInteger("code").intValue();
//        if (0 != code) return;
//        LOGGER.info("卡口设备信息 data={}", xml2json);
//    }
//
//    public String getBeginTime() {
//        StringBuilder builder = new StringBuilder();
//        builder.append("SELECT pass_time AS passTime")
//                .append(" FROM vehicle_pass_record")
//                .append(" WHERE is_deleted='N'")
//                .append(" ORDER BY pass_time DESC");
//        Pagination<Map> sql = runner.sql(builder.toString(), 1, 1);
//        String beginTime;
//        if (null != sql && sql.getCount() > 0) {
//            List<Map> data = sql.getData();
//            beginTime = String.valueOf(data.get(0).get("passTime"));
//        } else {
//            beginTime = "";
//        }
//        return beginTime;
//    }
}
