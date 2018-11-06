package com.huanke.iot.api.service.device.basic;

import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.customer.WxConfigMapper;
import com.huanke.iot.base.dao.device.*;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DeviceCustomerRelationPo;
import com.huanke.iot.base.po.device.DeviceCustomerUserRelationPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private DeviceCustomerRelationMapper deviceCustomerRelationMapper;

    @Autowired
    private WxConfigMapper wxConfigMapper;

    // reqMap = {DeviceType=gh_7f3ba47c70a3, DeviceID=gh_7f3ba47c70a3_f1c1cd2015ab27b6, Con
    // tent=, CreateTime=1523200569, Event=unbind, ToUserName=gh_7f3ba47c70a3, FromUserName=okOTjwpDwxJR666hVWnj_L_jp87w, MsgType=device_event, SessionID=0, OpenID=okOTjwpDwxJR666hVWnj_L_jp87w}
    public void handlerDeviceEvent(HttpServletRequest request, Map<String, String> requestMap, String event) {
        String status = requestMap.get("status");
        if(status != null && status.equals("huanke")){
            log.info("环可内部调用设备。。。");
        }else{
            log.info("微信post调用。。。");
        }
        String openId = requestMap.get("OpenID");
        String wxDeviceId = requestMap.get("DeviceID");
        DevicePo devicePo = deviceMapper.selectByWxDeviceId(wxDeviceId);
        if (devicePo == null) {
            log.warn("wxDeviceId = {} not in db", wxDeviceId);
            return;
        }

        Integer deviceId = devicePo.getId();
        DeviceCustomerRelationPo deviceCustomerRelationPo = deviceCustomerRelationMapper.selectByDeviceId(deviceId);
        Integer customerId = deviceCustomerRelationPo.getCustomerId();

        Integer userId = null;
        CustomerUserPo customerUserPo = customerUserMapper.selectByOpenId(openId);
        if (customerUserPo != null) {
            userId = customerUserPo.getId();
        } else {
            CustomerUserPo newCustomerUserPo = new CustomerUserPo();
            newCustomerUserPo.setCreateTime(System.currentTimeMillis());
            newCustomerUserPo.setOpenId(openId);
            newCustomerUserPo.setCustomerId(customerId);
            customerUserMapper.insert(newCustomerUserPo);
            userId = newCustomerUserPo.getId();
        }


        String defaultTeamName = wxConfigMapper.selectConfigByCustomerId(customerId).getDefaultTeamName();
        if (StringUtils.equals("bind", event)) {
            log.info("用户绑定设备，userId={}, openId={}, deviceId={}", userId, openId, deviceId);
            DeviceTeamItemPo toQueryTeamItemPo = new DeviceTeamItemPo();
            toQueryTeamItemPo.setDeviceId(deviceId);
            toQueryTeamItemPo.setUserId(userId);
            toQueryTeamItemPo.setStatus(null);
            List<DeviceTeamItemPo> deviceTeamItemPoList = deviceTeamMapper.queryTeamItems(toQueryTeamItemPo);
            if (deviceTeamItemPoList.size() > 0 && deviceTeamItemPoList.get(0).getStatus().equals(CommonConstant.STATUS_YES)) {
                log.error("该用户已拥有此设备");
                return;
            }
            DevicePo updateDevicePo = new DevicePo();
            updateDevicePo.setBindTime(System.currentTimeMillis());
            updateDevicePo.setId(devicePo.getId());
            updateDevicePo.setBindStatus(DeviceConstant.BIND_STATUS_YES);
            deviceMapper.updateById(updateDevicePo);

            List<DeviceTeamPo> deviceTeamPoList = deviceTeamMapper.selectByMasterUserId(userId);
            List<String> deviceTeamNameList = deviceTeamPoList.stream().map(deviceTeamPo -> deviceTeamPo.getName()).collect(Collectors.toList());
            int teamId = 0;
            if (deviceTeamPoList.isEmpty() || !deviceTeamNameList.contains(defaultTeamName)) {
                //根据配置创建新组
                DeviceTeamPo deviceTeamPo = new DeviceTeamPo();
                deviceTeamPo.setName(defaultTeamName);
                deviceTeamPo.setMasterUserId(userId);
                deviceTeamPo.setCreateUserId(userId);
                deviceTeamPo.setCustomerId(customerUserPo.getCustomerId());
                deviceTeamPo.setStatus(1);
                deviceTeamPo.setCreateTime(System.currentTimeMillis());
                deviceTeamPo.setTeamStatus(1);
                deviceTeamPo.setTeamType(3);
                deviceTeamPo.setCreateUserId(userId);
                deviceTeamPo.setCustomerId(customerId);
                deviceTeamMapper.insert(deviceTeamPo);
                teamId = deviceTeamPo.getId();
            } else {
                //使用默认组名称的组
                for (DeviceTeamPo deviceTeamPo : deviceTeamPoList) {
                    if(deviceTeamPo.getName().equals(defaultTeamName)){
                        teamId = deviceTeamPo.getId();
                    }
                }
            }
            //创建用户设备组关系表
            if (deviceTeamItemPoList.size() > 0 && deviceTeamItemPoList.get(0).getStatus().equals(CommonConstant.STATUS_DEL)) {
                DeviceTeamItemPo deviceTeamItemPo = deviceTeamItemPoList.get(0);
                deviceTeamItemPo.setTeamId(teamId);
                deviceTeamItemPo.setLastUpdateTime(System.currentTimeMillis());
                deviceTeamItemPo.setStatus(CommonConstant.STATUS_YES);
                deviceTeamItemMapper.updateById(deviceTeamItemPo);
            }else {
                DeviceTeamItemPo deviceTeamItemPo = new DeviceTeamItemPo();
                deviceTeamItemPo.setUserId(userId);
                deviceTeamItemPo.setDeviceId(devicePo.getId());
                deviceTeamItemPo.setTeamId(teamId);
                deviceTeamItemPo.setCreateTime(System.currentTimeMillis());
                deviceTeamItemPo.setStatus(1);
                deviceTeamItemMapper.insert(deviceTeamItemPo);
            }
            //通过设备查customerId
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
            } else {
                deviceCustomerUserRelationPo.setStatus(1);
                deviceCustomerUserRelationPo.setLastUpdateTime(System.currentTimeMillis());
                deviceCustomerUserRelationMapper.updateStatus(deviceCustomerUserRelationPo);
            }
        } else if (StringUtils.equals("unbind", event)) {
            log.info("用户解绑设备，userId={}, openId={}, deviceId={}", userId, openId, deviceId);
            if (customerUserPo == null) {
                log.error("找不到客户");
                return;
            }
            DevicePo updatePo = new DevicePo();
            updatePo.setId(devicePo.getId());
            List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPos = deviceCustomerUserRelationMapper.queryByDeviceId(deviceId);
            if(deviceCustomerUserRelationPos.size() == 0) {
                updatePo.setBindStatus(DeviceConstant.BIND_STATUS_NO);
                updatePo.setBindTime(null);
            }
            deviceMapper.updateById(updatePo);
            userId = customerUserPo.getId();
            //删除对应的设备
            deviceDataService.deleteDevice(userId, devicePo.getId());
        }

    }
}
