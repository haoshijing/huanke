package com.huanke.iot.api.controller.machine;

import com.huanke.iot.api.controller.machine.response.MachineDeviceVo;
import com.huanke.iot.api.service.machine.MachineService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequestMapping("/machine")
@RestController
@Slf4j
public class MachineController {

    @Autowired
    private  MachineService machineService;

    @RequestMapping("/obtain")
    public ApiResponse<MachineDeviceVo> obtainMachine(){
        MachineDeviceVo machineDeviceVo = machineService.obtainNewMachine();
        if(machineDeviceVo == null){
            return  new ApiResponse<>(RetCode.ERROR,"创建设备错误");
        }
        return new ApiResponse<>(machineDeviceVo);
    }
}
