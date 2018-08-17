//package com.huanke.iot.manage.controller.device;
//
//import com.huanke.iot.base.api.ApiResponse;
//import com.huanke.iot.manage.vo.request.type.DeviceTypeCreateUpdateVo;
//import com.huanke.iot.manage.vo.request.type.DeviceTypeQueryRequest;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
///**
// * @author haoshijing
// * @version 2018年06月01日 12:56
// **/
//@RestController
//@RequestMapping("/api/device")
//public class DeviceTypeController {
//    @Autowired
//    private DeviceTypeService deviceTypeService;
////    @RequestMapping("/typeList")
////    public ApiResponse<List<DeviceTypeResponseVo>> selectList(@RequestBody DeviceTypeQueryRequest request){
////        List<DeviceTypeResponseVo> typeResponseVos = deviceTypeService.selectList(request);
////        return new ApiResponse<>(typeResponseVos);
////    }
//
//    @RequestMapping("/typeUpdate")
//    public ApiResponse<Boolean> updateDeviceType(@RequestBody DeviceTypeCreateUpdateVo deviceTypeCreateUpdateVo){
//        if(!checkRequestValid(deviceTypeCreateUpdateVo)){
//            return ApiResponse.PARAM_ERROR;
//        }
//        Boolean ret = deviceTypeService.createOrUpdate(deviceTypeCreateUpdateVo);
//        return new ApiResponse<>(ret);
//    }
//
//    @RequestMapping("/typeCreate")
//    public ApiResponse<Boolean> createDeviceType(@RequestBody DeviceTypeCreateUpdateVo deviceTypeCreateUpdateVo){
//        if(!checkRequestValid(deviceTypeCreateUpdateVo)){
//            return ApiResponse.PARAM_ERROR;
//        }
//        Boolean ret = deviceTypeService.createOrUpdate(deviceTypeCreateUpdateVo);
//        return new ApiResponse<>(ret);
//    }
//    @RequestMapping("/selectCount")
//    public ApiResponse<Integer> selectCount(@RequestBody DeviceTypeQueryRequest request){
//        Integer count = deviceTypeService.selectCount(request);
//        return new ApiResponse<>(count);
//    }
//
//    private boolean checkRequestValid(DeviceTypeCreateUpdateVo deviceTypeCreateUpdateVo){
//        String funcList = deviceTypeCreateUpdateVo.getFuncList();
//        String sensorList = deviceTypeCreateUpdateVo.getSensorList();
//        if(StringUtils.isEmpty(funcList) || StringUtils.isEmpty(sensorList)){
//            return true;
//        }
//        return false;
//    }
//}
