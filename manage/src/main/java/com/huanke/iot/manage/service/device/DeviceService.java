package com.huanke.iot.manage.service.device;

import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.manage.request.DeviceQueryRequest;
import com.huanke.iot.manage.response.DeviceVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    public List<DeviceVo> selectList(DeviceQueryRequest deviceQueryRequest){

        DevicePo queryDevicePo = new DevicePo();
        queryDevicePo.setMac(deviceQueryRequest.getMac());
        Integer page = deviceQueryRequest.getPage();
        Integer limit = deviceQueryRequest.getLimit();
        Integer offset  = (page - 1)*limit;

        List<DevicePo> devicePos = deviceMapper.selectList(queryDevicePo,limit,offset);

        List<DeviceVo> deviceVos = devicePos.stream().map(devicePo -> {
            DeviceVo deviceVo = new DeviceVo();
            return deviceVo;
        }).collect(Collectors.toList());
        return deviceVos;
    }
}
