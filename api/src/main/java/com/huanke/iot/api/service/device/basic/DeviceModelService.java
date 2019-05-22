package com.huanke.iot.api.service.device.basic;

import com.huanke.iot.api.controller.h5.req.BaseRequest;
import com.huanke.iot.api.controller.h5.response.DeviceModelTypeVo;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-11 上午11:44
 */
@Repository
@Slf4j
public class DeviceModelService {
    @Autowired
    private DeviceModelMapper deviceModelMapper;

    public List<DeviceModelTypeVo> selectByCustomerId(Integer modelId) {
        List<DeviceModelTypeVo> deviceModelTypeVoList = new ArrayList<>();
        List<DeviceModelPo> deviceModelPos = deviceModelMapper.selectModelsByParentModelId(modelId);

        for (DeviceModelPo deviceModelPo : deviceModelPos) {
            DeviceModelTypeVo deviceModelTypeVo = new DeviceModelTypeVo();
            deviceModelTypeVo.setId(deviceModelPo.getId());
            deviceModelTypeVo.setName(deviceModelPo.getName());
            deviceModelTypeVoList.add(deviceModelTypeVo);
        }
        return deviceModelTypeVoList;
    }

    public List<String> getHelpFileUrls(BaseRequest<Integer> request) {
        DeviceModelPo deviceModelPo = deviceModelMapper.selectById(request.getValue());
        return Arrays.asList(deviceModelPo.getHelpFileUrl().split(","));
    }
}
