package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.controller.h5.req.ChildDeviceRequest;
import com.huanke.iot.api.controller.h5.req.DeviceIcon;
import com.huanke.iot.api.controller.h5.response.ChildDeviceVo;
import com.huanke.iot.api.controller.h5.response.DeviceIconItem;
import com.huanke.iot.api.gateway.MqttSendService;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.WxConfigMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.dao.format.WxFormatMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.WxConfigPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import com.huanke.iot.base.po.format.WxFormatPo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-11 上午10:00
 */
@Repository
@Slf4j
public class DeviceHighService {
    @Autowired
    private WxConfigMapper wxConfigMapper;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceModelMapper deviceModelMapper;
    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private WxFormatMapper wxFormatMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private MqttSendService mqttSendService;

    public String getHighToken(Integer customerId, String password) throws Exception {
        WxConfigPo wxConfigPo = wxConfigMapper.getByJoinId(customerId, password);
        if (wxConfigPo == null) {
            throw new Exception("密码错误");
        }
        String token = UUID.randomUUID().toString().replace("-", "");
        stringRedisTemplate.opsForValue().set(token, customerId + "-" + password);
        stringRedisTemplate.expire(token, 10, TimeUnit.HOURS);
        return token;
    }

    /**
     * 添加从设备
     *
     * @param request
     */
    @Transactional
    public Integer addChildDevice(ChildDeviceRequest request) throws Exception {
        String childDeviceName = request.getDeviceName();
        Integer hostDeviceId = request.getHostDeviceId();
        String childId = request.getChildId();
        Integer modelId = request.getModelId();
        Integer typeId = deviceModelMapper.selectById(modelId).getTypeId();

        DevicePo devicePo = deviceMapper.getByHostDeviceIdAndTypeId(hostDeviceId, childId);
        if (devicePo != null) {
            if (devicePo.getStatus().equals(CommonConstant.STATUS_YES)) {
                throw new Exception("设备地址已存在");
            }
        } else {
            devicePo = new DevicePo();
        }
        devicePo.setName(childDeviceName);
        devicePo.setChildId(childId);
        devicePo.setHostDeviceId(hostDeviceId);
        devicePo.setModelId(modelId);
        devicePo.setTypeId(typeId);
        devicePo.setLastUpdateTime(System.currentTimeMillis());
        devicePo.setStatus(CommonConstant.STATUS_YES);
        Integer childDeviceId = addOrUpdate(devicePo);
        sendMb(hostDeviceId);
        return childDeviceId;
    }

    private Integer addOrUpdate(DevicePo devicePo) {
        if (devicePo.getId() == null) {
            devicePo.setCreateTime(System.currentTimeMillis());
            deviceMapper.insert(devicePo);
        } else {
            devicePo.setLastUpdateTime(System.currentTimeMillis());
            deviceMapper.updateById(devicePo);
        }
        return devicePo.getId();
    }
    public void deleteById(Integer childDeviceId) {
        DevicePo devicePo = deviceMapper.selectById(childDeviceId);
        Integer hostDeviceId = devicePo.getHostDeviceId();
        deviceMapper.deleteById(childDeviceId);
        sendMb(hostDeviceId);
    }
    /**
     * 从设备列表
     *
     * @param hostDeviceId
     * @return
     */
    public List<ChildDeviceVo> childDeviceList(Integer hostDeviceId,Integer customerId) {
        DevicePo hostDevice = deviceMapper.selectById(hostDeviceId);
        CustomerPo customerPo = customerMapper.selectById(customerId);
        Integer powerStatus = hostDevice.getPowerStatus();
        List<DevicePo> devicePoList = deviceMapper.selectChildDeviceListByHostDeviceId(hostDeviceId);
        List<ChildDeviceVo> childDeviceVos = new ArrayList<>();
        for (DevicePo devicePo : devicePoList) {
            ChildDeviceVo childDeviceVo = new ChildDeviceVo();
            childDeviceVo.setId(devicePo.getId());
            childDeviceVo.setDeviceName(devicePo.getName());
            childDeviceVo.setChildId(devicePo.getChildId());
            childDeviceVo.setOnlineStatus(devicePo.getOnlineStatus());
            childDeviceVo.setHostPowerStatus(powerStatus);
            String location = devicePo.getLocation();
            if(StringUtils.isEmpty(location)){
                location = "";
            }
            childDeviceVo.setLocation(location);
            childDeviceVo.setPowerStatus(devicePo.getPowerStatus());
            childDeviceVos.add(childDeviceVo);
            Integer modelId = devicePo.getModelId();
            DeviceModelPo deviceModelPo = deviceModelMapper.selectById(modelId);
            Integer typeId = deviceModelPo.getTypeId();
            if(StringUtils.isNotEmpty(deviceModelPo.getFormatId().toString())) {
                WxFormatPo wxFormatPo = wxFormatMapper.selectById(deviceModelPo.getFormatId());
                childDeviceVo.setFormatName(wxFormatPo.getName());
                childDeviceVo.setIcon(deviceModelPo.getIconList());
                childDeviceVo.setDeviceModelName(deviceModelPo.getName());
            }
            DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(typeId);

            childDeviceVo.setDeviceTypeName(deviceTypePo.getName());
            childDeviceVo.setCustomerName(customerPo.getName());
        }
        childDeviceVos.sort((c1,c2)->{
            return c1.getChildId().compareTo(c2.getChildId());
        });
        return childDeviceVos;
    }

