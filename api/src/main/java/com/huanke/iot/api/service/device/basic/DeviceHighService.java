package com.huanke.iot.api.service.device.basic;

import com.huanke.iot.api.controller.h5.req.ChildDeviceRequest;
import com.huanke.iot.api.controller.h5.response.ChildDeviceVo;
import com.huanke.iot.api.controller.h5.response.DeviceTypeVo;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.WxConfigMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.WxConfigPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
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
    private CustomerMapper customerMapper;
    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

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
    public void addChildDevice(ChildDeviceRequest request) throws Exception {
        String childDeviceName = request.getDeviceName();
        Integer hostDeviceId = request.getHostDeviceId();
        String childId = request.getChildId();
        Integer typeId = request.getTypeId();

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
        devicePo.setTypeId(typeId);
        devicePo.setLastUpdateTime(System.currentTimeMillis());
        devicePo.setStatus(CommonConstant.STATUS_YES);
        DevicePo hostDevice = deviceMapper.selectById(hostDeviceId);
        devicePo.setModelId(hostDevice.getModelId());
        addOrUpdate(devicePo);
    }

    private void addOrUpdate(DevicePo devicePo) {
        if (devicePo.getId() == null) {
            devicePo.setCreateTime(System.currentTimeMillis());
            deviceMapper.insert(devicePo);
        } else {
            devicePo.setLastUpdateTime(System.currentTimeMillis());
            deviceMapper.updateById(devicePo);
        }
    }

    /**
     * 从设备列表
     *
     * @param hostDeviceId
     * @return
     */
    public List<ChildDeviceVo> childDeviceList(Integer hostDeviceId) {
        List<DevicePo> devicePoList = deviceMapper.selectChildDeviceListByHostDeviceId(hostDeviceId);
        List<ChildDeviceVo> childDeviceVos = new ArrayList<>();
        for (DevicePo devicePo : devicePoList) {
            ChildDeviceVo childDeviceVo = new ChildDeviceVo();
            childDeviceVo.setId(devicePo.getId());
            childDeviceVo.setDeviceName(devicePo.getName());
            childDeviceVo.setChildId(devicePo.getChildId());
            childDeviceVos.add(childDeviceVo);
        }
        return childDeviceVos;
    }

    public List<DeviceTypeVo> getTypeListByCustomerId(Integer customerId) {
        List<DeviceTypeVo> deviceTypeVoList = new ArrayList<>();
        CustomerPo customerPo = customerMapper.selectById(customerId);
        String typeIds = customerPo.getTypeIds();
        String[] types = typeIds.split(",");
        for (String typeId : types) {
            DeviceTypeVo deviceTypeVo = new DeviceTypeVo();
            DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(Integer.valueOf(typeId));
            deviceTypeVo.setId(Integer.valueOf(typeId));
            deviceTypeVo.setName(deviceTypePo.getName());
            deviceTypeVoList.add(deviceTypeVo);
        }
        return deviceTypeVoList;
    }
}