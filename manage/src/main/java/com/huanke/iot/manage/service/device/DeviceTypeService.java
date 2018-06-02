package com.huanke.iot.manage.service.device;

import com.huanke.iot.base.dao.impl.device.DeviceTypeMapper;
import com.huanke.iot.base.enums.FuncTypeEnums;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.base.po.device.DeviceTypePo;
import com.huanke.iot.manage.controller.device.request.DeviceQueryRequest;
import com.huanke.iot.manage.controller.device.request.type.DeviceTypeCreateUpdateVo;
import com.huanke.iot.manage.controller.device.request.type.DeviceTypeQueryRequest;
import com.huanke.iot.manage.controller.device.request.type.DeviceTypeResponseVo;
import com.huanke.iot.manage.response.DeviceTypeVo;
import org.assertj.core.util.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DeviceTypeService {

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    public List<DeviceTypeResponseVo> selectList(DeviceTypeQueryRequest queryRequest) {
        DeviceTypePo queryTypePo = new DeviceTypePo();
        queryTypePo.setName(queryRequest.getName());

        Integer offset = (queryRequest.getPage() - 1) * queryRequest.getLimit();
        Integer limit = queryRequest.getLimit();
        List<DeviceTypePo> deviceTypePos = deviceTypeMapper.selectList(queryTypePo, limit, offset);

        return deviceTypePos.stream().map(deviceTypePo -> {
            DeviceTypeResponseVo deviceTypeResponseVo = new DeviceTypeResponseVo();
            deviceTypeResponseVo.setFuncList(deviceTypePo.getFuncList());
            deviceTypeResponseVo.setIcon(deviceTypePo.getIcon());
            deviceTypeResponseVo.setId(deviceTypePo.getId());
            deviceTypeResponseVo.setTypeName(deviceTypePo.getName());
            deviceTypeResponseVo.setSensorListStr(deviceTypePo.getSensorList());
            StringBuilder sensorListSb = new StringBuilder();
            StringBuilder funcListSb = new StringBuilder();
            String[] sensorIdArr = deviceTypePo.getSensorList().split(",");
            String[] funcIdArr = deviceTypePo.getFuncList().split(",");
            for(String sensorType:sensorIdArr){
                sensorListSb.append(SensorTypeEnums.getByCode(sensorType).getMark()).append(" ");
            }
            for(String funcType :funcIdArr){
                funcListSb.append(FuncTypeEnums.getByCode(funcType).getMark()).append(" ");
            }
            deviceTypeResponseVo.setFuncListStr(deviceTypePo.getFuncList());
            deviceTypeResponseVo.setSensorListStr(sensorListSb.toString());
            deviceTypeResponseVo.setFuncListStr(funcListSb.toString());
            return deviceTypeResponseVo;
        }).collect(Collectors.toList());

    }

    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
        DeviceTypePo queryTypePo = new DeviceTypePo();
        queryTypePo.setName(queryRequest.getName());

        return deviceTypeMapper.selectCount(queryTypePo);
    }

    public Boolean createOrUpdate(DeviceTypeCreateUpdateVo deviceTypeCreateUpdateVo) {
        DeviceTypePo deviceTypePo = new DeviceTypePo();
        BeanUtils.copyProperties(deviceTypePo,deviceTypeCreateUpdateVo);
        int effectCount = 0;
        if(deviceTypePo.getId() != null && deviceTypePo.getId() > 0){
            deviceTypePo.setCreateTime(System.currentTimeMillis());
            effectCount =  deviceTypeMapper.updateById(deviceTypePo);
        }else{
            deviceTypePo.setLastUpdateTime(System.currentTimeMillis());
            effectCount = deviceTypeMapper.insert(deviceTypePo);
        }
        return effectCount > 0;
    }
}
