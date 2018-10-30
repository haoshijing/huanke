package com.huanke.iot.manage.controller.device.operate;


import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.operate.DeviceDataService;
import com.huanke.iot.manage.vo.request.device.operate.DeviceDataQueryRequest;
import com.huanke.iot.manage.vo.response.device.BaseListVo;
import com.huanke.iot.manage.vo.response.device.data.DeviceOperLogVo;
import com.huanke.iot.manage.vo.response.device.data.DeviceSensorStatVo;
import com.huanke.iot.manage.vo.response.device.data.DeviceWorkLogVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api/deviceData")
public class DeviceDataController {
    @Autowired
    private DeviceDataService deviceDataService;

    @ApiOperation("查看操作日志")
    @RequestMapping(value = "/queryOperLog", method = RequestMethod.POST)
    public ApiResponse<BaseListVo> queryOperLog(@RequestBody DeviceDataQueryRequest deviceDataQueryRequest){
        try {
            return this.deviceDataService.queryOperLogList(deviceDataQueryRequest);
        }catch (Exception e){
            log.error("设备日志查询异常 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"设备日志查询失败");
        }
    }
    @ApiOperation("查看设备数据")
    @RequestMapping(value = "/queryDeviceSensorStat", method = RequestMethod.POST)
    public ApiResponse<BaseListVo> queryDeviceSensorStat(@RequestBody DeviceDataQueryRequest deviceDataQueryRequest){
        try {
            return this.deviceDataService.queryDeivceSensorDataList(deviceDataQueryRequest);
        }catch (Exception e){
            log.error("设备传感器数据查询异常 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"设备传感器数据查询失败");
        }
    }

    @ApiOperation("查看工作日志")
    @RequestMapping(value = "/queryDeviceWorkLog", method = RequestMethod.POST)
    public ApiResponse<BaseListVo> queryDeviceWorkLog(@RequestBody DeviceDataQueryRequest deviceDataQueryRequest){
        try {
            return this.deviceDataService.queryDeviceWorkDataList(deviceDataQueryRequest);
        }catch (Exception e){
            log.error("设备工作日志查询异常= {}",e);
            return new ApiResponse<>(RetCode.ERROR,"设备工作日志查询失败");
        }
    }
}
