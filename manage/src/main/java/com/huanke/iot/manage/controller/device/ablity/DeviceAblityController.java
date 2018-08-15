package com.huanke.iot.manage.controller.device.ablity;

import com.aliyun.oss.OSSClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.device.DeviceUpgradeMapper;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.po.device.DeviceUpgradePo;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.manage.service.DeviceAdminService;
import com.huanke.iot.manage.service.DeviceOperLogService;
import com.huanke.iot.manage.service.device.DeviceService;
import com.huanke.iot.manage.service.device.ablity.DeviceAblityService;
import com.huanke.iot.manage.service.gateway.MqttSendService;
import com.huanke.iot.manage.vo.request.DeviceCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.DeviceLogQueryRequest;
import com.huanke.iot.manage.vo.response.DeviceOperLogVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;

//2018-08-15
//import com.huanke.iot.manage.controller.request.OtaDeviceRequest;
//2018-08-15
//import com.huanke.iot.manage.response.DeviceVo;

/**
 * @author caikun
 * @version 2018年08月15日 22:11
 **/
@RestController
@RequestMapping("/api/deviceAbility")
@Slf4j
public class DeviceAblityController {

    @Autowired
    private DeviceAblityService deviceAblityService;


    /**
     * 添加新能力
     * @param body
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @RequestMapping(value = "/createDeviceAblity",method = RequestMethod.POST)
    public ApiResponse<Boolean> createDevice(@RequestBody String body) throws Exception{
        Map<String,Object> requestParam = new ObjectMapper().readValue(body,Map.class);
        DeviceAblityPo deviceAblityPo  =(DeviceAblityPo) requestParam.get("deviceAblityPo");
        Boolean ret =  deviceAblityService.createOrUpdate(deviceAblityPo);
        return new ApiResponse<>(ret);
    }




//
//
//    private void uploadToOss(String fileKey,byte[] content){
//        OSSClient ossClient = new OSSClient(bucketUrl, accessKeyId,accessKeySecret);
//        try {
//            ossClient.putObject("idcfota", fileKey, new ByteArrayInputStream(content));
//        }catch (Exception e){
//            log.error("",e);
//        }finally {
//            if(ossClient != null){
//                ossClient.shutdown();
//            }
//        }
//    }
}
