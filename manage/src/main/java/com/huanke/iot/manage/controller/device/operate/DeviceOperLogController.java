package com.huanke.iot.manage.controller.device.operate;


import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.manage.service.device.operate.DeviceOperLogService;
import com.huanke.iot.manage.vo.request.device.operate.DeviceLogQueryRequest;
import com.huanke.iot.manage.vo.response.device.operate.DeviceOperLogVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/")
public class DeviceOperLogController {
    @Autowired
    private DeviceOperLogService deviceOperLogService;

    @ApiOperation("查看操作日志")
    @RequestMapping(value = "/queryOperLog", method = RequestMethod.POST)
    public ApiResponse<List<DeviceOperLogVo>> queryOperLog(DeviceLogQueryRequest deviceLogQueryRequest){
        try {
            return this.deviceOperLogService.queryOperLogList(deviceLogQueryRequest);
        }catch (Exception e){
            log.error("设备日志查询异常 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"设备日志查询失败");
        }
    }
}
