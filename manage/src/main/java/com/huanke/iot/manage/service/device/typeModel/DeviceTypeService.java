package com.huanke.iot.manage.service.device.typeModel;

import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeQueryRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceTypeCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceTypeVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceTypeService {

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;


    public Boolean createOrUpdate(DeviceTypeCreateOrUpdateRequest typeRequest) {

        int effectCount = 0;
        DeviceTypePo deviceTypePo = new DeviceTypePo();
        BeanUtils.copyProperties(typeRequest,deviceTypePo);
        if(typeRequest.getId() != null && typeRequest.getId() > 0){
            deviceTypePo.setLastUpdateTime(System.currentTimeMillis());
            effectCount = deviceTypeMapper.updateById(deviceTypePo);
        }else{
            deviceTypePo.setCreateTime(System.currentTimeMillis());
            effectCount =  deviceTypeMapper.insert(deviceTypePo);
        }
        return effectCount > 0;
    }

    public List<DeviceTypeVo> selectList(DeviceTypeQueryRequest request) {

        DeviceTypePo queryDeviceTypePo = new DeviceTypePo();
        queryDeviceTypePo.setName(request.getName());
        queryDeviceTypePo.setTypeNo(request.getTypeNo());

        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();

        List<DeviceTypePo> deviceTypePos = deviceTypeMapper.selectList(queryDeviceTypePo,limit,offset);
        return deviceTypePos.stream().map(deviceTypePo -> {
            DeviceTypeVo deviceTypeVo = new DeviceTypeVo();
            deviceTypeVo.setName(deviceTypePo.getName());
            deviceTypeVo.setTypeNo(deviceTypePo.getTypeNo());
            deviceTypeVo.setIcon(deviceTypePo.getIcon());
            deviceTypeVo.setRemark(deviceTypePo.getRemark());
            deviceTypeVo.setId(deviceTypePo.getId());
            return deviceTypeVo;
        }).collect(Collectors.toList());
    }

    public DeviceTypeVo selectById(DeviceTypeQueryRequest request) {

        DeviceTypePo queryDeviceTypePo = new DeviceTypePo();
        queryDeviceTypePo.setId(request.getId());

        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(queryDeviceTypePo);

        DeviceTypeVo deviceTypeVo = new DeviceTypeVo();
        deviceTypeVo.setName(deviceTypePo.getName());
        deviceTypeVo.setTypeNo(deviceTypePo.getTypeNo());
        deviceTypeVo.setIcon(deviceTypePo.getIcon());
        deviceTypeVo.setRemark(deviceTypePo.getRemark());
        deviceTypeVo.setId(deviceTypePo.getId());

        return deviceTypeVo;
    }
//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
