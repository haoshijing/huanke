package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huanke.iot.api.controller.h5.req.ChildDeviceRequest;
import com.huanke.iot.api.controller.h5.response.ChildDeviceVo;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.WxConfigMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceTeamItemMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.po.customer.WxConfigPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
    private DeviceModelMapper deviceModelMapper;
    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private DeviceTypeMapper deviceTypeMapper;
    @Autowired
    private DeviceTeamItemMapper deviceTeamItemMapper;
    @Autowired
    private DeviceDataService deviceDataService;

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
    public Integer addChildDevice(ChildDeviceRequest request, Integer userId) throws Exception {
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
        //缓存设备码表等待设备读取
        DevicePo hostDevice = deviceMapper.selectById(hostDeviceId);
        Integer hostModelId = hostDevice.getModelId();
        Integer hostDeviceTypeId = deviceModelMapper.selectById(hostModelId).getTypeId();
        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(hostDeviceTypeId);

        String stopWatch = deviceTypePo.getStopWatch();
        if(stopWatch != null){
            JSONObject jsonObject = JSONObject.parseObject(stopWatch);
            JSONObject mb = jsonObject.getJSONObject("mb");
            JSONArray n = mb.getJSONArray("n");
            if(!n.contains(childId)){
                n.add(childId);
                mb.remove("n");
                mb.put("n",n);
                jsonObject.remove("mb");
                jsonObject.put("mb", mb);
                stopWatch = jsonObject.toString();
            }
            deviceTypeMapper.updateStopWatch(hostDeviceTypeId, stopWatch);
            deviceDataService.sendMb(hostDeviceId, stopWatch);
        }else{
            log.info("码表为空：hostDeviceId ={}, childId={}, modelId={}", hostDeviceId, childId, modelId);
        }
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
            Integer modelId = devicePo.getModelId();
            Integer typeId = deviceModelMapper.selectById(modelId).getTypeId();
            DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(typeId);
            childDeviceVo.setDeviceTypeName(deviceTypePo.getName());
        }
        return childDeviceVos;
    }
}
