package com.huanke.iot.manage.service.device.ablity;

import com.huanke.iot.base.dao.device.ablity.DeviceAblitySetMapper;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.base.po.device.alibity.DeviceAblitySetPo;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityQueryRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblitySetQueryRequest;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblitySetVo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备功能集
 */
@Repository
@Slf4j
public class DeviceAblitySetService {

    @Autowired
    private DeviceAblitySetMapper deviceAblitySetMapper;


    /**
     * 创建或更新
     *
     * @param requestParam
     * @return
     */
    public Boolean createOrUpdate(Map<String, Object> requestParam) {

        int effectCount = 0;
        DeviceAblitySetPo deviceAblitySetPo = new DeviceAblitySetPo();

        deviceAblitySetPo.setId(Integer.parseInt((String) requestParam.get("id")));
        deviceAblitySetPo.setName((String) requestParam.get("name"));
        deviceAblitySetPo.setRemark((String) requestParam.get("remark"));
        deviceAblitySetPo.setStatus(Integer.parseInt((String) requestParam.get("status")));

        //如果id不为空，则是更新，否则是新增
        if (deviceAblitySetPo.getId() != null && deviceAblitySetPo.getId() > 0) {
            deviceAblitySetPo.setLastUpdateTime(System.currentTimeMillis());
            effectCount = deviceAblitySetMapper.updateById(deviceAblitySetPo);
        } else {
            deviceAblitySetPo.setCreateTime(System.currentTimeMillis());
            effectCount = deviceAblitySetMapper.insert(deviceAblitySetPo);
        }
        return effectCount > 0;
    }

    /**
     * 查询 能力集列表
     * @param request
     * @return
     */
    public List<DeviceAblitySetVo> selectList(DeviceAblitySetQueryRequest request) {

        DeviceAblitySetPo queryDeviceAblitySetPo = new DeviceAblitySetPo();

        queryDeviceAblitySetPo.setId(request.getId());
        queryDeviceAblitySetPo.setName(request.getName());
        queryDeviceAblitySetPo.setRemark(request.getRemark());
        queryDeviceAblitySetPo.setStatus(request.getStatus());

        Integer offset = (request.getPage() - 1) * request.getLimit();
        Integer limit = request.getLimit();

        List<DeviceAblitySetPo> deviceAblitySetPos = deviceAblitySetMapper.selectList(queryDeviceAblitySetPo, limit, offset);
        return deviceAblitySetPos.stream().map(deviceAblitySetPo -> {
            DeviceAblitySetVo deviceAblitySetVo = new DeviceAblitySetVo();

            deviceAblitySetVo.setName(deviceAblitySetPo.getName());
            deviceAblitySetVo.setRemark(deviceAblitySetPo.getRemark());
            deviceAblitySetVo.setStatus(deviceAblitySetPo.getStatus());
            deviceAblitySetVo.setId(deviceAblitySetPo.getId());
            return deviceAblitySetVo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询 能力集对象
     * @param request
     * @return
     */
    public DeviceAblitySetVo selectById(DeviceAblitySetQueryRequest request) {

        DeviceAblitySetPo queryDeviceAblitySetPo = new DeviceAblitySetPo();

        queryDeviceAblitySetPo.setId(request.getId());

        DeviceAblitySetPo deviceAblitySetPo = deviceAblitySetMapper.selectById(queryDeviceAblitySetPo.getId());

        DeviceAblitySetVo deviceAblitySetVo = new DeviceAblitySetVo();

        deviceAblitySetVo.setName(deviceAblitySetPo.getName());
        deviceAblitySetVo.setRemark(deviceAblitySetPo.getRemark());
        deviceAblitySetVo.setStatus(deviceAblitySetPo.getStatus());
        deviceAblitySetVo.setId(deviceAblitySetPo.getId());
        return deviceAblitySetVo;
    }
//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
