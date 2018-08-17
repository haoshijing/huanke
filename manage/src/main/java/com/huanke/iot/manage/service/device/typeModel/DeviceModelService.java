package com.huanke.iot.manage.service.device.typeModel;

import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.typeModel.DeviceModelQueryRequest;
import com.huanke.iot.manage.vo.response.device.typeModel.DeviceModelVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class DeviceModelService {

    @Autowired
    private DeviceModelMapper deviceModelMapper;


    public Boolean createOrUpdate(DeviceModelCreateOrUpdateRequest modelRequest) {

        int effectCount = 0;
        DeviceModelPo deviceModelPo = new DeviceModelPo();
        BeanUtils.copyProperties(modelRequest,deviceModelPo);
        if(modelRequest.getId() != null && modelRequest.getId() > 0){
            deviceModelPo.setLastUpdateTime(System.currentTimeMillis());
            effectCount = deviceModelMapper.updateById(deviceModelPo);
        }else{
            deviceModelPo.setCreateTime(System.currentTimeMillis());
            effectCount =  deviceModelMapper.insert(deviceModelPo);
        }
        return effectCount > 0;
    }

    public List<DeviceModelVo> selectList(DeviceModelQueryRequest request) {

        DeviceModelPo queryDeviceModelPo = new DeviceModelPo();
        queryDeviceModelPo.setName(request.getName());
        queryDeviceModelPo.setCustomerId(request.getCustomerId());
        queryDeviceModelPo.setProductId(request.getProductId());
        queryDeviceModelPo.setTypeId(request.getTypeId());
        queryDeviceModelPo.setStatus(request.getStatus());

        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();

        List<DeviceModelPo> deviceModelPos = deviceModelMapper.selectList(queryDeviceModelPo,limit,offset);
        return deviceModelPos.stream().map(deviceModelPo -> {
            DeviceModelVo deviceModelVo = new DeviceModelVo();
            deviceModelVo.setName(deviceModelPo.getName());
            deviceModelVo.setCustomerId(deviceModelPo.getCustomerId());
            deviceModelVo.setProductId(deviceModelPo.getProductId());
            deviceModelVo.setTypeId(deviceModelPo.getTypeId());
            deviceModelVo.setRemark(deviceModelPo.getRemark());
            deviceModelVo.setStatus(deviceModelPo.getStatus());
            deviceModelVo.setVersion(deviceModelPo.getVersion());
            deviceModelVo.setIcon(deviceModelPo.getIcon());
            deviceModelVo.setId(deviceModelPo.getId());
            return deviceModelVo;
        }).collect(Collectors.toList());
    }

    /**
     * 根据类型ID 查询型号列表
     * @param request
     * @return
     */
    public List<DeviceModelVo> selectByTypeId(DeviceModelQueryRequest request) {

        DeviceModelPo queryDeviceModelPo = new DeviceModelPo();
        queryDeviceModelPo.setTypeId(request.getTypeId());

        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();

        List<DeviceModelPo> deviceModelPos = deviceModelMapper.selectList(queryDeviceModelPo,limit,offset);
        return deviceModelPos.stream().map(deviceModelPo -> {
            DeviceModelVo deviceModelVo = new DeviceModelVo();
            deviceModelVo.setName(deviceModelPo.getName());
            deviceModelVo.setCustomerId(deviceModelPo.getCustomerId());
            deviceModelVo.setProductId(deviceModelPo.getProductId());
            deviceModelVo.setTypeId(deviceModelPo.getTypeId());
            deviceModelVo.setRemark(deviceModelPo.getRemark());
            deviceModelVo.setStatus(deviceModelPo.getStatus());
            deviceModelVo.setVersion(deviceModelPo.getVersion());
            deviceModelVo.setIcon(deviceModelPo.getIcon());
            deviceModelVo.setId(deviceModelPo.getId());
            return deviceModelVo;
        }).collect(Collectors.toList());
    }

    public DeviceModelVo selectById(DeviceModelQueryRequest request) {

        DeviceModelPo queryDeviceModelPo = new DeviceModelPo();
        queryDeviceModelPo.setTypeId(request.getTypeId());

        DeviceModelPo deviceModelPo = deviceModelMapper.selectById(queryDeviceModelPo);

        DeviceModelVo deviceModelVo = new DeviceModelVo();
        deviceModelVo.setName(deviceModelPo.getName());
        deviceModelVo.setCustomerId(deviceModelPo.getCustomerId());
        deviceModelVo.setProductId(deviceModelPo.getProductId());
        deviceModelVo.setTypeId(deviceModelPo.getTypeId());
        deviceModelVo.setRemark(deviceModelPo.getRemark());
        deviceModelVo.setStatus(deviceModelPo.getStatus());
        deviceModelVo.setVersion(deviceModelPo.getVersion());
        deviceModelVo.setIcon(deviceModelPo.getIcon());
        deviceModelVo.setId(deviceModelPo.getId());

        return deviceModelVo;
    }
//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
