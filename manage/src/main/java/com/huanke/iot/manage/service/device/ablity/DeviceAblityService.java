package com.huanke.iot.manage.service.device.ablity;

import com.huanke.iot.base.dao.device.DeviceGroupItemMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityMapper;
import com.huanke.iot.base.po.device.DeviceTypePo;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.manage.vo.request.DeviceLogQueryRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityQueryRequest;
import com.huanke.iot.manage.vo.request.type.DeviceTypeCreateUpdateVo;
import com.huanke.iot.manage.vo.response.DeviceOperLogVo;
import com.huanke.iot.manage.vo.response.ablity.DeviceAblityVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceAblityService {

    @Autowired
    private DeviceAblityMapper deviceAblityMapper;


    public Boolean createOrUpdate(DeviceAblityCreateOrUpdateRequest ablityRequest) {

        int effectCount = 0;
        DeviceAblityPo deviceAblityPo = new DeviceAblityPo();
        BeanUtils.copyProperties(ablityRequest,deviceAblityPo);
        if(ablityRequest.getId() != null && ablityRequest.getId() > 0){
            deviceAblityPo.setLastUpdateTime(System.currentTimeMillis());
            effectCount = deviceAblityMapper.updateById(deviceAblityPo);
        }else{
            deviceAblityPo.setCreateTime(System.currentTimeMillis());
            effectCount =  deviceAblityMapper.insert(deviceAblityPo);
        }
        return effectCount > 0;
    }

    public List<DeviceAblityVo> selectList(DeviceAblityQueryRequest request) {

        DeviceAblityPo queryDeviceAblityPo = new DeviceAblityPo();
        queryDeviceAblityPo.setAblityName(request.getAblityName());
        queryDeviceAblityPo.setDirValue(request.getDirValue());
        queryDeviceAblityPo.setWriteStatus(request.getWriteStatus());

        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();

        List<DeviceAblityPo> deviceAblityPos = deviceAblityMapper.selectList(queryDeviceAblityPo,limit,offset);
        return deviceAblityPos.stream().map(deviceAblityPo -> {
            DeviceAblityVo deviceAblityVo = new DeviceAblityVo();
            deviceAblityVo.setAblityName(deviceAblityPo.getAblityName());
            deviceAblityVo.setDirValue(deviceAblityPo.getDirValue());
            deviceAblityVo.setWriteStatus(deviceAblityPo.getWriteStatus());
            deviceAblityVo.setId(deviceAblityPo.getId());
            return deviceAblityVo;
        }).collect(Collectors.toList());
    }

//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
