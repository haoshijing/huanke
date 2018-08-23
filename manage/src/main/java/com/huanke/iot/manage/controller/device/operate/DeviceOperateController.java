package com.huanke.iot.manage.controller.device.operate;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.device.DeviceUpgradeMapper;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.manage.vo.request.device.operate.DeviceAssignToCustomerRequest;
import com.huanke.iot.manage.vo.request.device.operate.DeviceCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.operate.DeviceListQueryRequest;
//2018-08-15
//import com.huanke.iot.manage.controller.request.OtaDeviceRequest;
import com.huanke.iot.manage.service.gateway.MqttSendService;
//2018-08-15
//import com.huanke.iot.manage.response.DeviceVo;
import com.huanke.iot.manage.service.DeviceOperLogService;
import com.huanke.iot.manage.service.device.operate.DeviceOperateService;
import com.huanke.iot.manage.vo.request.device.operate.DeviceQueryRequest;
import com.huanke.iot.manage.vo.response.device.DeviceListVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @author haoshijing
 * @version 2018年04月02日 15:11
 **/
@RestController
@RequestMapping("/api/device")
@Slf4j
public class DeviceOperateController {

    @Value("${bucketUrl}")
    private String bucketUrl;

    @Value("${bucketName}")
    private String bucketName;

    @Autowired
    private DeviceOperateService deviceService;

    @Autowired
    private MqttSendService mqttSendService;

    @Autowired
    private DeviceUpgradeMapper deviceUpgradeMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    @Autowired
    private DeviceOperLogService deviceOperLogService;

    @Value("${accessKeyId}")
    private String accessKeyId;

    @Value("${accessKeySecret}")
    private String accessKeySecret;

    /**
     * 添加新设备
     * @param deviceCreateOrUpdateRequests
     * @return 成功返回true，失败返回false
     * 接口要求的形式为：
     * {"deviceCreateOrUpdateRequests":[{"name":"shebei23","deviceTypeId":1,"mac":"0x-2201-22223","createTime":20180815},{"name":"shebei24","deviceTypeId":1,"mac":"0x-2201-22224","createTime":20180815}]}
     * @throws Exception
     */
    @ApiOperation("添加新设备")
    @RequestMapping(value = "/createDevice",method = RequestMethod.POST)
    public ApiResponse<Boolean> createDevice(@RequestBody DeviceCreateOrUpdateRequest deviceCreateOrUpdateRequests) throws Exception{
        List<DeviceCreateOrUpdateRequest.DeviceUpdateList> deviceList=deviceCreateOrUpdateRequests.getDeviceList();
        DevicePo devicePo;
        if(null == deviceList){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"设备不可为空");
        }
        //查询设备列表中是否存在相同mac地址的设备
        devicePo=deviceService.queryDeviceByMac(deviceList);
        if(null != devicePo){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"当前列表中mac地址"+devicePo.getMac()+"已存在");
        }
        //查询设备列表中是否存在相同名称的设备
        devicePo=deviceService.queryDeviceByName(deviceList);
        if(null != devicePo){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"当前列表中设备名称"+devicePo.getName()+"已存在");
        }
        else {
            Boolean ret = deviceService.createDevice(deviceList);
            return new ApiResponse<>(ret);
        }
    }

    /**
     * sixiaojun
     * 设备列表
     * @return
     * @throws Exception
     * 查询获取与设备相关的所有信息
     */
    @ApiOperation("分页查询设备")
    @RequestMapping(value = "/queryDevice",method = RequestMethod.POST)
    public ApiResponse<List<DeviceListVo>> queryAllDevice(@RequestBody DeviceListQueryRequest deviceListQueryRequest) throws Exception{
        List<DeviceListVo> deviceQueryVos=deviceService.queryDeviceByPage(deviceListQueryRequest);
        if(0 == deviceQueryVos.size()){
            return new ApiResponse<>(RetCode.OK,"设备列表中无设备");
        }
        else {
            return new ApiResponse<>(deviceQueryVos);
        }
    }

    /**
     * 查询当前设备列表中的设备总数
     * sixiaojun
     * 2018-08-20
     * @return
     */
    @ApiOperation("获取设备总数")
    @RequestMapping(value = "/queryCount",method = RequestMethod.GET)
    public ApiResponse<Integer> queryCount(){
        return new ApiResponse<>(deviceService.selectCount());
    }

    /**
     * 删除设备
     * sixiaojun
     * 2018-08-20
     * @param deviceCreateOrUpdateRequest
     * @return
     */
    @ApiOperation("删除选中设备")
    @RequestMapping(value = "/deleteDevice",method = RequestMethod.POST)
    public ApiResponse<Integer> deleteDevice(@RequestBody DeviceCreateOrUpdateRequest deviceCreateOrUpdateRequest){
        List<DeviceCreateOrUpdateRequest.DeviceUpdateList> deviceList=deviceCreateOrUpdateRequest.getDeviceList();
        if(null == deviceList){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"请先选中设备再删除");
        }
        return new ApiResponse<>(deviceService.deleteDevice(deviceList));
    }

    /**
     * 将选中设备分配给客户
     * sixiaojun
     * 2018-08-21
     * @param deviceAssignToCustomerRequest
     * @return
     */
    @ApiOperation("将选中设备分配给客户")
    @RequestMapping(value = "/assignDeviceToCustomer",method = RequestMethod.POST)
    public ApiResponse<Boolean> assignDeviceToCustomer(@RequestBody DeviceAssignToCustomerRequest deviceAssignToCustomerRequest){
        List<DeviceQueryRequest.DeviceQueryList> deviceList=deviceAssignToCustomerRequest.getDeviceQueryRequest().getDeviceList();
        if(0 == deviceList.size()){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"请选中设备");
        }
        else if(null == deviceAssignToCustomerRequest.getCustomerId()||0 == deviceAssignToCustomerRequest.getCustomerId()){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"请选择客户");
        }
        else if(deviceService.isDeviceHasCustomer(deviceList)){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"当前设别列表已有设备被分配");
        }
        else {
            Boolean ret=deviceService.assignDeviceToCustomer(deviceAssignToCustomerRequest);
            return new ApiResponse<>(ret);
        }
    }
