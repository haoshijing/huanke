package com.huanke.iot.manage.controller.device.operate;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.device.DeviceUpgradeMapper;
import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import com.huanke.iot.manage.common.util.ExcelUtil;
import com.huanke.iot.manage.vo.request.device.operate.*;
//2018-08-15
//import com.huanke.iot.manage.controller.request.OtaDeviceRequest;
import com.huanke.iot.manage.service.gateway.MqttSendService;
//2018-08-15
//import com.huanke.iot.manage.response.DeviceVo;
import com.huanke.iot.manage.service.DeviceOperLogService;
import com.huanke.iot.manage.service.device.operate.DeviceOperateService;
import com.huanke.iot.manage.vo.response.device.operate.DeviceAddSuccessVo;
import com.huanke.iot.manage.vo.response.device.operate.DeviceListVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
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
     *
     * @param deviceCreateOrUpdateRequests
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("添加新设备")
    @RequestMapping(value = "/createDevice", method = RequestMethod.POST)
    public ApiResponse<List<DeviceAddSuccessVo>> createDevice(@RequestBody DeviceCreateOrUpdateRequest deviceCreateOrUpdateRequests) {
        List<DeviceCreateOrUpdateRequest.DeviceUpdateList> deviceList = deviceCreateOrUpdateRequests.getDeviceList();
        DevicePo devicePo;
        try {

            if (null == deviceList || 0 == deviceList.size()) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "设备不可为空");
            }
            //查询设备列表中是否存在相同mac地址的设备
            devicePo = deviceService.queryDeviceByMac(deviceList);
            if (null != devicePo) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "当前列表中mac地址 " + devicePo.getMac() + " 已存在");
            }
            //查询设备列表中是否存在相同名称的设备
            devicePo = deviceService.queryDeviceByName(deviceList);
            if (null != devicePo) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "当前列表中设备名称 " + devicePo.getName() + " 已存在");
            } else {
                return this.deviceService.createDevice(deviceCreateOrUpdateRequests.getDeviceList());
            }
        } catch (Exception e){
            log.error("设备添加异常 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备新增异常");
        }

    }

    /**
     * sixiaojun
     * 设备列表
     *
     * @return
     * @throws Exception 查询获取与设备相关的所有信息
     */
    @ApiOperation("分页查询设备")
    @RequestMapping(value = "/queryDevice", method = RequestMethod.POST)
    public ApiResponse<List<DeviceListVo>> queryAllDevice(@RequestBody DeviceListQueryRequest deviceListQueryRequest) throws Exception {
        try {
            return this.deviceService.queryDeviceByPage(deviceListQueryRequest);
        } catch (Exception e) {
            log.error("设备查询错误 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备查询异常");
        }
    }

    /**
     * 查询当前设备列表中的设备总数
     * sixiaojun
     * 2018-08-20
     *
     * @return
     */
    @ApiOperation("获取设备总数")
    @RequestMapping(value = "/queryCount/{status}", method = RequestMethod.POST)
    public ApiResponse<Integer> queryCount(@PathVariable("status") Integer status) {
        try {
            return this.deviceService.selectCount(status);
        } catch (Exception e) {
            log.error("设备总数查询异常 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备总数查询异常");
        }
    }

    /**
     * 删除设备
     * sixiaojun
     * 2018-08-20
     *
     * @param deviceCreateOrUpdateRequest
     * @return
     */
    @ApiOperation("删除选中设备")
    @RequestMapping(value = "/deleteDevice", method = RequestMethod.POST)
    public ApiResponse<Integer> deleteDevice(@RequestBody DeviceCreateOrUpdateRequest deviceCreateOrUpdateRequest) {
        List<DeviceCreateOrUpdateRequest.DeviceUpdateList> deviceList = deviceCreateOrUpdateRequest.getDeviceList();
        if (null == deviceList || 0 == deviceList.size()) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "请先选中设备再删除", null);
        }
        try {
            return this.deviceService.deleteDevice(deviceCreateOrUpdateRequest.getDeviceList());
        } catch (Exception e) {
            log.error("设备删除异常 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备删除异常:" + e.getMessage());
        }
    }

    /**
     * 删除设备
     * sixiaojun
     * 2018-08-20
     *
     * @param deviceVo
     * @return
     */
    @ApiOperation("删除单个设备")
    @DeleteMapping(value = "/deleteOneDevice")
    public ApiResponse<Boolean> deleteOneDevice(@RequestBody DeviceUnbindRequest.deviceVo deviceVo) {
        try {
            if (null == deviceVo.deviceId || deviceVo.deviceId <= 0 || StringUtils.isBlank(deviceVo.mac)) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "参数不可为空");
            }
            return deviceService.deleteOneDevice(deviceVo);
        } catch (Exception e) {
            log.error("删除单个设备异常 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "删除单个设备异常:" + e.getMessage());
        }
    }

    /**
     * 恢复设备
     * caik
     * 2018-08-20
     *
     * @param deviceVo
     * @return
     */
    @ApiOperation("恢复设备")
    @PutMapping(value = "/recoverDevice")
    public ApiResponse<Boolean> recoverDevice(@RequestBody DeviceUnbindRequest.deviceVo deviceVo) {

        try {
            if (null == deviceVo.deviceId || deviceVo.deviceId <= 0 || StringUtils.isBlank(deviceVo.mac)) {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "参数不可为空");
            }
            return deviceService.recoverDevice(deviceVo);
        } catch (Exception e) {
            log.error("恢复设备异常 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "恢复设备异常:" + e.getMessage());
        }
    }

    /**
     * 将选中设备分配给客户
     * sixiaojun
     * 2018-08-21
     *
     * @param deviceAssignToCustomerRequest
     * @return
     */
    @ApiOperation("将选中设备分配给客户")
    @RequestMapping(value = "/assignDeviceToCustomer", method = RequestMethod.POST)
    public ApiResponse<Boolean> assignDeviceToCustomer(@RequestBody DeviceAssignToCustomerRequest deviceAssignToCustomerRequest) {
        List<DeviceQueryRequest.DeviceQueryList> deviceList = deviceAssignToCustomerRequest.getDeviceQueryRequest().getDeviceList();
        if (null == deviceList || 0 == deviceList.size()) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "请选中设备", false);
        } else if (null == deviceAssignToCustomerRequest.getCustomerId() || 0 == deviceAssignToCustomerRequest.getCustomerId()) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "请选择客户", false);
        } else {
            try {
                DevicePo devicePo = this.deviceService.isDeviceHasCustomer(deviceList);
                if (null != devicePo) {
                    return new ApiResponse<>(RetCode.PARAM_ERROR, "当前设备列表中 " + devicePo.getMac() + " 已被分配");
                }
                return deviceService.assignDeviceToCustomer(deviceAssignToCustomerRequest);
            } catch (Exception e) {
                log.error("设备分配异常 = {}", e);
                return new ApiResponse<>(RetCode.ERROR, "设备分配异常");
            }
        }
    }

    /**
     * 将选中设备从客户处召回
     * sixiaojun
     * 2018-08-28
     *
     * @param deviceQueryRequest
     * @return
     */
    @ApiOperation("将选中设备从客户处召回")
    @RequestMapping(value = "/callBackDeviceFromCustomer", method = RequestMethod.POST)
    public ApiResponse<Boolean> assignDeviceToCustomer(@RequestBody DeviceQueryRequest deviceQueryRequest) {
        List<DeviceQueryRequest.DeviceQueryList> deviceList = deviceQueryRequest.getDeviceList();
        if (null == deviceList || 0 == deviceList.size()) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "请选中设备", false);
        } else {
            try {
                DevicePo devicePo = deviceService.isDeviceHasCustomer(deviceList);
                if (null == devicePo) {
                    return new ApiResponse<>(RetCode.PARAM_ERROR, "当前设别列表设备 " + devicePo.getName() + " 尚未被分配", false);
                }
                return this.deviceService.callBackDeviceFromCustomer(deviceList);
            } catch (Exception e) {
                log.error("设备召回异常 = {}", e);
                return new ApiResponse<>(RetCode.ERROR, "设备召回异常");
            }
        }
    }

    @ApiOperation("根据用户的openId查询用户的组信息")
    @RequestMapping(value = "/queryTeamInfo", method = RequestMethod.POST)
    public ApiResponse<List<DeviceTeamPo>> queryTeamInfo(@RequestBody TeamInfoQueryRequest teamInfoQueryRequest) {
        CustomerUserPo customerUserPo = this.deviceService.isUserExist(teamInfoQueryRequest.getOpenId());
        if (null == customerUserPo) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "用户openId " + teamInfoQueryRequest.getOpenId() + " 不存在");
        }
        try {
            return this.deviceService.queryTeamInfoByUser(teamInfoQueryRequest.getOpenId());
        } catch (Exception e) {
            log.error("用户设备组查询异常 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "用户设备组查询异常");
        }

    }

    @ApiOperation("将选中设备绑定给用户")
    @RequestMapping(value = "/bindDeviceToUser", method = RequestMethod.POST)
    public ApiResponse<Boolean> bindDeviceToUser(@RequestBody DeviceBindToUserRequest deviceBindToUserRequest) {
        List<DeviceQueryRequest.DeviceQueryList> deviceLists = deviceBindToUserRequest.getDeviceQueryRequest().getDeviceList();
        if (null == deviceLists || 0 == deviceLists.size()) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "请选择设备", false);
        }
        if (null == deviceBindToUserRequest.getOpenId() || StringUtils.isEmpty(deviceBindToUserRequest.getOpenId())) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "请指定一个用户", false);
        }
        if (null == deviceBindToUserRequest.getTeamId() || 0 == deviceBindToUserRequest.getTeamId()) {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "请指定一个组", false);
        }
        try {
            return this.deviceService.bindDeviceToUser(deviceBindToUserRequest);
        } catch (Exception e) {
            log.error("设备绑定异常 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备绑定异常");
        }
    }

    @ApiOperation("查询主设备下的从设备")
    @RequestMapping(value = "/queryChildDevice/{id}", method = RequestMethod.POST)
    public ApiResponse<List<DevicePo>> queryChildDevice(@PathVariable("id") Integer hostDeviceId){
        try {
            return this.deviceService.queryChildDevice(hostDeviceId);
        }catch (Exception e){
            log.error("从设备查询错误 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"从设备查询失败");
        }
    }


    @ApiOperation("查询当前客户下的用户列表")
    @RequestMapping(value = "/queryUsers", method = RequestMethod.POST)
    public ApiResponse<List<CustomerUserPo>> queryUsers(@RequestBody QueryInfoByCustomerRequest queryInfoByCustomerRequest) {
        List<CustomerUserPo> customerUserPoList = this.deviceService.queryUser(queryInfoByCustomerRequest.getCustomerId());
        if (0 != customerUserPoList.size()) {
            return new ApiResponse<>(RetCode.OK, "查询成功", customerUserPoList);
        } else {
            return new ApiResponse<>(RetCode.OK, "当前客户下无用户数据", null);
        }

    }

    @ApiOperation("解绑")
    @RequestMapping(value = "/untieDeviceToUser", method = RequestMethod.POST)
    public ApiResponse<Boolean> untieDeviceToUser(@RequestBody DeviceUnbindRequest deviceUnbindRequest) {
        try {
            if (deviceUnbindRequest != null && deviceUnbindRequest.deviceVos != null && deviceUnbindRequest.deviceVos.size() > 0) {

                return deviceService.untieDeviceToUser(deviceUnbindRequest);
            } else {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "解绑设备列表不可为空");

            }
        } catch (Exception e) {
            log.error("设备解绑异常 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备解绑异常");
        }
    }

    @ApiOperation("禁用")
    @RequestMapping(value = "/updateDeivceDisble", method = RequestMethod.PUT)
    public ApiResponse<Boolean> updateDeivceDisble(@RequestBody DeviceUnbindRequest devices) {
        try {
            if (devices != null && devices.deviceVos != null && devices.deviceVos.size() > 0) {

                return deviceService.updateDeivceDisble(devices);
            } else {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "禁用设备列表不可为空");
            }
        } catch (Exception e) {
            log.error("设备禁用异常 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备禁用异常");
        }
    }

    @ApiOperation("启用")
    @RequestMapping(value = "/updateDeivceEnable", method = RequestMethod.PUT)
    public ApiResponse<Boolean> updateDeivceEnable(@RequestBody DeviceUnbindRequest devices) {
        try {
            if (devices != null && devices.deviceVos != null && devices.deviceVos.size() > 0) {

                return deviceService.updateDeivceEnable(devices);
            } else {
                return new ApiResponse<>(RetCode.PARAM_ERROR, "启用设备列表不可为空");

            }
        } catch (Exception e) {
            log.error("设备启用异常 = {}", e);
            return new ApiResponse<>(RetCode.ERROR, "设备启用异常");
        }
    }

    @ApiOperation("导出设备列表")
    @RequestMapping(value = "/exportDeviceData", method = RequestMethod.GET)
    public ApiResponse<String> exportDeviceData(@RequestBody DeviceListExportRequest deviceListExportRequest,HttpServletResponse response){
        try {
            this.deviceService.exportDeviceList(response,deviceListExportRequest);
        }catch (Exception e){
            log.error("设备列表导出错误 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"设备列表导出异常");
        }

        return new ApiResponse<>(RetCode.OK,"导出excel成功");
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