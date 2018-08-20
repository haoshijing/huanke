package com.huanke.iot.manage.service.device.operate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.dao.device.DeviceGroupItemMapper;
import com.huanke.iot.base.dao.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.device.DeviceIdPoolMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.manage.vo.request.device.operate.DeviceCreateOrUpdateRequest;
import com.huanke.iot.manage.service.wechart.WechartUtil;
import com.huanke.iot.manage.vo.request.device.operate.DeviceListRequest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceOperateService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceGroupItemMapper deviceGroupItemMapper;

    @Autowired
    private DeviceGroupMapper deviceGroupMapper;

    @Autowired
    private DeviceIdPoolMapper deviceIdPoolMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Autowired
    private WechartUtil wechartUtil;

    /**2018-08-15
     * sixiaojun
     * 支持批量或单个添加
     * @param deviceCreateOrUpdateRequests
     * @return
     */
    public Boolean createDevice(List<DeviceCreateOrUpdateRequest> deviceCreateOrUpdateRequests) {

        for (DeviceCreateOrUpdateRequest request:deviceCreateOrUpdateRequests
             ) {
            String mac=request.getMac();
            DevicePo devicePo=deviceMapper.selectByMac(mac);
            if(null != devicePo && StringUtils.isNotEmpty(devicePo.getDeviceId())){
                return  false;
            }
            else {
                DevicePo insertPo = new DevicePo();
                insertPo.setName(request.getName());
                insertPo.setTypeId(request.getDeviceTypeId());
                insertPo.setMac(request.getMac());
                insertPo.setCreateTime(request.getCreateTime());
                deviceMapper.insert(insertPo);
            }
        }
        return true;
    }

    /**2018-08-15
     * sixiaojun
     * 根据前台请求按页查询设备数据
     * @param deviceListRequest
     * @return list
     */
public List<DeviceListRequest> queryDeviceByPage(DeviceListRequest deviceListRequest){
        //当期要查询的页
        Integer currentPage = deviceListRequest.getPage();
        //每页显示的数量
        Integer limit= deviceListRequest.getLimit();
        //偏移量
        Integer offset = (currentPage - 1) * limit;
        //查询所有数据相关数据，要求DevicePo所有值为null，所以新建一个空的DevicePo
        DevicePo queryPo=new DevicePo();
        List<DevicePo> devicePos=deviceMapper.selectList(queryPo,limit,offset);
        List<DeviceListRequest> deviceQueryVos=devicePos.stream().map(devicePo -> {
            DeviceListRequest deviceQueryVo=new DeviceListRequest();
            deviceQueryVo.setName(devicePo.getName());
            deviceQueryVo.setMac(devicePo.getMac());
            deviceQueryVo.setDeviceTypeId(devicePo.getDeviceTypeId());
            if(null != devicePo.getBindStatus()){
                if(3 == devicePo.getBindStatus()){
                    deviceQueryVo.setBindStatus("已解绑");
                }
                if(2 == devicePo.getBindStatus()){
                    deviceQueryVo.setBindStatus("已绑定");
                }
                if(1 == devicePo.getBindStatus()){
                    deviceQueryVo.setBindStatus("未绑定");
                }
            }
            if(null != devicePo.getEnableStatus()){
                if(1 == devicePo.getEnableStatus()){
                    deviceQueryVo.setEnableStatus("已启用");
                }
                if(0 == devicePo.getEnableStatus()){
                    deviceQueryVo.setEnableStatus("已禁用用");
                }
            }
            if(null != deviceGroupItemMapper.selectByDeviceId(devicePo.getId())){
                deviceQueryVo.setGroupId(deviceGroupItemMapper.selectByDeviceId(devicePo.getId()).getGroupId());
                deviceQueryVo.setGroupName(deviceGroupMapper.selectById(deviceGroupItemMapper.selectByDeviceId(devicePo.getId()).getGroupId()).getName());
            }
            else {
                deviceQueryVo.setGroupId(-1);
                deviceQueryVo.setGroupName("无");
            }
            if(null !=devicePo.getWorkStatus()){
                if(1 == devicePo.getWorkStatus()){
                    deviceQueryVo.setWorkStatus("租赁中");
                }
                if(0 == devicePo.getWorkStatus()){
                    deviceQueryVo.setWorkStatus("空闲");
                }
            }
            if(null != devicePo.getOnlineStatus()){
                if(1 == devicePo.getOnlineStatus()){
                    deviceQueryVo.setOnlineStatus("在线");
                }
                if(0 == devicePo.getOnlineStatus()){
                    deviceQueryVo.setOnlineStatus("离线");
                }
            }
            return deviceQueryVo;
        }).collect(Collectors.toList());
        return deviceQueryVos;
    }


    /**
     * 2018-08-18
     * sixiaojun
     * 获取设备总数
     * @param
     * @return
     */
    public Integer selectCount() {
        DevicePo queryDevicePo = new DevicePo();
        return deviceMapper.selectCount(queryDevicePo);
    }

    /**
     * 2018-08-20
     * sixiaojun
     * 根据mac地址查询设备表中是否存在相同mac地址的设备，如存在，返回DevicePo，新增失败
     * @param deviceCreateOrUpdateRequests
     * @return devicePo
     */
    public DevicePo isDeviceExist(List<DeviceCreateOrUpdateRequest> deviceCreateOrUpdateRequests){
        DevicePo devicePo=null;
        for(DeviceCreateOrUpdateRequest deviceCreateOrUpdateRequest:deviceCreateOrUpdateRequests){
            devicePo=deviceMapper.selectByMac(deviceCreateOrUpdateRequest.getMac());
            if(null != devicePo){
                return devicePo;
            }
        }
        return devicePo;
    }
