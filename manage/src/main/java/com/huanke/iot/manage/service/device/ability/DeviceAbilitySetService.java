package com.huanke.iot.manage.service.device.ability;

import com.huanke.iot.base.dao.device.ability.DeviceAbilitySetMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilitySetRelationMapper;
import com.huanke.iot.base.po.device.ability.DeviceAbilitySetPo;
import com.huanke.iot.manage.vo.request.device.ability.DeviceAbilitySetQueryRequest;
import com.huanke.iot.manage.vo.response.device.ability.DeviceAbilitySetVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 设备功能集
 */
@Repository
@Slf4j
public class DeviceAbilitySetService {

    @Autowired
    private DeviceAbilitySetMapper deviceAbilitySetMapper;

    @Autowired
    private DeviceAbilitySetRelationMapper deviceAbilitySetRelationMapper;



    /**
     * 创建或更新
     *
     * @param requestParam
     * @return
     */
    public Boolean createOrUpdate(Map<String, Object> requestParam) {

        int effectCount = 0;
        DeviceAbilitySetPo deviceAbilitySetPo = new DeviceAbilitySetPo();

        deviceAbilitySetPo.setId((Integer)(requestParam.get("id")));
        deviceAbilitySetPo.setName((String) requestParam.get("name"));
        deviceAbilitySetPo.setRemark((String) requestParam.get("remark"));
        deviceAbilitySetPo.setStatus(Integer.parseInt((String) requestParam.get("status")));

        //如果id不为空，则是更新，否则是新增
        if (deviceAbilitySetPo.getId() != null && deviceAbilitySetPo.getId() > 0) {
            deviceAbilitySetPo.setLastUpdateTime(System.currentTimeMillis());
            effectCount = deviceAbilitySetMapper.updateById(deviceAbilitySetPo);
        } else {
            deviceAbilitySetPo.setCreateTime(System.currentTimeMillis());
            effectCount = deviceAbilitySetMapper.insert(deviceAbilitySetPo);
        }
        return effectCount > 0;
    }

    /**
     * 删除 该设备功能集
     *
     * @param requestParam
     * @return
     */
    public Boolean deleteAbilitySet(Map<String, Object> requestParam) {

        Boolean ret  =false;
        Integer abilitySetId = (Integer) requestParam.get("abilitySetId");//功能集主键


        //判断当 功能集id不为空时
        if( abilitySetId!=null){
            //先删除 该设备能力集
            ret = deviceAbilitySetMapper.deleteById(abilitySetId) > 0;
            //再删除 关系表中 与该能力集有关的数据
            ret = ret && deviceAbilitySetRelationMapper.deleteByAbilitySetId(abilitySetId) > 0;

        }else{
            log.error("功能集主键不可为空");
            return false;
        }

        return ret;
    }
    /**
     * 查询 能力集列表
     * @param request
     * @return
     */
    public List<DeviceAbilitySetVo> selectList(DeviceAbilitySetQueryRequest request) {

        DeviceAbilitySetPo queryDeviceAbilitySetPo = new DeviceAbilitySetPo();

        queryDeviceAbilitySetPo.setId(request.getId());
        queryDeviceAbilitySetPo.setName(request.getName());
        queryDeviceAbilitySetPo.setRemark(request.getRemark());
        queryDeviceAbilitySetPo.setStatus(request.getStatus());

        Integer offset = (request.getPage() - 1) * request.getLimit();
        Integer limit = request.getLimit();

        List<DeviceAbilitySetPo> deviceAbilitySetPos = deviceAbilitySetMapper.selectList(queryDeviceAbilitySetPo, limit, offset);
        return deviceAbilitySetPos.stream().map(deviceAbilitySetPo -> {
            DeviceAbilitySetVo deviceAbilitySetVo = new DeviceAbilitySetVo();

            deviceAbilitySetVo.setName(deviceAbilitySetPo.getName());
            deviceAbilitySetVo.setRemark(deviceAbilitySetPo.getRemark());
            deviceAbilitySetVo.setStatus(deviceAbilitySetPo.getStatus());
            deviceAbilitySetVo.setId(deviceAbilitySetPo.getId());
            return deviceAbilitySetVo;
        }).collect(Collectors.toList());
    }

    /**
     * 查询 能力集对象
     * @param request
     * @return
     */
    public DeviceAbilitySetVo selectById(DeviceAbilitySetQueryRequest request) {

        DeviceAbilitySetPo queryDeviceAbilitySetPo = new DeviceAbilitySetPo();

        queryDeviceAbilitySetPo.setId(request.getId());

        DeviceAbilitySetPo deviceAbilitySetPo = deviceAbilitySetMapper.selectById(queryDeviceAbilitySetPo.getId());

        DeviceAbilitySetVo deviceAbilitySetVo = new DeviceAbilitySetVo();

        if(deviceAbilitySetPo!=null){
            deviceAbilitySetVo.setName(deviceAbilitySetPo.getName());
            deviceAbilitySetVo.setRemark(deviceAbilitySetPo.getRemark());
            deviceAbilitySetVo.setStatus(deviceAbilitySetPo.getStatus());
            deviceAbilitySetVo.setId(deviceAbilitySetPo.getId());
        }
        return deviceAbilitySetVo;
    }
//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
