package com.huanke.iot.api.service.device.timer;

import com.huanke.iot.api.constants.DictConstants;
import com.huanke.iot.api.controller.h5.req.DeviceTimerRequest;
import com.huanke.iot.api.controller.h5.response.DeviceTimerVo;
import com.huanke.iot.api.controller.h5.response.DictVo;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.TimerConstants;
import com.huanke.iot.base.dao.DictMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceTimerDayMapper;
import com.huanke.iot.base.dao.device.DeviceTimerMapper;
import com.huanke.iot.base.po.config.DictPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.DeviceTimerDayPo;
import com.huanke.iot.base.po.device.DeviceTimerPo;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DeviceTimerService {

    @Autowired
    private DeviceTimerMapper deviceTimerMapper;

    @Autowired
    private DeviceTimerDayMapper deviceTimerDayMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DictMapper dictMapper;

    @Transactional
    public Integer insertTimer(DeviceTimerRequest request) {
        String name = request.getName();
        Long afterTime = request.getAfterTime();
        String deviceIdStr = request.getDeviceId();
        Integer timerType = request.getTimerType();
        Integer type = request.getType();
        DeviceTimerPo deviceTimerPo = new DeviceTimerPo();
        if (type == TimerConstants.TIMER_TYPE_ONCE_TIME && afterTime <= 0) {
            return 0;
        }
        DevicePo devicePo = deviceMapper.selectByWxDeviceId(deviceIdStr);
        if (devicePo == null) {
            return 0;
        }
        deviceTimerPo.setTimerType(timerType);
        deviceTimerPo.setCreateTime(System.currentTimeMillis());
        deviceTimerPo.setLastUpdateTime(System.currentTimeMillis());
        deviceTimerPo.setDeviceId(devicePo.getId());
        deviceTimerPo.setName(name);
        deviceTimerPo.setUserId(request.getUserId());
        deviceTimerPo.setStatus(1);
        deviceTimerPo.setType(request.getType());
        deviceTimerPo.setHour(request.getHour());
        deviceTimerPo.setMinute(request.getMinute());
        deviceTimerPo.setSecond(request.getSecond());
        Integer ret = deviceTimerMapper.insert(deviceTimerPo);
        if (type == TimerConstants.TIMER_TYPE_ONCE_TIME) {
            deviceTimerPo.setExecuteTime(System.currentTimeMillis() + afterTime);
        }
        if (type == TimerConstants.TIMER_TYPE_IDEA) {
            List<Integer> daysOfWeek = request.getDaysOfWeek();
            for (Integer dayOfWeek : daysOfWeek) {
                DeviceTimerDayPo deviceTimerDayPo = new DeviceTimerDayPo();
                deviceTimerDayPo.setTimeId(deviceTimerPo.getId());
                deviceTimerDayPo.setDayOfWeek(dayOfWeek);
                deviceTimerDayPo.setCreateTime(System.currentTimeMillis());
                deviceTimerDayPo.setStatus(CommonConstant.STATUS_YES);
                deviceTimerDayMapper.insert(deviceTimerDayPo);
            }
        }
        return devicePo.getId();
    }


    public List<DeviceTimerVo> queryTimerList(Integer userId, String wxDeviceId, Integer type) {

        DevicePo devicePo = deviceMapper.selectByWxDeviceId(wxDeviceId);
        if (devicePo == null) {
            return Lists.newArrayList();
        }
        DeviceTimerPo queryPo = new DeviceTimerPo();
        queryPo.setUserId(userId);
        queryPo.setType(type);
        queryPo.setDeviceId(devicePo.getId());
        List<DeviceTimerPo> deviceTimerPos = deviceTimerMapper.selectTimerList(queryPo);
        List<DeviceTimerVo> deviceTimerVos = deviceTimerPos.stream().map(
                deviceTimerPo -> {
                    DeviceTimerVo deviceTimerVo = new DeviceTimerVo();
                    deviceTimerVo.setName(deviceTimerPo.getName());
                    Long t = deviceTimerPo.getExecuteTime() - System.currentTimeMillis();
                    if (t < 0) {
                        t = 0L;
                    }
                    deviceTimerVo.setId(deviceTimerPo.getId());
                    deviceTimerVo.setRemainTime(t);
                    deviceTimerVo.setTimerType(deviceTimerPo.getTimerType());
                    deviceTimerVo.setStatus(deviceTimerPo.getStatus());
                    deviceTimerVo.setType(deviceTimerPo.getType());
                    deviceTimerVo.setHour(deviceTimerPo.getHour());
                    deviceTimerVo.setMinute(deviceTimerPo.getMinute());
                    deviceTimerVo.setSecond(deviceTimerPo.getSecond());
                    if (type != null && type == TimerConstants.TIMER_TYPE_IDEA){
                        List<Integer> daysOfWeek = deviceTimerDayMapper.selectDaysOfWeekByTimeId(deviceTimerPo.getId());
                        deviceTimerVo.setDaysOfWeek(daysOfWeek);
                    }
                    return deviceTimerVo;
                }
        ).collect(Collectors.toList());
        return deviceTimerVos;
    }

    public Boolean updateTimerStatus(Integer userId, Integer timerId, Integer status) {

        DeviceTimerPo timerPo = deviceTimerMapper.selectById(timerId);
        if (timerPo != null) {
            if (!timerPo.getUserId().equals(userId)) {
                return false;
            }
        }
        if (status == timerPo.getStatus()) {
            return false;
        }
        DeviceTimerPo updatePo = new DeviceTimerPo();
        updatePo.setUserId(userId);
        updatePo.setId(timerId);
        updatePo.setStatus(status);
        updatePo.setLastUpdateTime(System.currentTimeMillis());
        return deviceTimerMapper.updateById(updatePo) > 0;
    }

    public DeviceTimerVo getById(Integer id) {
        DeviceTimerPo deviceTimerPo = deviceTimerMapper.selectById(id);
        if (deviceTimerPo != null) {
            DeviceTimerVo deviceTimerVo = new DeviceTimerVo();
            deviceTimerVo.setName(deviceTimerPo.getName());
            Long t = deviceTimerPo.getExecuteTime() - System.currentTimeMillis();
            if (t < 0) {
                t = 0L;
            }
            deviceTimerVo.setRemainTime(t);
            deviceTimerVo.setId(deviceTimerPo.getId());
            deviceTimerVo.setTimerType(deviceTimerPo.getTimerType());
            deviceTimerVo.setStatus(deviceTimerPo.getStatus());
            deviceTimerVo.setType(deviceTimerPo.getType());
            deviceTimerVo.setHour(deviceTimerPo.getHour());
            deviceTimerVo.setMinute(deviceTimerPo.getMinute());
            deviceTimerVo.setSecond(deviceTimerPo.getSecond());
            if(deviceTimerPo.getType() == TimerConstants.TIMER_TYPE_IDEA){
                List<Integer> daysOfWeek = deviceTimerDayMapper.selectDaysOfWeekByTimeId(deviceTimerPo.getId());
                deviceTimerVo.setDaysOfWeek(daysOfWeek);
            }
            return deviceTimerVo;
        }
        return null;
    }

    @Transactional
    public Boolean editTimer(Integer userId, DeviceTimerRequest deviceTimerRequest) {
        Integer type = deviceTimerRequest.getType();
        DeviceTimerPo updatePo = new DeviceTimerPo();
        updatePo.setId(deviceTimerRequest.getId());
        updatePo.setName(deviceTimerRequest.getName());
        updatePo.setStatus(deviceTimerRequest.getStatus());
        updatePo.setUserId(deviceTimerRequest.getUserId());
        updatePo.setExecuteTime(deviceTimerRequest.getAfterTime() + System.currentTimeMillis());
        updatePo.setLastUpdateTime(System.currentTimeMillis());
        updatePo.setType(deviceTimerRequest.getType());
        updatePo.setHour(deviceTimerRequest.getHour());
        updatePo.setMinute(deviceTimerRequest.getMinute());
        updatePo.setSecond(deviceTimerRequest.getSecond());
        deviceTimerRequest.setUserId(userId);
        boolean result = deviceTimerMapper.updateById(updatePo) > 0;
        if(type == TimerConstants.TIMER_TYPE_ONCE_TIME){
            deviceTimerDayMapper.deleteByTimeId(deviceTimerRequest.getId());
        }else if(type == TimerConstants.TIMER_TYPE_IDEA){
            deviceTimerDayMapper.deleteByTimeId(deviceTimerRequest.getId());
            List<Integer> daysOfWeek = deviceTimerRequest.getDaysOfWeek();
            for (Integer dayOfWeek : daysOfWeek) {
                DeviceTimerDayPo deviceTimerDayPo = new DeviceTimerDayPo();
                deviceTimerDayPo.setTimeId(deviceTimerRequest.getId());
                deviceTimerDayPo.setDayOfWeek(dayOfWeek);
                deviceTimerDayPo.setCreateTime(System.currentTimeMillis());
                deviceTimerDayPo.setStatus(CommonConstant.STATUS_YES);
                deviceTimerDayMapper.insert(deviceTimerDayPo);
            }
        }
        return result;
    }

    public List<DictVo> getTimeTypes() {
        List<DictVo> dictVoList = new ArrayList<>();
        List<DictPo> dictPoList = dictMapper.selectByType(DictConstants.TIME_JOB_TYPE);
        for (DictPo dictPo : dictPoList) {
            DictVo dictVo = new DictVo();
            dictVo.setLabel(dictPo.getLabel());
            dictVo.setValue(dictPo.getValue());
            dictVoList.add(dictVo);
        }
        return dictVoList;
    }
}