    public Boolean editManageName(Integer deviceId, String manageName) {
        DevicePo devicePo = new DevicePo();
        devicePo.setId(deviceId);
        devicePo.setManageName(manageName);
        return deviceMapper.updateById(devicePo) > 0;
    }
    public Boolean sendMb(Integer deviceId){
        DevicePo currentDevicePo = deviceMapper.selectById(deviceId);
        List<Integer> childIds = deviceMapper.queryChildDeviceIds(deviceId);
        //拼接码表
        JSONObject mb = new JSONObject();
        Map<String, Object> mbMap = new HashMap<>();
        List<String> childIdList = new ArrayList<>();
        int flag = 1;
        Map<Integer, String> mMap = new HashMap<>();
        for (Integer childDeviceId : childIds) {
            DevicePo devicePo = deviceMapper.selectById(childDeviceId);
            String childId = devicePo.getChildId();
            childIdList.add(childId);
            Integer modelId = devicePo.getModelId();
            Integer typeId = deviceModelMapper.selectById(modelId).getTypeId();
            if(mMap.containsKey(typeId)){
                String mName = mMap.get(typeId);
                mbMap.put(childId, mName);
                continue;
            }
            DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(typeId);
            String stopWatch = deviceTypePo.getStopWatch();
            String mName = "m" + flag;
            try{
                mbMap.put(mName, JSONObject.parseObject(stopWatch));
            }catch (Exception e){
                mbMap.put(mName, stopWatch);
            }
            mMap.put(typeId, mName);
            flag++;
            mbMap.put(childId, mName);
        }
        mbMap.put("n", childIdList);
        mb.put("mb", mbMap);
        String topic = "/down2/stopWatch/" + deviceId;
        mqttSendService.sendMessage(topic, mb.toString(), currentDevicePo.isOldDevice());
        return true;
    }

    public List<DeviceIconItem> queryDeviceIcon(Integer deviceId){
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        DeviceModelPo deviceModelPo = deviceModelMapper.selectById(devicePo.getModelId());
        List<DeviceIconItem> resp = new ArrayList<>();
        if(StringUtils.isNotEmpty(deviceModelPo.getIconList())){
            String[] icons = deviceModelPo.getIconList().split(",");
            int select;
            if(devicePo.getIconSelect()==null||devicePo.getIconSelect()>=icons.length) {
                select = 0;
            }else{
                select = devicePo.getIconSelect();
            }
            for (int i = 0 ; i < icons.length;i++){
                DeviceIconItem deviceIconItem = new DeviceIconItem();
                deviceIconItem.setIcon(icons[i]);
                deviceIconItem.setSort(i);
                if (select == i) {
                    deviceIconItem.setIsSelect(1);
                }else{
                    deviceIconItem.setIsSelect(0);
                }
                resp.add(deviceIconItem);
            }
        }
        return resp;
    }

    public Boolean setDeviceIcon(DeviceIcon deviceIcon,Integer userId){
        DevicePo devicePo = deviceMapper.selectById(deviceIcon.getDeviceId());
        devicePo.setIconSelect(deviceIcon.getIconSelect());
        deviceMapper.updateById(devicePo);
        return true;
    }
}
