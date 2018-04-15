package com.huanke.iot.api.service.device.basic;

import com.google.common.collect.Lists;
import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.dao.impl.user.AppUserMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
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
        if (StringUtils.equals("bind", event))
            if (appUserPo == null) {
                AppUserPo newUserPo = new AppUserPo();
                newUserPo.setCreateTime(System.currentTimeMillis());
                newUserPo.setOpenId(openId);
                appUserMapper.insert(newUserPo);
                userId = newUserPo.getId();
            }
        DevicePo uppdatePo = new DevicePo();
        uppdatePo.setBindTime(System.currentTimeMillis());
        uppdatePo.setId(devicePo.getId());
        uppdatePo.setBindStatus(2);
        deviceMapper.updateById(uppdatePo);
        try {
            DeviceGroupItemPo queryItemPo = new DeviceGroupItemPo();
            queryItemPo.setDeviceId(devicePo.getId());
            queryItemPo.setUserId(userId);
            Integer count = deviceGroupMapper.queryItemCount(queryItemPo);
            if (count == 0) {
                DeviceGroupItemPo insertDeviceGroupItemPo = queryItemPo;
                insertDeviceGroupItemPo.setGroupId(0);
                insertDeviceGroupItemPo.setStatus(1);
                insertDeviceGroupItemPo.setUserId(userId);
                insertDeviceGroupItemPo.setCreateTime(System.currentTimeMillis());
                deviceGroupMapper.insertGroupItem(insertDeviceGroupItemPo);
            } else if (StringUtils.equals("unbind", event)) {
                DevicePo updatePo = new DevicePo();
                updatePo.setBindStatus(3);
                updatePo.setId(devicePo.getId());
                deviceMapper.updateById(updatePo);
                if (appUserPo == null) {
                    return;
                }
                userId = appUserPo.getId();
                Integer dId = devicePo.getId();
                deviceGroupMapper.updateGroupItemStatus(dId, userId);
            }
        } catch (Exception e) {
            log.error("", e);
        }
    }
}
