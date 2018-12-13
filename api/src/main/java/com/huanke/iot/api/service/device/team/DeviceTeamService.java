package com.huanke.iot.api.service.device.team;

import com.huanke.iot.api.controller.h5.req.DeviceFuncVo;
import com.huanke.iot.api.controller.h5.req.OccRequest;
import com.huanke.iot.api.controller.h5.team.DeviceTeamNewRequest;
import com.huanke.iot.api.controller.h5.team.DeviceTeamRequest;
import com.huanke.iot.api.service.device.basic.DeviceDataService;
import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceTeamConstants;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceTeamItemMapper;
import com.huanke.iot.base.dao.device.DeviceTeamMapper;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    DeviceDataService deviceDataService;

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
            deviceTeamPo.setTeamType(DeviceTeamConstants.DEVICE_TEAM_TYPE_USER);
            deviceTeamPo.setTeamStatus(0);
            deviceTeamPo.setStatus(CommonConstant.STATUS_YES);
            deviceTeamMapper.insert(deviceTeamPo);
            teamId = deviceTeamPo.getId();
        } else {
            teamId = queryTeamPo.getId();
            DeviceTeamPo updatePo = new DeviceTeamPo();
            updatePo.setName(newRequest.getTeamName());
            updatePo.setStatus(CommonConstant.STATUS_YES);
            updatePo.setTeamType(DeviceTeamConstants.DEVICE_TEAM_TYPE_USER);
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
        log.info("添加设备至组，teamId={}，wxdeviceIds={}",teamId,deviceTeamRequest.getDeviceIds());
        if(deviceTeamMapper.selectById(teamId) == null){
            log.error("不存在的teamId={}",teamId);
            return false;
        }
        deviceTeamRequest.getDeviceIds().forEach((deviceId) -> {
                    DevicePo devicePo = deviceMapper.selectByWxDeviceId(deviceId);
                    if (devicePo != null) {
                        Integer dId = devicePo.getId();
                        deviceTeamItemMapper.updateDeviceGroupId(userId, teamId, dId);
                    }
                }
        );
        log.info("添加设备至组成功");
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
            DevicePo devicePo = deviceMapper.selectByWxDeviceId(wxDeviceId);
            DeviceTeamItemPo deviceTeamItemPo = deviceTeamItemMapper.selectByJoinId(devicePo.getId(),userId);
            deviceTeamItemPo.setTeamId(teamId);
            deviceTeamItemPo.setStatus(CommonConstant.STATUS_YES);
            deviceTeamItemPo.setLastUpdateTime(System.currentTimeMillis());
            deviceTeamItemMapper.updateById(deviceTeamItemPo);
        }
        return new ApiResponse<>(true);
    }

    @Transactional
    public Boolean occ(OccRequest occRequest, Integer userId, Integer operType) {
        Integer teamId = occRequest.getTeamId();
        //根据teamId查当前组所有可用设备
        List<DeviceTeamItemPo> deviceTeamItemPos = deviceTeamItemMapper.selectByTeamId(teamId);
        List<Integer> deviceIdList = deviceTeamItemPos.stream().filter(e -> e.getStatus().equals(CommonConstant.STATUS_YES)).map(e -> e.getDeviceId()).collect(Collectors.toList());

        List<DevicePo> deviceList = deviceMapper.selectByIdList(deviceIdList);
        String result;
        for (DevicePo device : deviceList) {
            DeviceFuncVo deviceFuncVo = new DeviceFuncVo();
            BeanUtils.copyProperties(occRequest, deviceFuncVo);
            deviceFuncVo.setDeviceId(device.getId());
            deviceDataService.sendFunc(deviceFuncVo, userId, operType);//H5操作，所以是1
            DeviceTeamItemPo deviceTeamItemPo = this.deviceTeamItemMapper.selectByDeviceId(device.getId());
            if(null != deviceTeamItemPo && deviceTeamItemPo.getLinkAgeStatus().equals(1)){
                //对其他联动设备发送指令
                List<DeviceTeamItemPo> deviceTeamItemPoList = this.deviceTeamItemMapper.selectLinkDevice(deviceTeamItemPo);
                for (DeviceTeamItemPo eachPo : deviceTeamItemPoList) {
                    DeviceFuncVo linkDeviceFuncVo = new DeviceFuncVo();
                    linkDeviceFuncVo.setDeviceId(eachPo.getDeviceId());
                    linkDeviceFuncVo.setFuncId(deviceFuncVo.getFuncId());
                    linkDeviceFuncVo.setValue(deviceFuncVo.getValue());
                    result = deviceDataService.sendFunc(linkDeviceFuncVo,userId, operType);
                    if(result.equals("")){
                        throw new BusinessException("指令发送失败");
                    }
                }
            }
        }
        return true;
    }
}
