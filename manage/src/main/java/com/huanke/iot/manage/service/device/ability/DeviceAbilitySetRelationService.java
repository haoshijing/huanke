package com.huanke.iot.manage.service.device.ability;

import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilitySetMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilitySetRelationMapper;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.ability.DeviceAbilitySetPo;
import com.huanke.iot.base.po.device.ability.DeviceAbilitySetRelationPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 设备功能集 与 功能的关系
 */
@Repository
@Slf4j
public class DeviceAbilitySetRelationService {

    @Autowired
    private DeviceAbilitySetRelationMapper deviceAbilitySetRelationMapper;

    @Autowired
    private DeviceAbilityMapper deviceAbilityMapper;

    @Autowired
    private DeviceAbilitySetMapper deviceAbilitySetMapper;

    /**
     * 创建 设备功能集与 功能的关系
     *
     * @param requestParam
     * @return
     */
    public Boolean createOrUpdate(Map<String, Object> requestParam) {

        int effectCount = 0;
        DeviceAbilitySetRelationPo deviceAbilitySetRelationPo = new DeviceAbilitySetRelationPo();

        deviceAbilitySetRelationPo.setAbilityId((Integer) requestParam.get("abilityId"));
        deviceAbilitySetRelationPo.setAbilitySetId((Integer) requestParam.get("abilitySetId"));
        if(deviceAbilitySetRelationPo.getAbilityId()!=null&&deviceAbilitySetRelationPo.getAbilityId()>0&&
                deviceAbilitySetRelationPo.getAbilitySetId()!=null&&deviceAbilitySetRelationPo.getAbilitySetId()!=null){

            /*验证能力是否存在*/
            DeviceAbilityPo resultDeviceAbilitypo = deviceAbilityMapper.selectById(deviceAbilitySetRelationPo.getAbilityId());
            if(resultDeviceAbilitypo==null){
                log.error("能力主键不存在");
                return false;
            }
            /*验证功能集是否存在*/
            DeviceAbilitySetPo resultDeviceAbilitySetpo = deviceAbilitySetMapper.selectById(deviceAbilitySetRelationPo.getAbilitySetId());
            if(resultDeviceAbilitypo==null){
                log.error("功能集主键不存在");
                return false;
            }

            effectCount = deviceAbilitySetRelationMapper.insert(deviceAbilitySetRelationPo);

        }else{
            log.error("能力主键或功能集主键不可为空");
            return false;
        }

        return effectCount > 0;
    }

    /**
     * 更新 设备功能集与 功能的关系
     *
     * @param requestParam
     * @return
     */
    public Boolean update(Map<String, Object> requestParam) {

        int effectCount = 0;
        DeviceAbilitySetRelationPo deviceAbilitySetRelationPo = new DeviceAbilitySetRelationPo();

        deviceAbilitySetRelationPo.setId((Integer) (requestParam.get("id")));
        deviceAbilitySetRelationPo.setAbilityId((Integer) requestParam.get("abilityId"));
        deviceAbilitySetRelationPo.setAbilitySetId((Integer) requestParam.get("abilitySetId"));

        //更新
        if (deviceAbilitySetRelationPo.getId() != null && deviceAbilitySetRelationPo.getId() > 0) {
            effectCount = deviceAbilitySetRelationMapper.updateById(deviceAbilitySetRelationPo);
        }
        return effectCount > 0;
    }

    /**
     * 删除 该设备功能集
     *
     * @param requestParam
     * @return
     */
    public Boolean deleteByAbilitySetId(Map<String, Object> requestParam) {

        int effectCount = 0;
        DeviceAbilitySetRelationPo deviceAbilitySetRelationPo = new DeviceAbilitySetRelationPo();

        deviceAbilitySetRelationPo.setAbilitySetId((Integer) requestParam.get("abilitySetId"));
        //判断当 能力集id不为空时
        if( deviceAbilitySetRelationPo.getAbilitySetId()!=null&&deviceAbilitySetRelationPo.getAbilitySetId()!=null){

            effectCount = deviceAbilitySetRelationMapper.deleteByAbilitySetId(deviceAbilitySetRelationPo.getAbilitySetId());

        }else{
            log.error("功能集主键不可为空");
            return false;
        }

        return effectCount > 0;
    }

