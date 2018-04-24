package com.huanke.iot.api.service.device.basic;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.huanke.iot.api.controller.h5.req.DeviceFuncVo;
import com.huanke.iot.api.controller.h5.response.DeviceDetailVo;
import com.huanke.iot.api.controller.h5.response.SensorDataVo;
import com.huanke.iot.api.gateway.MqttSendService;
import com.huanke.iot.api.util.FloatDataUtil;
import com.huanke.iot.base.dao.impl.UserMapper;
import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.impl.device.DeviceMapper;
import com.huanke.iot.base.dao.impl.device.DeviceRelationMapper;
import com.huanke.iot.base.dao.impl.device.DeviceTypeMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceOperLogMapper;
import com.huanke.iot.base.dao.impl.device.data.DeviceSensorDataMapper;
import com.huanke.iot.base.dao.impl.user.AppUserMapper;
import com.huanke.iot.base.enums.FuncTypeEnums;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.device.*;
import com.huanke.iot.base.po.device.data.DeviceOperLogPo;
import com.huanke.iot.base.po.device.data.DeviceSensorPo;
import com.huanke.iot.base.po.user.AppUserPo;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class DeviceDataService {

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceOperLogMapper deviceOperLogMapper;

    @Autowired
    private MqttSendService mqttSendService;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;


    @Autowired
    private DeviceRelationMapper deviceRelationMapper;

    @Autowired
    private DeviceGroupMapper deviceGroupMapper;

    @Autowired
    private AppUserMapper appUserMapper;

    @Autowired
    private DeviceSensorDataMapper deviceSensorDataMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public Boolean shareDevice(String master, Integer toId, String deviceIdStr, String token) {


        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceIdStr);
        if (devicePo == null) {
            return false;
        }
        Integer deviceId = devicePo.getId();
        AppUserPo appUserPo = appUserMapper.selectByOpenId(master);
        if (appUserPo == null) {
            return false;
        }
        String storeToken = stringRedisTemplate.opsForValue().get("user."+appUserPo.getId());
        if(StringUtils.isEmpty(storeToken) || !StringUtils.equals(storeToken,token)){
            return false;
        }
        if (appUserPo.getId().equals(toId)) {
            return false;
        }
        //TODO检查deviceId和用户是不是可以对应上的
        DeviceGroupItemPo queryGroupItemPo = new DeviceGroupItemPo();
        queryGroupItemPo.setDeviceId(deviceId);
        queryGroupItemPo.setUserId(appUserPo.getId());
        Integer itemCount = deviceGroupMapper.queryItemCount(queryGroupItemPo);
        if (itemCount == 0) {
            return false;
        }
        DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
        deviceGroupPo.setGroupName("默认组");
        deviceGroupPo.setUserId(toId);
        Integer defaultGroupId = 0;
        Integer defaultGroupCount = deviceGroupMapper.queryGroupCount(toId, "默认组");
        if (defaultGroupCount == 0) {
            DeviceGroupPo defaultGroup = new DeviceGroupPo();
            defaultGroup.setGroupName("默认组");
            defaultGroup.setUserId(toId);
            defaultGroup.setCreateTime(System.currentTimeMillis());
            deviceGroupMapper.insert(defaultGroup);
            defaultGroupId = defaultGroup.getId();
        } else {
            defaultGroupId = deviceGroupMapper.selectList(deviceGroupPo, 0, 1).get(0).getId();
        }
        DeviceGroupItemPo queryItemPo = new DeviceGroupItemPo();
        queryItemPo.setDeviceId(deviceId);
        queryItemPo.setUserId(toId);
        Integer count = deviceGroupMapper.queryItemCount(queryItemPo);
        if (count == 0) {
            DeviceGroupItemPo insertDeviceGroupItemPo = queryItemPo;
            insertDeviceGroupItemPo.setGroupId(defaultGroupId);
            insertDeviceGroupItemPo.setStatus(1);
            insertDeviceGroupItemPo.setUserId(toId);
            insertDeviceGroupItemPo.setCreateTime(System.currentTimeMillis());
            insertDeviceGroupItemPo.setIsMaster(2);
            deviceGroupMapper.insertGroupItem(insertDeviceGroupItemPo);
        }

        DeviceRelationPo queryPo = new DeviceRelationPo();
        queryPo.setDeviceId(deviceId);
        queryPo.setJoinUserId(toId);
        Integer relationCount = deviceRelationMapper.selectCount(queryPo);
        if (relationCount == 0) {

            DeviceRelationPo deviceRelationPo = new DeviceRelationPo();
            deviceRelationPo.setDeviceId(deviceId);
            deviceRelationPo.setJoinUserId(toId);
            deviceRelationPo.setMasterUserId(appUserPo.getId());
            deviceRelationPo.setStatus(1);
            deviceRelationPo.setCreateTime(System.currentTimeMillis());
            deviceRelationMapper.insert(deviceRelationPo);
        } else {
            DeviceRelationPo updatePo = new DeviceRelationPo();
            updatePo.setJoinUserId(toId);
            updatePo.setStatus(1);
            updatePo.setLastUpdateTime(System.currentTimeMillis());
            updatePo.setDeviceId(deviceId);
            deviceRelationMapper.updateStatus(updatePo);
        }
        return true;
    }

    public Boolean clearRelation(String joinOpenId, Integer userId, String deviceIdStr) {
        Boolean clear = true;
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceIdStr);
        if (devicePo == null) {
            return false;
        }
        Integer deviceId = devicePo.getId();
        AppUserPo appUserPo = appUserMapper.selectByOpenId(joinOpenId);
        if (appUserPo == null) {
            return false;
        }
        if (appUserPo.getId().equals(userId)) {
            return false;
        }

        DeviceGroupItemPo queryItemPo = new DeviceGroupItemPo();
        queryItemPo.setUserId(userId);
        queryItemPo.setDeviceId(deviceId);

        List<DeviceGroupItemPo> groupItemPos = deviceGroupMapper.queryGroupItems(queryItemPo);
        if (groupItemPos.size() == 0) {
            return false;
        }
        DeviceGroupItemPo deviceGroupItemPo = groupItemPos.get(0);
        if (deviceGroupItemPo.getIsMaster() != 1) {
            return false;
        }
        DeviceRelationPo deviceRelationPo = new DeviceRelationPo();
        deviceRelationPo.setLastUpdateTime(System.currentTimeMillis());
        deviceRelationPo.setDeviceId(deviceId);
        deviceRelationPo.setStatus(2);

        deviceRelationPo.setJoinUserId(appUserPo.getId());

        Integer updateCount = deviceRelationMapper.updateStatus(deviceRelationPo);
        return updateCount > 0;
    }

    public List<SensorDataVo> getHistoryData(String deviceId, Integer type) {
        Long startTimestamp = new DateTime().plusDays(-1).getMillis();
        Long endTimeStamp = System.currentTimeMillis();

        List<SensorDataVo> sensorDataVos = Lists.newArrayList();

        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
        if(devicePo == null){
            return null;
        }
        Integer deviceTypeId = devicePo.getDeviceTypeId();
        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(deviceTypeId);
        if(devicePo == null){
            return null;
        }
        String sensorList = deviceTypePo.getSensorList();
        String sensorTypes [] = sensorList.split(",");

        for(String sensorType:sensorTypes){
            SensorDataVo sensorDataVo = new SensorDataVo();
            SensorTypeEnums sensorTypeEnums = SensorTypeEnums.getByCode(sensorType);
            sensorDataVo.setName(sensorTypeEnums.getMark());
            sensorDataVo.setUnit(sensorTypeEnums.getUnit());
            sensorDataVo.setType(sensorType);
            List<String> xdata = Lists.newArrayList();
            List<String> ydata = Lists.newArrayList();
            for(Long t = startTimestamp; t < endTimeStamp;t+=1000*60*60){
                DeviceSensorPo deviceSensorPo =  deviceSensorDataMapper.selectData(devicePo.getId(),t,t+1000*60*60,sensorType);
                xdata.add(String.valueOf(t));
                if(deviceSensorPo != null){
                    ydata.add(deviceSensorPo.getSensorValue().toString());
                }else{
                    ydata.add("");
                }
            }
            sensorDataVo.setXdata(xdata);
            sensorDataVo.setYdata(ydata);
            sensorDataVos.add(sensorDataVo);
        }

        return sensorDataVos;
    }

    @Data
    public static class FuncItemMessage {
        private String type;
        private String value;
    }

    @Data
    public static class FuncListMessage {
        private String msg_id;
        private String msg_type;
        private List<DeviceDataService.FuncItemMessage> datas;
    }

    public DeviceDetailVo queryDetailByDeviceId(String deviceId) {
        DeviceDetailVo deviceDetailVo = new DeviceDetailVo();
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
        if (devicePo != null) {
            getIndexData(deviceDetailVo, devicePo.getId(), devicePo.getDeviceTypeId());
        }

        return deviceDetailVo;
    }

    public String sendFunc(DeviceFuncVo deviceFuncVo) {
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceFuncVo.getDeviceId());
        if (devicePo != null) {
            Integer deviceId = devicePo.getId();
            String topic = "/down/control/" + deviceId;
            String requestId = UUID.randomUUID().toString().replace("-", "");
            DeviceOperLogPo deviceOperLogPo = new DeviceOperLogPo();
            deviceOperLogPo.setFuncId(deviceFuncVo.getFuncId());
            deviceOperLogPo.setDeviceId(deviceId);
            deviceOperLogPo.setFuncValue(deviceFuncVo.getValue());
            deviceOperLogPo.setRequestId(requestId);
            deviceOperLogPo.setCreateTime(System.currentTimeMillis());
            deviceOperLogMapper.insert(deviceOperLogPo);
            FuncListMessage funcListMessage = new FuncListMessage();
            funcListMessage.setMsg_type("control");
            funcListMessage.setMsg_id(requestId);
            FuncItemMessage funcItemMessage = new FuncItemMessage();
            funcItemMessage.setType(deviceFuncVo.getFuncId());
            funcItemMessage.setValue(deviceFuncVo.getValue());
            funcListMessage.setDatas(Lists.newArrayList(funcItemMessage));
            mqttSendService.sendMessage(topic, JSON.toJSONString(funcListMessage));
            return requestId;
        }
        return "";
    }

    private void getIndexData(DeviceDetailVo deviceDetailVo, Integer deviceId, Integer deviceTypeId) {

        Map<Object, Object> datas = stringRedisTemplate.opsForHash().entries("sensor." + deviceId);
        Map<Object, Object> controlDatas = stringRedisTemplate.opsForHash().entries("control." + deviceId);

        DeviceDetailVo.PmDataItem pm = new DeviceDetailVo.PmDataItem();
        pm.setData(getData(datas, SensorTypeEnums.PM25_IN.getCode()));
        pm.setUnit(SensorTypeEnums.PM25_IN.getUnit());
        String data = pm.getData();
        if (StringUtils.isNotEmpty(data)) {
            Integer diData = Integer.valueOf(data);
            if (diData >= 0 && diData <= 35) {
                pm.setMass("优");
            } else if (diData > 35 && diData <= 75) {
                pm.setMass("良");
            } else if (diData > 75 && diData <= 150) {
                pm.setMass("中");
            } else {
                pm.setMass("差");
            }
        } else {
            pm.setMass("");
        }
        deviceDetailVo.setPm(pm);

        DeviceDetailVo.SysDataItem co2 = new DeviceDetailVo.SysDataItem();
        co2.setData(getData(datas, SensorTypeEnums.CO2_IN.getCode()));
        co2.setUnit(SensorTypeEnums.CO2_IN.getUnit());
        deviceDetailVo.setCo2(co2);


        DeviceDetailVo.SysDataItem tvoc = new DeviceDetailVo.SysDataItem();
        tvoc.setUnit(SensorTypeEnums.TVOC_IN.getUnit());
        String tvocData = getData(datas, SensorTypeEnums.TVOC_IN.getCode());
        if (StringUtils.isNotEmpty(tvocData)) {
            Integer digData = Integer.valueOf(tvocData);
            tvoc.setData(FloatDataUtil.getFloat(digData));
        } else {
            tvoc.setData("0");
        }
        deviceDetailVo.setTvoc(tvoc);

        DeviceDetailVo.SysDataItem hcho = new DeviceDetailVo.SysDataItem();
        hcho.setUnit(SensorTypeEnums.HCHO_IN.getUnit());
        String hchoData = getData(datas, SensorTypeEnums.HCHO_IN.getCode());
        if (StringUtils.isNotEmpty(hchoData)) {
            Integer digData = Integer.valueOf(hchoData);
            hcho.setData(FloatDataUtil.getFloat(digData));
        } else {
            hcho.setData("0");
        }
        deviceDetailVo.setHcho(hcho);

        DeviceDetailVo.SysDataItem tem = new DeviceDetailVo.SysDataItem();
        tem.setData(getData(datas, SensorTypeEnums.TEMPERATURE_IN.getCode()));
        tem.setUnit(SensorTypeEnums.TEMPERATURE_IN.getUnit());
        deviceDetailVo.setTem(tem);


        DeviceDetailVo.SysDataItem hum = new DeviceDetailVo.SysDataItem();
        hum.setData(getData(datas, SensorTypeEnums.HUMIDITY_IN.getCode()));
        hum.setUnit(SensorTypeEnums.HUMIDITY_IN.getUnit());
        deviceDetailVo.setHum(hum);

        DeviceDetailVo.SysDataItem screen = new DeviceDetailVo.SysDataItem();
        screen.setData(getData(controlDatas, FuncTypeEnums.TIMER_SCREEN.getCode()));
        screen.setUnit("秒");
        deviceDetailVo.setScreen(screen);

        DeviceDetailVo.SysDataItem remain = new DeviceDetailVo.SysDataItem();
        remain.setData(getData(controlDatas, FuncTypeEnums.TIMER_REMAIN.getCode()));
        remain.setUnit("秒");
        deviceDetailVo.setRemain(remain);

        DeviceDetailVo.DataItem modeItem = new DeviceDetailVo.DataItem();
        modeItem.setChoice(FuncTypeEnums.MODE.getRange());
        modeItem.setType(FuncTypeEnums.MODE.getCode());
        String modeValue = getData(controlDatas, FuncTypeEnums.MODE.getCode());
        modeItem.setValue(modeValue);

        deviceDetailVo.setModeItem(modeItem);

        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(deviceTypeId);
        if (deviceTypePo != null) {
            String funcTypeList = deviceTypePo.getFuncList();
            List<String> winds = getType(FuncTypeEnums.WIND1.getCode().substring(0, 2), funcTypeList);
            List<DeviceDetailVo.OtherItem> dataItems = winds.stream().map(wind -> {
                DeviceDetailVo.OtherItem dataItem = new DeviceDetailVo.OtherItem();
                dataItem.setType(wind);
                dataItem.setChoice("1:一档风速,2:二档风速,3:三档风速,4:四挡风速,5:五档风速");
                dataItem.setValue(getData(controlDatas, wind));
                if (winds.size() == 1) {
                    dataItem.setName("风速");
                } else {
                    dataItem.setName(FuncTypeEnums.getByCode(wind).getMark());
                }
                return dataItem;
            }).collect(Collectors.toList());
            deviceDetailVo.setWindItems(dataItems);
            List<JSONArray> jsonArrays = Lists.newArrayList();
            List<String> uvList = getType(FuncTypeEnums.UV.getCode().substring(0, 2), funcTypeList);
            if (uvList.size() > 0) {
                List<DeviceDetailVo.OtherItem> uvItems = uvList.stream().map(uv -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(uv).getMark());
                    otherItem.setType(uv);
                    otherItem.setValue(getData(controlDatas, uv));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray uv = new JSONArray();
                uv.addAll(uvItems);
                jsonArrays.add(uv);
            }

            List<String> anoins = getType(FuncTypeEnums.ANION.getCode().substring(0, 2), funcTypeList);
            if (anoins.size() > 0) {
                List<DeviceDetailVo.OtherItem> anoinsItems = anoins.stream().map(anoin -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(anoin).getMark());
                    otherItem.setType(anoin);
                    otherItem.setValue(getData(controlDatas, anoin));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(anoinsItems);
                jsonArrays.add(array);
            }

            List<String> warms = getType(FuncTypeEnums.WARM.getCode().substring(0, 2), funcTypeList);
            if (warms.size() > 0) {
                List<DeviceDetailVo.OtherItem> warmItems = warms.stream().map(warm -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(warm).getMark());
                    otherItem.setType(warm);
                    otherItem.setValue(getData(controlDatas, warm));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(warmItems);
                jsonArrays.add(array);
            }

            List<String> humList = getType(FuncTypeEnums.HUMIDIFER.getCode().substring(0, 2), funcTypeList);
            if (humList.size() > 0) {
                List<DeviceDetailVo.OtherItem> humItems = humList.stream().map(humStr -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(humStr).getMark());
                    otherItem.setType(humStr);
                    otherItem.setValue(getData(controlDatas, humStr));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(humItems);
                jsonArrays.add(array);
            }

            List<String> deHumList = getType(FuncTypeEnums.DEHUMIDIFER.getCode().substring(0, 2), funcTypeList);
            if (deHumList.size() > 0) {
                List<DeviceDetailVo.OtherItem> dehumItems = deHumList.stream().map(dehum -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(dehum).getMark());
                    otherItem.setType(dehum);
                    otherItem.setValue(getData(controlDatas, dehum));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(dehumItems);
                jsonArrays.add(array);
            }

            List<String> valves = getType(FuncTypeEnums.VALVE1.getCode().substring(0, 2), funcTypeList);
            if (valves.size() > 0) {
                List<DeviceDetailVo.OtherItem> valvesItems = valves.stream().map(valve -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(valve).getMark());
                    otherItem.setType(valve);
                    otherItem.setValue(getData(controlDatas, valve));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(valvesItems);
                jsonArrays.add(array);
            }

            List<String> frankList = getType(FuncTypeEnums.FRANKLINISM.getCode().substring(0, 2), funcTypeList);
            if (frankList.size() > 0) {
                List<DeviceDetailVo.OtherItem> frankItems = frankList.stream().map(frank -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(frank).getMark());
                    otherItem.setType(frank);
                    otherItem.setValue(getData(controlDatas, frank));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(frankItems);
                jsonArrays.add(array);
            }

            List<String> heatList = getType(FuncTypeEnums.HEAT.getCode().substring(0, 2), funcTypeList);
            if (heatList.size() > 0) {
                List<DeviceDetailVo.OtherItem> heatItems = heatList.stream().map(heat -> {
                    DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                    otherItem.setName(FuncTypeEnums.getByCode(heat).getMark());
                    otherItem.setType(heat);
                    otherItem.setValue(getData(controlDatas, heat));
                    return otherItem;
                }).collect(Collectors.toList());
                JSONArray array = new JSONArray();
                array.addAll(heatItems);
                jsonArrays.add(array);
            }
            deviceDetailVo.setFuncs(jsonArrays);


            List<DeviceDetailVo.OtherItem> timers = Lists.newArrayList();
            deviceDetailVo.setTimers(timers);
            deviceDetailVo.setFuncs(jsonArrays);

            List<String> openList = getType(FuncTypeEnums.TIMER_OEPN.getCode().substring(0, 2), funcTypeList);
            if (openList.size() > 0) {
                String open = openList.get(0);
                DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                otherItem.setName(FuncTypeEnums.getByCode(open).getMark());
                otherItem.setType(open);
                data = getData(controlDatas, open);
                if (StringUtils.isNotEmpty(data)) {
                    otherItem.setValue(getData(controlDatas, open));
                } else {
                    otherItem.setValue("0");
                }
                timers.add(otherItem);
            }

            List<String> closeList = getType(FuncTypeEnums.TIMER_CLOSE.getCode().substring(0, 2), funcTypeList);
            if (closeList.size() > 0) {
                String close = closeList.get(0);
                DeviceDetailVo.OtherItem otherItem = new DeviceDetailVo.OtherItem();
                otherItem.setName(FuncTypeEnums.getByCode(close).getMark());
                otherItem.setType(close);
                data = getData(controlDatas, close);
                if (StringUtils.isNotEmpty(data)) {
                    otherItem.setValue(getData(controlDatas, close));
                } else {
                    otherItem.setValue("0");
                }
                timers.add(otherItem);
            }
        }
        DeviceDetailVo.DataItem childItem = new DeviceDetailVo.DataItem();
        childItem.setType(FuncTypeEnums.CHILD_LOCK.getCode());
        childItem.setChoice("0:未开,1:已开");
        childItem.setValue(getData(controlDatas, FuncTypeEnums.CHILD_LOCK.getCode()));
        deviceDetailVo.setChildItem(childItem);
    }

    private List<String> getType(String smallType, String source) {
        String[] datas = source.split(",");
        List<String> retList = Lists.newArrayList();
        for (String data : datas) {
            if (data.startsWith(smallType)) {
                retList.add(data);
            }
        }
        return retList;
    }

    private String getData(Map<Object, Object> map, String key) {
        if (map.containsKey(key)) {
            return (String) map.get(key);
        }
        return "";
    }
}
