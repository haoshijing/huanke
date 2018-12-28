package com.huanke.iot.manage.service;

import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.stat.DeviceSensorStatMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityMapper;
import com.huanke.iot.base.enums.SensorTypeEnums;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.stat.DeviceSensorStatPo;
import com.huanke.iot.manage.vo.response.device.data.SensorDataVo;
import org.assertj.core.util.Lists;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author haoshijing
 * @version 2018年05月29日 20:14
 **/

@Service
public class DashBoardIndexService {
    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceModelAbilityMapper deviceModelabilityMapper;
    @Autowired
    private DeviceSensorStatMapper deviceSensorStatMapper;

    List<String> needValues = new ArrayList<>(Arrays.asList(
            SensorTypeEnums.TEMPERATURE_IN.getCode(),
            SensorTypeEnums.HUMIDITY_IN.getCode(),
            SensorTypeEnums.PM25_IN.getCode(),
            SensorTypeEnums.CO2_IN.getCode(),
            SensorTypeEnums.HCHO_IN.getCode(),
            SensorTypeEnums.TVOC_IN.getCode(),
            SensorTypeEnums.NH3_IN.getCode(),
            SensorTypeEnums.ANION_IN.getCode()));

    public List<SensorDataVo> getHistoryData(Integer deviceId) {
        Long startTimestamp = new DateTime().plusDays(-1).getMillis();
        Long endTimeStamp = System.currentTimeMillis();
        List<SensorDataVo> sensorDataVos = Lists.newArrayList();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        if (devicePo == null) {
            return null;
        }
        Integer deviceModelId = devicePo.getModelId();
        List<DeviceAbilityPo> deviceAbilityPos = deviceModelabilityMapper.selectActiveByModelId(deviceModelId);
        List<String> dirValues = deviceAbilityPos.stream().map(deviceAbilityPo -> deviceAbilityPo.getDirValue()).collect(Collectors.toList());
        List<DeviceSensorStatPo> deviceSensorPos = deviceSensorStatMapper.selectData(devicePo.getId(), startTimestamp, endTimeStamp);
        for (String sensorType : dirValues) {
            if(!needValues.contains(sensorType))continue;
            SensorDataVo sensorDataVo = new SensorDataVo();
            SensorTypeEnums sensorTypeEnums = SensorTypeEnums.getByCode(sensorType);
            sensorDataVo.setName(sensorTypeEnums.getMark());
            sensorDataVo.setUnit(sensorTypeEnums.getUnit());
            sensorDataVo.setType(sensorType);
            Map<String,String> map = new LinkedHashMap<>();
            for (DeviceSensorStatPo deviceSensorPo : deviceSensorPos) {
                Float value;
                switch (sensorTypeEnums){
                    case CO2_IN:
                        value = Float.valueOf(deviceSensorPo.getCo2()==null?0:deviceSensorPo.getCo2());
                        break;
                    case HUMIDITY_IN:
                        value = Float.valueOf(deviceSensorPo.getHum()==null?0:deviceSensorPo.getHum());
                        break;
                    case TEMPERATURE_IN:
                        value = Float.valueOf(deviceSensorPo.getTem()==null?0:deviceSensorPo.getTem());
                        break;
                    case HCHO_IN:
                        value = Float.valueOf(deviceSensorPo.getHcho()==null?0:deviceSensorPo.getHcho())/100;
                        break;
                    case PM25_IN:
                        value = Float.valueOf(deviceSensorPo.getPm()==null?0:deviceSensorPo.getPm());
                        break;
                    case TVOC_IN:
                        value = Float.valueOf(deviceSensorPo.getTvoc()==null?0:deviceSensorPo.getTvoc())/100;
                        break;
                    case NH3_IN:
                        value = Float.valueOf(deviceSensorPo.getNh3()==null?0:deviceSensorPo.getNh3());
                        break;
                    case ANION_IN:
                        value = Float.valueOf(deviceSensorPo.getAnion()==null?0:deviceSensorPo.getAnion());
                        break;
                    default:
                        value = Float.valueOf(0);
                }
                String key = new DateTime(deviceSensorPo.getStartTime()).toString("yyyy-MM-dd HH:00:00");
                if(map.get(key)==null){
                    map.put(key,new DecimalFormat("0.00").format(value));
                }else{
                    map.put(key,new DecimalFormat("0.00").format(Float.valueOf(map.get(key))/2+Float.valueOf(value)/2));
                }
            }
            sensorDataVo.setXdata(new ArrayList<>(map.keySet()));
            sensorDataVo.setYdata(new ArrayList<>(map.values()));
            sensorDataVos.add(sensorDataVo);
        }
        return sensorDataVos;
    }


}
