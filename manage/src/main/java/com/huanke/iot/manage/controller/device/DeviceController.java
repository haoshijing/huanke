package com.huanke.iot.manage.controller.device;

import com.alibaba.fastjson.JSON;
import com.aliyun.oss.OSSClient;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.impl.device.DeviceUpgradeMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceLocationMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.po.device.DeviceUpgradePo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.manage.controller.device.request.DeviceQueryRequest;
import com.huanke.iot.manage.controller.device.request.DeviceUpdateRequest;
import com.huanke.iot.manage.controller.request.OtaDeviceRequest;
import com.huanke.iot.manage.gateway.MqttSendService;
import com.huanke.iot.manage.message.OtaDeviceVo;
import com.huanke.iot.manage.response.DeviceTypeVo;
import com.huanke.iot.manage.response.DeviceVo;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.manage.service.device.DeviceService;
import com.huanke.iot.manage.service.device.DeviceTypeService;
import com.huanke.iot.manage.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.UUID;

/**
 * @author haoshijing
 * @version 2018年04月02日 15:11
 **/
@RestController
@RequestMapping("/api/device")
@Slf4j
public class DeviceController {

    @Autowired
    private DeviceService deviceService;

    @Autowired
    private MqttSendService mqttSendService;

    @Autowired
    private DeviceUpgradeMapper deviceUpgradeMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;


    @Value("${accessKeyId}")
    private String accessKeyId;

    @Value("${accessKeySecret}")
    private String accessKeySecret;

    @Value("${bucketUrl}")
    private String bucketUrl;

    @Value("${bucketName}")
    private String bucketName;

    @RequestMapping("/resetPid")
    public ApiResponse<Boolean> resetPid(String productId){
        stringRedisTemplate.opsForValue().set("productId", productId);
        return new ApiResponse<>(true);
    }

    @Autowired
    private DeviceTypeService deviceTypeService;
    @RequestMapping("/queryList")
    public ApiResponse<List<DeviceVo>> queryList(@RequestBody DeviceQueryRequest deviceQueryRequest){
        List<DeviceVo> deviceVos = deviceService.selectList(deviceQueryRequest);
        return new ApiResponse<>(deviceVos);
    }

    @RequestMapping("/queryCount")
    public ApiResponse<Integer> queryCount(@RequestBody  DeviceQueryRequest deviceQueryRequest){
        return new ApiResponse<>(deviceService.selectCount(deviceQueryRequest));
    }

    @RequestMapping("/otaDevice")
    public ApiResponse<Boolean> otaDevice(@RequestBody OtaDeviceRequest request){
        Integer deviceId = request.getId();
        String topic = "/down/fota/"+deviceId;
        DeviceUpgradePo deviceUpgradePo = deviceUpgradeMapper.selectByFileName(request.getFileName());
        if(deviceUpgradePo == null){
            return new ApiResponse<>(false);
        }
        OtaDeviceVo otaDeviceVo = new OtaDeviceVo();
        OtaDeviceVo.OtaDeviceItem item = new OtaDeviceVo.OtaDeviceItem();
        item.setType(request.getType());
        String fileName = request.getFileName();
        int idx = fileName.lastIndexOf(".");
        String fileOriginName = fileName.substring(0,idx);
        String[] strArr = fileOriginName.split("_");
        if(strArr.length != 3){
            return  new ApiResponse<>(false);
        }
        OtaDeviceVo.VersionPo versionPo = new OtaDeviceVo.VersionPo();
        versionPo.setHardware(strArr[1]);
        versionPo.setSoftware(strArr[0]);
        item.setVersion(versionPo);
        otaDeviceVo.setFota(item);
        String url = String.format("http://%s.%s/%s",bucketName,bucketUrl,request.getFileName());
        item.setUrl(url);
        item.setSize(deviceUpgradePo.getFileSize());
        item.setMd5(deviceUpgradePo.getMd5());
        String data = JSON.toJSONString(otaDeviceVo);
        log.info("data={}",data);
        mqttSendService.sendMessage(topic, data);

        DeviceOperLogPo deviceOperLogPo = new DeviceOperLogPo();
        deviceOperLogPo.setFuncId("800");
        deviceOperLogPo.setDeviceId(deviceId);
        deviceOperLogPo.setFuncValue(JSON.toJSONString(otaDeviceVo));
        deviceOperLogPo.setRequestId(otaDeviceVo.getRequestId());
        deviceOperLogPo.setCreateTime(System.currentTimeMillis());
        deviceOperLogMapper.insert(deviceOperLogPo);

        return new ApiResponse<>(true);
    }


    @RequestMapping("/upload")
    public ApiResponse<String> uploadBinFile(MultipartFile file){
        String fileName = file.getOriginalFilename();
        try {
            uploadToOss(fileName,file.getBytes());
        }catch (Exception e){

        }

        DeviceUpgradePo queryPo = deviceUpgradeMapper.selectByFileName(fileName);
        Long fileSize = file.getSize();
        String md5 = "";
        try {
            md5 =  DigestUtils.md5Hex(file.getBytes());
        }catch (Exception e){

        }
        if(queryPo != null){
            DeviceUpgradePo updatePo = new DeviceUpgradePo();
            updatePo.setLastUpdateTime(System.currentTimeMillis());
            updatePo.setId(queryPo.getId());
            updatePo.setFileSize(fileSize.intValue());
            updatePo.setMd5(md5);
            deviceUpgradeMapper.updateById(updatePo);
        }else{
            DeviceUpgradePo insertPo = new DeviceUpgradePo();
            insertPo.setFileSize(fileSize.intValue());
            insertPo.setMd5(md5);
            insertPo.setCreateTime(System.currentTimeMillis());
            insertPo.setFileName(fileName);
            deviceUpgradeMapper.insert(insertPo);
        }

        return new ApiResponse<>(fileName);
    }


    @RequestMapping("/updateDevice")
    public ApiResponse<Boolean> updateDevice(@RequestBody DeviceUpdateRequest deviceUpdateRequest){

        if(StringUtils.isEmpty(deviceUpdateRequest.getDeviceName())){
            return new ApiResponse(RetCode.PARAM_ERROR);
        }
        Boolean updateRet = deviceService.updateDevice(deviceUpdateRequest);
        return new ApiResponse<>(updateRet);
    }


    @RequestMapping("/queryTypeList")
    public ApiResponse<List<DeviceTypeVo>> queryTypeList(@RequestBody DeviceQueryRequest deviceQueryRequest){
        List<DeviceTypeVo> deviceVos = deviceTypeService.selectList(deviceQueryRequest);
        return new ApiResponse<>(deviceVos);
    }

    @RequestMapping("/queryTypeCount")
    public ApiResponse<Integer> queryTypeCount(@RequestBody DeviceQueryRequest deviceQueryRequest){
        Integer typeCount = deviceTypeService.selectCount(deviceQueryRequest);
        return new ApiResponse<>(typeCount);
    }


    private void uploadToOss(String fileKey,byte[] content){
        OSSClient ossClient = new OSSClient(bucketUrl, accessKeyId,accessKeySecret);
        try {
            ossClient.putObject("idcfota", fileKey, new ByteArrayInputStream(content));
        }catch (Exception e){

        }finally {
            if(ossClient != null){
                ossClient.shutdown();
            }
        }

    }
}
