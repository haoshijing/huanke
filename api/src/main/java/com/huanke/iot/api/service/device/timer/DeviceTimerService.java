package com.huanke.iot.api.service.device.timer;

import com.huanke.iot.api.controller.h5.req.DeviceTimerRequest;
import com.huanke.iot.api.controller.h5.response.DeviceTimerVo;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.dao.impl.device.DeviceTimerMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.DeviceTimerPo;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DeviceTimerService {

    @Autowired
    private DeviceTimerMapper deviceTimerMapper;

    @Autowired
    private DeviceMapper deviceMapper;


    public Boolean insertTimer(DeviceTimerRequest request) {
        String name = request.getName();
        Long afterTime = request.getAfterTime();
        String deviceIdStr = request.getDeviceId();
        Integer timerType = request.getTimerType();

        if(afterTime <= 0){
            return false;
        }
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceIdStr);
        if(devicePo == null){
            return  false;
        }
        DeviceTimerPo deviceTimerPo = new DeviceTimerPo();
        deviceTimerPo.setTimerType(timerType);
        deviceTimerPo.setCreateTime(System.currentTimeMillis());
        deviceTimerPo.setLastUpdateTime(System.currentTimeMillis());
        deviceTimerPo.setDeviceId(devicePo.getId());
        deviceTimerPo.setName(name);
        deviceTimerPo.setUserId(request.getUserId());
        deviceTimerPo.setExecuteTime(System.currentTimeMillis()+afterTime);
        deviceTimerPo.setStatus(1);

        Integer ret = deviceTimerMapper.insert(deviceTimerPo);
        return  ret >0;
    }


    public List<DeviceTimerVo> queryTimerList(Integer userId,String deviceIdStr, Integer timerType) {

        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceIdStr);
        if(devicePo == null){
            return Lists.newArrayList();
        }
        DeviceTimerPo queryPo = new DeviceTimerPo();
        queryPo.setUserId(userId);
        queryPo.setTimerType(timerType);
        queryPo.setDeviceId(devicePo.getId());
        List<DeviceTimerPo> deviceTimerPos = deviceTimerMapper.selectTimerList(queryPo);
        List<DeviceTimerVo> deviceTimerVos = deviceTimerPos.stream().map(
                deviceTimerPo -> {
                    DeviceTimerVo deviceTimerVo = new DeviceTimerVo();
                    deviceTimerVo.setName(deviceTimerPo.getName());
                    Long t = deviceTimerPo.getExecuteTime() - System.currentTimeMillis();
                    if(t <0 ){
                        t = 0L;
                    }
                    deviceTimerVo.setId(deviceTimerPo.getId());
                    deviceTimerVo.setRemainTime(t);
                    deviceTimerVo.setTimerType(deviceTimerPo.getTimerType());
                    deviceTimerPo.setStatus(deviceTimerPo.getStatus());
                    return deviceTimerVo;
                }
        ).collect(Collectors.toList());
        return  deviceTimerVos;
    }

    public Boolean cancelTimer(Integer userId,Integer timerId){
        DeviceTimerPo updatePo = new DeviceTimerPo();
        updatePo.setId(timerId);
        updatePo.setUserId(userId);
        updatePo.setStatus(2);

        return deviceTimerMapper.cancelTimer(updatePo) > 0;
    }
}
