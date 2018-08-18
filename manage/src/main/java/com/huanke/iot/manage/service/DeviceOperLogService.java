package com.huanke.iot.manage.service;

import com.huanke.iot.base.dao.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.manage.vo.request.device.operate.DeviceLogQueryRequest;
import com.huanke.iot.manage.vo.response.DeviceOperLogVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author haoshijing
 * @version 2018年05月30日 13:25
 **/
@Service
public class DeviceOperLogService {

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    public List<DeviceOperLogVo> queryOperLogList(DeviceLogQueryRequest request) {
        DeviceOperLogPo deviceOperLogPo = new DeviceOperLogPo();
        deviceOperLogPo.setDeviceId(request.getDeviceId());
        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();

        List<DeviceOperLogPo> deviceOperLogPos = deviceOperLogMapper.selectList(deviceOperLogPo,limit,offset);
        return deviceOperLogPos.stream().map(bean -> {
            DeviceOperLogVo deviceOperLogVo = new DeviceOperLogVo();
            return deviceOperLogVo;
        }).collect(Collectors.toList());
    }
}
