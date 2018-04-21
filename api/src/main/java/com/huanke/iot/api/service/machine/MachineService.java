package com.huanke.iot.api.service.machine;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.controller.machine.response.MachineDeviceVo;
import com.huanke.iot.api.wechat.WechartUtil;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.UUID;

/**
 * @author haoshijing
 * @version 2018年04月09日 13:03
 **/
@Repository
@Slf4j
public class MachineService {
    @Value("${productId}")
    private String productId;

    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private WechartUtil wechartUtil;

    public MachineDeviceVo queryByMac(String mac) {
        DevicePo devicePo = deviceMapper.selectByMac(mac);
        if(devicePo != null){
            MachineDeviceVo machineDeviceVo = new MachineDeviceVo();
            machineDeviceVo.setDeviceId(devicePo.getId());
            String deviceId = devicePo.getDeviceId();
            String datas[] = deviceId.split("_");
            if(datas.length == 3){
                machineDeviceVo.setDeviceType(datas[0]+"_"+datas[1]);
            }
            machineDeviceVo.setDevicelicence(devicePo.getDevicelicence());
            machineDeviceVo.setWechatDeviceId(devicePo.getDeviceId());
            return machineDeviceVo;
        }
        return null;
    }

    public Integer createNew(String mac,Integer typeId){
        DevicePo devicePo = new DevicePo();
        JSONObject jsonObject = obtainDeviceJson();
        if(jsonObject != null){
            String deviceId = jsonObject.getString("deviceid");
            String devicelicence = jsonObject.getString("devicelicence");
            devicePo.setMac(mac);
            devicePo.setDeviceId(deviceId);
            devicePo.setDeviceTypeId(typeId);
            devicePo.setDevicelicence(devicelicence);
            devicePo.setCreateTime(System.currentTimeMillis());

            DevicePo queryDevicePo = deviceMapper.selectByMac(mac);
            if(queryDevicePo != null){
                return 1;
            }
            int insertRet = deviceMapper.insert(devicePo);
            if(insertRet > 0){
                return 0;
            }
        }
        return 2;
    }

    private JSONObject obtainDeviceJson() {
        JSONObject deviceInfo = obtainDeviceInfo();
        if(deviceInfo == null){
            wechartUtil.getAccessToken(true);
            deviceInfo = obtainDeviceInfo();
        }
        if(deviceInfo != null){
            return deviceInfo;
        }
        return   null;
    }

    private JSONObject obtainDeviceInfo() {
        String accessToken = wechartUtil.getAccessToken(false);
        String url = new StringBuilder("https://api.weixin.qq.com/device/getqrcode?access_token=").append(accessToken)
                .append("&product_id=").append(productId).toString();
        HttpGet httpGet = new HttpGet();
        try {
            httpGet.setURI(new URI(url));
            CloseableHttpResponse response = HttpClients.createDefault().execute(httpGet);
            BufferedReader rd = new BufferedReader(
                    new InputStreamReader(response.getEntity().getContent()));

            StringBuilder result = new StringBuilder();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            log.info("result = {}", result);
            JSONObject jsonObject = JSON.parseObject(result.toString());
            if (jsonObject != null) {
                JSONObject resultObject = jsonObject.getJSONObject("base_resp");
                if(resultObject != null && resultObject.containsKey("errcode")){
                    Integer retCode = resultObject.getInteger("errcode");
                    if(retCode != null && retCode.equals(0)){
                        return jsonObject;
                    }
                }
            }
        }catch (Exception e){
            log.error("",e);
        }
        return null;
    }


}
