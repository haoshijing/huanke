package com.huanke.iot.api.service.device.basic;

import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.dao.impl.user.AppUserMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.user.AppUserPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author haoshijing
 * @version 2018年04月09日 13:57
 **/
@Repository
@Slf4j
public class DeviceBindService {

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired(required = false)
    private DeviceGroupMapper deviceGroupMapper;

    // reqMap = {DeviceType=gh_7f3ba47c70a3, DeviceID=gh_7f3ba47c70a3_f1c1cd2015ab27b6, Con
    // tent=, CreateTime=1523200569, Event=unbind, ToUserName=gh_7f3ba47c70a3, FromUserName=okOTjwpDwxJR666hVWnj_L_jp87w, MsgType=device_event, SessionID=0, OpenID=okOTjwpDwxJR666hVWnj_L_jp87w}
    public void handlerDeviceEvent(Map<String, String> requestMap, String event) {
        String openId = requestMap.get("OpenID");
        String deviceId = requestMap.get("DeviceID");
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
        if (devicePo == null) {
            log.warn("deviceId = {} not in db", deviceId);
            return;
        }
        Integer userId = null;
        AppUserPo appUserPo = appUserMapper.selectByOpenId(openId);
        if(appUserPo != null){
            userId = appUserPo.getId();
        }else{
            AppUserPo newUserPo = new AppUserPo();
            newUserPo.setCreateTime(System.currentTimeMillis());
            newUserPo.setOpenId(openId);
            appUserMapper.insert(newUserPo);
            userId = newUserPo.getId();
        }
        if (StringUtils.equals("bind", event)) {
            DevicePo updateDevicePo = new DevicePo();
            updateDevicePo.setBindTime(System.currentTimeMillis());
            updateDevicePo.setId(devicePo.getId());
            updateDevicePo.setBindStatus(2);
            deviceMapper.updateById(updateDevicePo);
            DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
            deviceGroupPo.setGroupName("默认组");
            deviceGroupPo.setUserId(userId);
            Integer defaultGroupId = 0;
            Integer defaultGroupCount = deviceGroupMapper.queryGroupCount(userId, "默认组");
            if (defaultGroupCount == 0) {
                DeviceGroupPo defaultGroup = new DeviceGroupPo();
                defaultGroup.setGroupName("默认组");
                defaultGroup.setUserId(userId);
                defaultGroup.setCreateTime(System.currentTimeMillis());
                deviceGroupMapper.insert(defaultGroup);
                defaultGroupId = defaultGroup.getId();
            }else{
                defaultGroupId = deviceGroupMapper.selectList(deviceGroupPo,0,1).get(0).getId();
            }
            DeviceGroupItemPo queryItemPo = new DeviceGroupItemPo();
            queryItemPo.setDeviceId(devicePo.getId());
            queryItemPo.setUserId(userId);
            Integer count = deviceGroupMapper.queryItemCount(queryItemPo);
            if (count == 0) {
                DeviceGroupItemPo insertDeviceGroupItemPo = queryItemPo;
                insertDeviceGroupItemPo.setGroupId(defaultGroupId);
                insertDeviceGroupItemPo.setStatus(1);
                insertDeviceGroupItemPo.setUserId(userId);
                insertDeviceGroupItemPo.setCreateTime(System.currentTimeMillis());
                deviceGroupMapper.insertGroupItem(insertDeviceGroupItemPo);
            }
        } else if (StringUtils.equals("unbind", event)) {
            if (appUserPo == null) {
                return;
            }
            DevicePo updatePo = new DevicePo();
            updatePo.setBindStatus(3);
            updatePo.setId(devicePo.getId());
            deviceMapper.updateById(updatePo);
            userId = appUserPo.getId();
            Integer dId = devicePo.getId();
            deviceGroupMapper.updateGroupItemStatus(dId, userId);
        }
    }
}
