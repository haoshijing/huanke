package com.huanke.iot.api.service.device.team;

import com.alibaba.druid.util.StringUtils;
import com.huanke.iot.api.controller.h5.team.DeviceTeamNewRequest;
import com.huanke.iot.api.controller.h5.team.DeviceTeamRequest;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceTeamMapper;
import com.huanke.iot.base.po.device.DevicePo;
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
    DeviceMapper deviceMapper;

    @Transactional
    public Integer createDeviceTeam(Integer userId, DeviceTeamNewRequest newRequest) {
        String teamName = newRequest.getTeamName();
        DeviceTeamPo queryPo = new DeviceTeamPo();
        queryPo.setName(teamName);
        queryPo.setMasterUserId(userId);
        DeviceTeamPo queryTeamPo = deviceTeamMapper.queryByName(queryPo);
        if (queryTeamPo != null && queryTeamPo.getStatus() == 1) {
            return 0;
        }
        Integer teamId = 0;
        if (queryTeamPo == null) {
            DeviceTeamPo deviceTeamPo = new DeviceTeamPo();
            deviceTeamPo.setCreateTime(System.currentTimeMillis());
            deviceTeamPo.setMasterUserId(userId);
            deviceTeamPo.setName(teamName);
            deviceTeamPo.setStatus(1);
            deviceTeamMapper.insert(deviceTeamPo);
            teamId = deviceTeamPo.getId();
        } else {
            teamId = queryTeamPo.getId();
            DeviceTeamPo updatePo = new DeviceTeamPo();
            updatePo.setName(newRequest.getTeamName());
            updatePo.setStatus(1);
            updatePo.setId(teamId);
            updatePo.setLastUpdateTime(System.currentTimeMillis());
            int ret = deviceTeamMapper.updateById(updatePo);
            log.info("ret = {}",ret);
        }
        if (!CollectionUtils.isEmpty(newRequest.getDeviceIds())) {
            DeviceTeamRequest deviceTeamRequest = new DeviceTeamRequest();
            deviceTeamRequest.setDeviceIds(newRequest.getDeviceIds());
            deviceTeamRequest.setTeamId(teamId);
            updateDeviceTeam(userId, deviceTeamRequest);
            return 1;
        }
        return 0;
    }

    public Boolean deleteTeam(Integer userId, Integer teamId) {
        DeviceTeamPo teamPo = deviceTeamMapper.selectById(teamId);
        if (teamPo != null && StringUtils.equals(teamPo.getName(), "默认组")) {
            return false;
        }
        Boolean updateRet = deviceTeamMapper.updateTeamStatus(userId, teamId, 2) > 0;
        if (updateRet) {
            DeviceTeamPo deviceTeamPo = new DeviceTeamPo();
            deviceTeamPo.setName("默认组");
            deviceTeamPo.setMasterUserId(userId);
            deviceTeamPo.setStatus(1);
            List<DeviceTeamPo> deviceTeamPos = deviceTeamMapper.selectList(deviceTeamPo, 1, 0);
            Integer defaultTeamId = 0;

            if (deviceTeamPos.size() > 0) {
                defaultTeamId = deviceTeamPos.get(0).getId();
            }
            deviceTeamMapper.updateDeviceTeamItem(userId, teamId, defaultTeamId);
        }
        return updateRet;
    }

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
}
