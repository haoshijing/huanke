package com.huanke.iot.api.service.device.team;

import com.huanke.iot.api.controller.h5.team.DeviceTeamNewRequest;
import com.huanke.iot.api.controller.h5.team.DeviceTeamRequest;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceTeamItemMapper;
import com.huanke.iot.base.dao.device.DeviceTeamMapper;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author onlymark
 * @version 2018年08月22日
 **/
@Repository
@Slf4j
public class DeviceTeamService {

    @Autowired
    DeviceTeamMapper deviceTeamMapper;

    @Autowired
    DeviceTeamItemMapper deviceTeamItemMapper;

    @Autowired
    DeviceMapper deviceMapper;

    @Autowired
    CustomerUserMapper customerUserMapper;

    @Transactional
    public Object createDeviceTeam(Integer userId, DeviceTeamNewRequest newRequest) {
        CustomerUserPo customerUserPo = customerUserMapper.selectById(userId);
        int customerId = customerUserPo.getCustomerId();
        String teamName = newRequest.getTeamName();
        DeviceTeamPo queryPo = new DeviceTeamPo();
        queryPo.setName(teamName);
        queryPo.setMasterUserId(userId);
        DeviceTeamPo queryTeamPo = deviceTeamMapper.queryByName(queryPo);
        if (queryTeamPo != null && queryTeamPo.getStatus() == 1) {
            return new ApiResponse<>(RetCode.ERROR, "该设备组已存在");
        }
        Integer teamId = 0;
        if (queryTeamPo == null) {
            DeviceTeamPo deviceTeamPo = new DeviceTeamPo();
            deviceTeamPo.setCreateTime(System.currentTimeMillis());
            deviceTeamPo.setMasterUserId(userId);
            deviceTeamPo.setCreateUserId(userId);
            deviceTeamPo.setCustomerId(customerId);
            deviceTeamPo.setName(teamName);
            deviceTeamPo.setTeamType(3);
            deviceTeamPo.setTeamStatus(0);
            deviceTeamPo.setStatus(CommonConstant.STATUS_YES);
            deviceTeamMapper.insert(deviceTeamPo);
            teamId = deviceTeamPo.getId();
        } else {
            teamId = queryTeamPo.getId();
            DeviceTeamPo updatePo = new DeviceTeamPo();
            updatePo.setName(newRequest.getTeamName());
            updatePo.setStatus(CommonConstant.STATUS_YES);
            updatePo.setTeamType(3);
            updatePo.setCreateUserId(userId);
            updatePo.setCustomerId(customerId);
            updatePo.setTeamStatus(0);
            updatePo.setId(teamId);
            updatePo.setLastUpdateTime(System.currentTimeMillis());
            int ret = deviceTeamMapper.updateById(updatePo);
            log.info("ret = {}", ret);
        }
        if (!CollectionUtils.isEmpty(newRequest.getWxDeviceIds())) {
            DeviceTeamRequest deviceTeamRequest = new DeviceTeamRequest();
            deviceTeamRequest.setDeviceIds(newRequest.getWxDeviceIds());
            deviceTeamRequest.setTeamId(teamId);
            updateDeviceTeam(userId, deviceTeamRequest);
        }
        return new ApiResponse<>(teamId);
    }

    public Object deleteTeam(Integer userId, Integer teamId) {
        DeviceTeamPo teamPo = deviceTeamMapper.selectById(teamId);
        //判断是否默认组

        DeviceTeamItemPo queryTeamItem = new DeviceTeamItemPo();
        queryTeamItem.setStatus(CommonConstant.STATUS_YES);
        queryTeamItem.setTeamId(teamId);
        Integer count = deviceTeamMapper.queryItemCount(queryTeamItem);
        if (count > 0) {
            return new ApiResponse<>(RetCode.ERROR, "该组下有设备，无法删除");
        }
        Boolean updateRet = deviceTeamMapper.updateTeamStatus(userId, teamId, 2) > 0;

        return new ApiResponse<>(updateRet);
    }

    @Transactional
    public Boolean updateDeviceTeam(Integer userId, DeviceTeamRequest deviceTeamRequest) {
        final Integer teamId = deviceTeamRequest.getTeamId();
        deviceTeamRequest.getDeviceIds().forEach((deviceId) -> {
                    DevicePo devicePo = deviceMapper.selectByWxDeviceId(deviceId);
                    if (devicePo != null) {
                        Integer dId = devicePo.getId();
                        deviceTeamMapper.updateDeviceGroupId(userId, teamId, dId);
                    }
                }
        );
        return true;
    }

    public Boolean updateTeamName(Integer userId, Integer teamId, String teamName) {
        DeviceTeamPo queryPo = new DeviceTeamPo();
        queryPo.setName(teamName);
        queryPo.setMasterUserId(userId);
        DeviceTeamPo deviceTeamPo = deviceTeamMapper.queryByName(queryPo);
        if (deviceTeamPo != null && deviceTeamPo.getStatus() == 1) {
            return false;
        }
        DeviceTeamPo updatePo = new DeviceTeamPo();
        updatePo.setId(teamId);
        updatePo.setName(teamName);
        updatePo.setStatus(1);
        updatePo.setLastUpdateTime(System.currentTimeMillis());
        int ret = deviceTeamMapper.updateById(updatePo);
        return ret > 0;
    }

    public List<DeviceTeamPo> selectByUserId(Integer userId) {
        return deviceTeamMapper.selectByMasterUserId(userId);
    }

    public Object addTeamDevices(Integer userId, DeviceTeamRequest deviceTeamRequest) {
        Integer teamId = deviceTeamRequest.getTeamId();
        Integer count = deviceTeamMapper.verifyTeam(userId, teamId);
        if (count < 1) {
            return new ApiResponse<>(RetCode.ERROR, "该用户无此组");
        }
        List<String> wxDeviceIds = deviceTeamRequest.getDeviceIds();
        for (String wxDeviceId : wxDeviceIds) {
            DeviceTeamItemPo deviceTeamItemPo = new DeviceTeamItemPo();
            deviceTeamItemPo.setTeamId(teamId);
            deviceTeamItemPo.setUserId(userId);
            deviceTeamItemPo.setDeviceId(deviceMapper.selectByWxDeviceId(wxDeviceId).getId());
            deviceTeamItemPo.setStatus(CommonConstant.STATUS_YES);
            deviceTeamItemPo.setCreateTime(System.currentTimeMillis());
            deviceTeamItemMapper.insert(deviceTeamItemPo);
        }
        return new ApiResponse<>();
    }
}