    /**
     * 删除 设备功能集与 功能的关系
     *
     * @param requestParam
     * @return
     */
    public Boolean deleteByAbilityId(Map<String, Object> requestParam) {

        int effectCount = 0;
        DeviceAbilitySetRelationPo deviceAbilitySetRelationPo = new DeviceAbilitySetRelationPo();

        deviceAbilitySetRelationPo.setAbilityId((Integer) requestParam.get("abilityId"));
        deviceAbilitySetRelationPo.setAbilitySetId((Integer) requestParam.get("abilitySetId"));
        //判断当 能力id 于 能力集id都不为空
        if(deviceAbilitySetRelationPo.getAbilityId()!=null&&deviceAbilitySetRelationPo.getAbilityId()>0&&
                deviceAbilitySetRelationPo.getAbilitySetId()!=null&&deviceAbilitySetRelationPo.getAbilitySetId()!=null){

            effectCount = deviceAbilitySetRelationMapper.deleteByAbilityId(deviceAbilitySetRelationPo);

        }else{
            log.error("能力主键或功能集主键不可为空");
            return false;
        }

        return effectCount > 0;
    }

//    /**
//     * 查询 能力集列表
//     * @param abilitySetId
//     * @return
//     */
//    public List<DeviceAbilitySetRelationVo> selectByAbilitySetId(Integer abilitySetId) {
//
//        DeviceAbilitySetRelationPo queryDeviceAbilitySetRelationPo = new DeviceAbilitySetRelationPo();
//
//
//        List<DeviceAbilitySetRelationPo> deviceAbilitySetRelationPos = deviceAbilitySetRelationMapper.selectByAbilitySetId(abilitySetId);
//        return deviceAbilitySetRelationPos.stream().map(deviceAbilitySetRelationPo -> {
//            DeviceAbilitySetRelationVo deviceAbilitySetRelationVo = new DeviceAbilitySetRelationVo();
//
//            deviceAbilitySetRelationVo.setName(deviceAbilitySetRelationPo.getName());
//            deviceAbilitySetRelationVo.setRemark(deviceAbilitySetRelationPo.getRemark());
//            deviceAbilitySetRelationVo.setStatus(deviceAbilitySetRelationPo.getStatus());
//            deviceAbilitySetRelationVo.setId(deviceAbilitySetRelationPo.getId());
//            return deviceAbilitySetRelationVo;
//        }).collect(Collectors.toList());
//    }

//    /**
//     * 查询 能力集对象
//     * @param request
//     * @return
//     */
//    public DeviceAbilitySetRelationVo selectById(DeviceAbilitySetRelationQueryRequest request) {
//
//        DeviceAbilitySetRelationPo queryDeviceAbilitySetRelationPo = new DeviceAbilitySetRelationPo();
//
//        queryDeviceAbilitySetRelationPo.setId(request.getId());
//
//        DeviceAbilitySetRelationPo deviceAbilitySetRelationPo = deviceAbilitySetRelationMapper.selectById(queryDeviceAbilitySetRelationPo.getId());
//
//        DeviceAbilitySetRelationVo deviceAbilitySetRelationVo = new DeviceAbilitySetRelationVo();
//
//        if(deviceAbilitySetRelationPo!=null){
//            deviceAbilitySetRelationVo.setName(deviceAbilitySetRelationPo.getName());
//            deviceAbilitySetRelationVo.setRemark(deviceAbilitySetRelationPo.getRemark());
//            deviceAbilitySetRelationVo.setStatus(deviceAbilitySetRelationPo.getStatus());
//            deviceAbilitySetVo.setId(deviceAbilitySetPo.getId());
//        }
//        return deviceAbilitySetVo;
//    }
//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