//    @RequestMapping("/queryOperLogList")
//    public ApiResponse<List<DeviceOperLogVo>>queryOperLog(@RequestBody DeviceLogQueryRequest deviceLogQueryRequest){
//        List<DeviceOperLogVo> deviceOperLogVos =  deviceOperLogService.queryOperLogList(deviceLogQueryRequest);
//        return new ApiResponse<>(deviceOperLogVos);
//    }
//
//    @RequestMapping("/queryOperLogCount")
//    public ApiResponse<Integer>queryOperLogCount(@RequestBody DeviceLogQueryRequest deviceLogQueryRequest){
//        return new ApiResponse<>(0);
//    }
    /*2018-08-15
    @RequestMapping("/otaDevice1")
    public ApiResponse<Boolean> otaDevice1(@RequestBody OtaDeviceRequest request){
        Integer deviceId = request.getId();
        String topic = "/down/fota/"+deviceId;
        DeviceUpgradePo deviceUpgradePo = deviceUpgradeMapper.selectByFileName(request.getFileName());
        if(deviceUpgradePo == null){
            return new ApiResponse<>(false);
        }
        OtaDeviceVo otaDeviceVo = new OtaDeviceVo();
        OtaDeviceVo.OtaDeviceItem item = new OtaDeviceVo.OtaDeviceItem();
        item.setType(request.getOtaType());
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
        log.info("data={},topic = {}",data,topic);
        mqttSendService.sendMessage(topic, data);

        DeviceOperLogPo deviceOperLogPo = new DeviceOperLogPo();
        deviceOperLogPo.setFuncId("800");
        deviceOperLogPo.setOperUserId(0);
        deviceOperLogPo.setOperType(3);
        deviceOperLogPo.setDeviceId(deviceId);
        deviceOperLogPo.setFuncValue(JSON.toJSONString(otaDeviceVo));
        deviceOperLogPo.setRequestId(otaDeviceVo.getRequestId());
        deviceOperLogPo.setCreateTime(System.currentTimeMillis());
        deviceOperLogMapper.insert(deviceOperLogPo);

        return new ApiResponse<>(true);
    }
    */
//    @RequestMapping("/upload")
//    public ApiResponse<String> uploadBinFile(MultipartFile file){
//        String fileName = file.getOriginalFilename();
//        int idx = fileName.lastIndexOf(".");
//        String fileExt = fileName.substring(idx+1);
//        if(!StringUtils.contains(fileExt,"bin")){
//            ApiResponse apiResponse = new ApiResponse();
//            apiResponse.setCode(RetCode.PARAM_ERROR);
//            return apiResponse;
//        }
//        try {
//            uploadToOss(fileName,file.getBytes());
//        }catch (Exception e){
//            return ApiResponse.responseError(e);
//        }
//
//        DeviceUpgradePo queryPo = deviceUpgradeMapper.selectByFileName(fileName);
//        Long fileSize = file.getSize();
//        String md5 = "";
//        try {
//            md5 =  DigestUtils.md5Hex(file.getBytes());
//        }catch (Exception e){
//
//        }
//        if(queryPo != null){
//            DeviceUpgradePo updatePo = new DeviceUpgradePo();
//            updatePo.setLastUpdateTime(System.currentTimeMillis());
//            updatePo.setId(queryPo.getId());
//            updatePo.setFileSize(fileSize.intValue());
//            updatePo.setMd5(md5);
//            deviceUpgradeMapper.updateById(updatePo);
//        }else{
//            DeviceUpgradePo insertPo = new DeviceUpgradePo();
//            insertPo.setFileSize(fileSize.intValue());
//            insertPo.setMd5(md5);
//            insertPo.setCreateTime(System.currentTimeMillis());
//            insertPo.setFileName(fileName);
//            deviceUpgradeMapper.insert(insertPo);
//        }
//        return new ApiResponse<>(fileName);
//    }
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
