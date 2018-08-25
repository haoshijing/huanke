package com.huanke.iot.api.service.device.basic;

import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.*;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DeviceCustomerUserRelationPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author haoshijing
 * @version 2018年04月09日 13:57
 **/
@Repository
@Slf4j
public class DeviceBindService {
    @Autowired
    private CustomerUserMapper customerUserMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceTeamMapper deviceTeamMapper;

    @Autowired
    private DeviceTeamItemMapper deviceTeamItemMapper;

    @Autowired
    private DeviceDataService deviceDataService;

    @Autowired
    private DeviceCustomerUserRelationMapper deviceCustomerUserRelationMapper;

    // reqMap = {DeviceType=gh_7f3ba47c70a3, DeviceID=gh_7f3ba47c70a3_f1c1cd2015ab27b6, Con
    // tent=, CreateTime=1523200569, Event=unbind, ToUserName=gh_7f3ba47c70a3, FromUserName=okOTjwpDwxJR666hVWnj_L_jp87w, MsgType=device_event, SessionID=0, OpenID=okOTjwpDwxJR666hVWnj_L_jp87w}
    public void handlerDeviceEvent(HttpServletRequest request,Map<String, String> requestMap, String event) {
        String openId = requestMap.get("OpenID");
        String deviceId = requestMap.get("DeviceID");
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
        if (devicePo == null) {
            log.warn("deviceId = {} not in db", deviceId);
            return;
        }
        Integer userId = null;
        CustomerUserPo customerUserPo = customerUserMapper.selectByOpenId(openId);
        if(customerUserPo != null){
            userId = customerUserPo.getId();
        }else{
            CustomerUserPo newCustomerUserPo = new CustomerUserPo();
            newCustomerUserPo.setCreateTime(System.currentTimeMillis());
            newCustomerUserPo.setOpenId(openId);
            customerUserMapper.insert(newCustomerUserPo);
            userId = newCustomerUserPo.getId();
        }

        if (StringUtils.equals("bind", event)) {
            DevicePo updateDevicePo = new DevicePo();
            updateDevicePo.setBindTime(System.currentTimeMillis());
            updateDevicePo.setId(devicePo.getId());
            updateDevicePo.setBindStatus(2);
            deviceMapper.updateById(updateDevicePo);
            DeviceTeamPo deviceTeamPo = new DeviceTeamPo();
            deviceTeamPo.setName("默认组");
            Integer defaultTeamCount = deviceTeamMapper.queryTeamCount(userId, "默认组");
            Integer defaultTeamId = 0;

            if (defaultTeamCount == 0) {
                DeviceTeamPo defaultTeam = new DeviceTeamPo();
                defaultTeam.setName("默认组");
                defaultTeam.setMasterUserId(userId);
                defaultTeam.setCreateTime(System.currentTimeMillis());
                deviceTeamMapper.insert(defaultTeam);
                defaultTeamId = defaultTeam.getId();
            }else{
                defaultTeamId = deviceTeamMapper.selectList(deviceTeamPo, 1, 0).get(0).getId();
            }
            //通过设备查customerId
            Integer customerId = deviceMapper.getCustomerId(devicePo);
            DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = new DeviceCustomerUserRelationPo();
            deviceCustomerUserRelationPo.setOpenId(openId);
            deviceCustomerUserRelationPo.setDeviceId(devicePo.getId());
            deviceCustomerUserRelationPo.setCustomerId(customerId);
            Integer count = deviceCustomerUserRelationMapper.queryRelationCount(deviceCustomerUserRelationPo);
            if (count == 0) {
                DeviceCustomerUserRelationPo newDeviceCustomerUserRelationPo = new DeviceCustomerUserRelationPo();
                newDeviceCustomerUserRelationPo.setCustomerId(customerId);
                newDeviceCustomerUserRelationPo.setDeviceId(devicePo.getId());
                newDeviceCustomerUserRelationPo.setOpenId(openId);
                deviceCustomerUserRelationMapper.insert(newDeviceCustomerUserRelationPo);
            }else{
                deviceCustomerUserRelationPo.setStatus(1);
                deviceCustomerUserRelationPo.setLastUpdateTime(System.currentTimeMillis());
                deviceCustomerUserRelationMapper.updateStatus(deviceCustomerUserRelationPo);
            }


            DeviceTeamItemPo queryItemPo = new DeviceTeamItemPo();

            queryItemPo.setDeviceId(devicePo.getId());
            queryItemPo.setTeamId(defaultTeamId);
            Integer itemCount = deviceTeamMapper.queryItemCount(queryItemPo);
            if (itemCount == 0) {
                DeviceTeamItemPo deviceTeamItemPo = new DeviceTeamItemPo();
                deviceTeamItemPo.setUserId(userId);
                deviceTeamItemPo.setDeviceId(devicePo.getId());
                deviceTeamItemPo.setTeamId(defaultTeamId);
                deviceTeamItemPo.setCreateTime(System.currentTimeMillis());
                deviceTeamItemPo.setStatus(1);

                deviceTeamItemMapper.insert(deviceTeamItemPo);
            }else{
                deviceTeamItemMapper.updateStatus(devicePo.getId(), userId, 1);
            }
        } else if (StringUtils.equals("unbind", event)) {
            if (customerUserPo == null) {
                return;
            }
            DevicePo updatePo = new DevicePo();
            updatePo.setBindStatus(3);
            updatePo.setId(devicePo.getId());
            deviceMapper.updateById(updatePo);
            userId = customerUserPo.getId();
            //删除对应的设备
            deviceDataService.deleteDevice(userId,deviceId);
        }
    }
}
