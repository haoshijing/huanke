package com.huanke.iot.manage.service.device;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.base.dao.device.DeviceGroupItemMapper;
import com.huanke.iot.base.dao.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.device.DeviceIdPoolMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.data.DeviceInfoMapper;
//2018-08-15
//import com.huanke.iot.base.dao.publicnumber.PublicNumberMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.manage.vo.request.DeviceCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.DeviceQueryRequest;
import com.huanke.iot.manage.vo.request.DeviceQueryVo;
//2018-08-15
//import com.huanke.iot.manage.response.DeviceVo;
import com.huanke.iot.manage.service.wechart.WechartUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceInfoMapper deviceInfoMapper;

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

//    public Boolean createDevice(DeviceCreateOrUpdateRequest request) {
//        String mac = request.getMac();
//        DevicePo devicePo = deviceMapper.selectByMac(mac);
//        if (devicePo != null && StringUtils.isNotEmpty(devicePo.getDeviceId())) {
//            return false;
//        } else {
//            Integer publicId = request.getPublicId();
//            String  productId = request.getProductId();
//            DeviceIdPoolPo deviceIdPoolPo = deviceIdPoolMapper.selectByPublicIdAndPid(publicId,productId);
//
//            if (deviceIdPoolPo == null || true) {
//                //没有从池子中获取到设备id
//                PublicNumberPo publicNumberPo = publicNumberMapper.selectById(publicId);
//                if(publicNumberPo != null) {
//                    String appId = publicNumberPo.getAppId();
//                    String appSecret = publicNumberPo.getAppSecret();
//                    JSONObject jsonObject = obtainDeviceInfo(appId, appSecret, publicId,productId);
//                    if(jsonObject == null){
//                        return false;
//                    }else{
//                        String deviceId = jsonObject.getString("deviceid");
//                        String devicelicence = jsonObject.getString("devicelicence");
//                        DevicePo insertPo = new DevicePo();
//                        insertPo.setPublicId(publicId);
//                        insertPo.setMac(request.getMac());
//                        insertPo.setName(request.getName());
//                        insertPo.setDeviceId(deviceId);
//                        insertPo.setDevicelicence(devicelicence);
//                        insertPo.setWxProductId(productId);
//                        insertPo.setDeviceTypeId(request.getDeviceTypeId());
//                        deviceMapper.insert(insertPo);
//                    }
//                }
//            } else {
//                String deviceId = deviceIdPoolPo.getDeviceId();
//                String devicelicence = deviceIdPoolPo.getDevicelicence();
//                DevicePo insertPo = new DevicePo();
//                insertPo.setPublicId(publicId);
//                insertPo.setMac(request.getMac());
//                insertPo.setName(request.getName());
//                insertPo.setDeviceId(deviceId);
//                insertPo.setDevicelicence(devicelicence);
//                insertPo.setDeviceTypeId(request.getDeviceTypeId());
//                deviceMapper.insert(insertPo);
//                Integer devicePoolId = deviceIdPoolPo.getId();
//                deviceIdPoolMapper.deleteById(devicePoolId);
//            }
//
//        }
//        return true;
//
//    }

    /**2018-08-15
     * sixiaojun
     * @param request
     * @return
     */
    public Boolean createDevice(DeviceCreateOrUpdateRequest request) {
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
        return true;
    }

    /**2018-08-15
     * sixiaojun
     * @param
     * @return list
     */
    public List<DeviceQueryVo> deviceList(DeviceQueryRequest deviceQueryRequest){
        List<DevicePo> devicePos=deviceMapper.selectAll();
        List<DeviceQueryVo> deviceQueryVos=devicePos.stream().map(devicePo -> {
            DeviceQueryVo deviceQueryVo=new DeviceQueryVo();
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
            if(null != deviceGroupItemMapper.selectByDeviceId(devicePo)){
                deviceQueryVo.setGroup_id(deviceGroupItemMapper.selectByDeviceId(devicePo).getGroupId());
                deviceQueryVo.setGroup_name(deviceGroupMapper.selectById(deviceGroupItemMapper.selectByDeviceId(devicePo)).getGroupName());
            }
            else {
                deviceQueryVo.setGroup_id(0);
                deviceQueryVo.setGroup_name("无");
            }
            return deviceQueryVo;
        }).collect(Collectors.toList());
        return deviceQueryVos;
    }
    public List<DeviceVo> selectList(DeviceQueryRequest deviceQueryRequest) {

        DevicePo queryDevicePo = new DevicePo();
        queryDevicePo.setMac(deviceQueryRequest.getMac());
        queryDevicePo.setOnlineStatus(deviceQueryRequest.getOnlineStatus());
        Integer page = deviceQueryRequest.getPage();
        Integer limit = deviceQueryRequest.getLimit();
        Integer offset = (page - 1) * limit;

        List<DevicePo> devicePos = deviceMapper.selectList(queryDevicePo, limit, offset);

        List<DeviceVo> deviceVos = devicePos.stream().map(devicePo -> {
            DeviceVo deviceVo = new DeviceVo();
            deviceVo.setDeviceId(devicePo.getDeviceId());
            deviceVo.setName(devicePo.getName());
            deviceVo.setMac(devicePo.getMac());
            deviceVo.setId(devicePo.getId());
            if (devicePo.getBindStatus() != null) {
                if (devicePo.getBindStatus() == 2) {
                    deviceVo.setBindStatus("已绑定");
                } else if (devicePo.getBindStatus() == 3) {
                    deviceVo.setBindStatus("已解绑");
                } else if (devicePo.getBindStatus() == 1) {
                    deviceVo.setBindStatus("未绑定");
                }
            }
            if (devicePo.getOnlineStatus() == 1) {
                deviceVo.setOnlineStatus("在线");
            } else if (devicePo.getOnlineStatus() == 2) {
                deviceVo.setOnlineStatus("离线");
            }
            deviceVo.setLocation(devicePo.getLocation());
            DeviceInfoPo deviceInfoPo = deviceInfoMapper.selectByMac(devicePo.getMac());
            if (deviceInfoPo != null) {
                String version = deviceInfoPo.getVersion();
                JSONObject jsonObject = JSON.parseObject(version);
                if (jsonObject != null) {
                    deviceVo.setHardware(jsonObject.getString("hardware"));
                    deviceVo.setSoftware(jsonObject.getString("software"));
                }
            }
            return deviceVo;
        }).collect(Collectors.toList());
        return deviceVos;
    }

    public Boolean updateDeviceId(String mac, Integer publicId, String productId) {
        DevicePo devicePo = deviceMapper.selectByMac(mac);
        if(devicePo == null){
            return false;
        }
        PublicNumberPo publicNumberPo = publicNumberMapper.selectById(publicId);
        if(publicNumberPo != null){
            String appId = publicNumberPo.getAppId();
            String appSecret = publicNumberPo.getAppSecret();
            JSONObject jsonObject = obtainDeviceJson(appId, appSecret,publicId,productId);
            if(jsonObject != null){
                String deviceId = jsonObject.getString("deviceid");
                String devicelicence = jsonObject.getString("devicelicence");

                DevicePo updatePo = new DevicePo();
                updatePo.setId(devicePo.getId());
                updatePo.setDeviceId(deviceId);
                int updateRet = deviceMapper.updateOnlyDeviceId(updatePo);
                log.info("updateRet = {}",updateRet);
                return true;
            }
        }
        return false;
    }
    public Integer selectCount(DeviceQueryRequest deviceQueryRequest) {

        DevicePo queryDevicePo = new DevicePo();
        queryDevicePo.setMac(deviceQueryRequest.getMac());
        queryDevicePo.setOnlineStatus(deviceQueryRequest.getOnlineStatus());
        return deviceMapper.selectCount(queryDevicePo);
    }

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