//    public Boolean updateDeviceId(String mac, Integer publicId, String productId) {
//        DevicePo devicePo = deviceMapper.selectByMac(mac);
//        if(devicePo == null){
//            return false;
//        }
//        PublicNumberPo publicNumberPo = publicNumberMapper.selectById(publicId);
//        if(publicNumberPo != null){
//            String appId = publicNumberPo.getAppId();
//            String appSecret = publicNumberPo.getAppSecret();
//            JSONObject jsonObject = obtainDeviceJson(appId, appSecret,publicId,productId);
//            if(jsonObject != null){
//                String deviceId = jsonObject.getString("deviceid");
//                String devicelicence = jsonObject.getString("devicelicence");
//
//                DevicePo updatePo = new DevicePo();
//                updatePo.setId(devicePo.getId());
//                updatePo.setDeviceId(deviceId);
//                int updateRet = deviceMapper.updateOnlyDeviceId(updatePo);
//                log.info("updateRet = {}",updateRet);
//                return true;
//            }
//        }
//        return false;
//    }


    public Boolean updateDevice(DeviceCreateOrUpdateRequest deviceUpdateRequest) {

        DevicePo updatePo = new DevicePo();
        updatePo.setId(deviceUpdateRequest.getId());
        updatePo.setName(deviceUpdateRequest.getName());
        return deviceMapper.updateById(updatePo) > 0;
    }


    private JSONObject obtainDeviceJson(String appId,String appSecret,Integer publicId,String productId) {
        JSONObject deviceInfo = obtainDeviceInfo(appId,appSecret,publicId,productId);
        if (deviceInfo == null) {
            wechartUtil.getAccessToken(appId,appSecret,publicId, true);
            deviceInfo = obtainDeviceInfo(appId, appSecret, publicId,productId);
        }
        if (deviceInfo != null) {
            return deviceInfo;
        }
        return null;
    }

    private JSONObject obtainDeviceInfo(String appId,String appSecret,Integer publicId,String productId) {

        String accessToken = wechartUtil.getAccessToken(appId, appSecret,publicId, false);
        String url = new StringBuilder("https://api.weixin.qq.com/device/getqrcode?access_token=").append(accessToken).append("&product_id=").append(productId).toString();
        HttpGet httpGet = new HttpGet();
        try {
            httpGet.setURI(new URI(url));
            CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet);
            BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            log.info("result = {}", result);
            JSONObject jsonObject = JSON.parseObject(result.toString());
            if (jsonObject != null) {
                JSONObject resultObject = jsonObject.getJSONObject("base_resp");
                if (resultObject != null && resultObject.containsKey("errcode")) {
                    Integer retCode = resultObject.getInteger("errcode");
                    if (retCode != null && retCode.equals(0)) {
                        return jsonObject;
                    }
                }
            }
        } catch (Exception e) {
            log.error("", e);
        }
        return null;
    }

    private Integer getCanUseProductId(Integer publicId) {
        String productKey = "productKey." + publicId;
        String productIdStr = stringRedisTemplate.opsForValue().get(productKey);
        if (StringUtils.isEmpty(productIdStr)) {
            return 0;
        }
        return Integer.valueOf(productIdStr);
    }

}
