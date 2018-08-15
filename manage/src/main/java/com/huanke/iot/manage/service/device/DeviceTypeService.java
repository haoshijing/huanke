package com.huanke.iot.manage.service.device;

import com.huanke.iot.base.dao.device.DeviceTypeMapper;
import com.huanke.iot.base.enums.FuncTypeEnums;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.device.DeviceTypePo;
import com.huanke.iot.manage.vo.request.type.DeviceTypeCreateUpdateVo;
import com.huanke.iot.manage.vo.request.type.DeviceTypeQueryRequest;
import com.huanke.iot.manage.vo.request.type.DeviceTypeResponseVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
public class DeviceTypeService {

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

//    public List<DeviceTypeResponseVo> selectList(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//
//        Integer offset = (queryRequest.getPage() - 1) * queryRequest.getLimit();
//        Integer limit = queryRequest.getLimit();
//        List<DeviceTypePo> deviceTypePos = deviceTypeMapper.selectList(queryTypePo, limit, offset);
//
//        return deviceTypePos.stream().map(deviceTypePo -> {
//            DeviceTypeResponseVo deviceTypeResponseVo = new DeviceTypeResponseVo();
//            deviceTypeResponseVo.setFuncList(deviceTypePo.getFuncList());
//            deviceTypeResponseVo.setIcon(deviceTypePo.getIcon());
//            deviceTypeResponseVo.setId(deviceTypePo.getId());
//            deviceTypeResponseVo.setTypeName(deviceTypePo.getName());
//            deviceTypeResponseVo.setSensorListStr(deviceTypePo.getSensorList());
//            StringBuilder sensorListSb = new StringBuilder();
//            StringBuilder funcListSb = new StringBuilder();
//            if(StringUtils.isNotEmpty(deviceTypePo.getSensorList())) {
//                String[] sensorIdArr = deviceTypePo.getSensorList().split(",");
//                for (String sensorType : sensorIdArr) {
//                    sensorListSb.append(SensorTypeEnums.getByCode(sensorType).getMark()).append(" ");
//                }
//            }
//            if(StringUtils.isNotEmpty(deviceTypePo.getFuncList())){
//                String[] funcIdArr = deviceTypePo.getFuncList().split(",");
//                for (String funcType : funcIdArr) {
//                    funcListSb.append(FuncTypeEnums.getByCode(funcType).getMark()).append(" ");
//                }
//            }
//            deviceTypeResponseVo.setFuncListStr(deviceTypePo.getFuncList());
//            deviceTypeResponseVo.setSensorListStr(sensorListSb.toString());
//            deviceTypeResponseVo.setFuncListStr(funcListSb.toString());
//            return deviceTypeResponseVo;
//        }).collect(Collectors.toList());
//    }

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
