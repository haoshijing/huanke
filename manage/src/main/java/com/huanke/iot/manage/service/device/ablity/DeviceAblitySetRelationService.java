package com.huanke.iot.manage.service.device.ablity;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblitySetMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblitySetRelationMapper;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.base.po.device.alibity.DeviceAblitySetPo;
import com.huanke.iot.base.po.device.alibity.DeviceAblitySetRelationPo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 设备功能集 与 功能的关系
 */
@Repository
@Slf4j
public class DeviceAblitySetRelationService {

    @Autowired
    private DeviceAblitySetRelationMapper deviceAblitySetRelationMapper;

    @Autowired
    private DeviceAblityMapper deviceAblityMapper;

    @Autowired
    private DeviceAblitySetMapper deviceAblitySetMapper;

    /**
     * 创建 设备功能集与 功能的关系
     *
     * @param requestParam
     * @return
     */
    public Boolean createOrUpdate(Map<String, Object> requestParam) {

        int effectCount = 0;
        DeviceAblitySetRelationPo deviceAblitySetRelationPo = new DeviceAblitySetRelationPo();

        deviceAblitySetRelationPo.setAblityId((Integer) requestParam.get("ablityId"));
        deviceAblitySetRelationPo.setAblitySetId((Integer) requestParam.get("ablitySetId"));
        if(deviceAblitySetRelationPo.getAblityId()!=null&&deviceAblitySetRelationPo.getAblityId()>0&&
                deviceAblitySetRelationPo.getAblitySetId()!=null&&deviceAblitySetRelationPo.getAblitySetId()!=null){

            /*验证能力是否存在*/
            DeviceAblityPo resultDeviceAblitypo = deviceAblityMapper.selectById(deviceAblitySetRelationPo.getAblityId());
            if(resultDeviceAblitypo==null){
                log.error("能力主键不存在");
                return false;
            }
            /*验证功能集是否存在*/
            DeviceAblitySetPo resultDeviceAblitySetpo = deviceAblitySetMapper.selectById(deviceAblitySetRelationPo.getAblitySetId());
            if(resultDeviceAblitypo==null){
                log.error("功能集主键不存在");
                return false;
            }

            effectCount = deviceAblitySetRelationMapper.insert(deviceAblitySetRelationPo);

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
        DeviceAblitySetRelationPo deviceAblitySetRelationPo = new DeviceAblitySetRelationPo();

        deviceAblitySetRelationPo.setId((Integer) (requestParam.get("id")));
        deviceAblitySetRelationPo.setAblityId((Integer) requestParam.get("ablityId"));
        deviceAblitySetRelationPo.setAblitySetId((Integer) requestParam.get("ablitySetId"));

        //更新
        if (deviceAblitySetRelationPo.getId() != null && deviceAblitySetRelationPo.getId() > 0) {
            effectCount = deviceAblitySetRelationMapper.updateById(deviceAblitySetRelationPo);
        }
        return effectCount > 0;
    }

    /**
     * 删除 该设备功能集
     *
     * @param requestParam
     * @return
     */
    public Boolean deleteByAblitySetId(Map<String, Object> requestParam) {

        int effectCount = 0;
        DeviceAblitySetRelationPo deviceAblitySetRelationPo = new DeviceAblitySetRelationPo();

        deviceAblitySetRelationPo.setAblitySetId((Integer) requestParam.get("ablitySetId"));
        //判断当 能力集id不为空时
        if( deviceAblitySetRelationPo.getAblitySetId()!=null&&deviceAblitySetRelationPo.getAblitySetId()!=null){

            effectCount = deviceAblitySetRelationMapper.deleteByAblitySetId(deviceAblitySetRelationPo.getAblitySetId());

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
    public Boolean deleteByAblityId(Map<String, Object> requestParam) {

        int effectCount = 0;
        DeviceAblitySetRelationPo deviceAblitySetRelationPo = new DeviceAblitySetRelationPo();

        deviceAblitySetRelationPo.setAblityId((Integer) requestParam.get("ablityId"));
        deviceAblitySetRelationPo.setAblitySetId((Integer) requestParam.get("ablitySetId"));
        //判断当 能力id 于 能力集id都不为空
        if(deviceAblitySetRelationPo.getAblityId()!=null&&deviceAblitySetRelationPo.getAblityId()>0&&
                deviceAblitySetRelationPo.getAblitySetId()!=null&&deviceAblitySetRelationPo.getAblitySetId()!=null){

            effectCount = deviceAblitySetRelationMapper.deleteByAblityId(deviceAblitySetRelationPo);

        }else{
            log.error("能力主键或功能集主键不可为空");
            return false;
        }

        return effectCount > 0;
    }

//    /**
//     * 查询 能力集列表
//     * @param ablitySetId
//     * @return
//     */
//    public List<DeviceAblitySetRelationVo> selectByAblitySetId(Integer ablitySetId) {
//
//        DeviceAblitySetRelationPo queryDeviceAblitySetRelationPo = new DeviceAblitySetRelationPo();
//
//
//        List<DeviceAblitySetRelationPo> deviceAblitySetRelationPos = deviceAblitySetRelationMapper.selectByAblitySetId(ablitySetId);
//        return deviceAblitySetRelationPos.stream().map(deviceAblitySetRelationPo -> {
//            DeviceAblitySetRelationVo deviceAblitySetRelationVo = new DeviceAblitySetRelationVo();
//
//            deviceAblitySetRelationVo.setName(deviceAblitySetRelationPo.getName());
//            deviceAblitySetRelationVo.setRemark(deviceAblitySetRelationPo.getRemark());
//            deviceAblitySetRelationVo.setStatus(deviceAblitySetRelationPo.getStatus());
//            deviceAblitySetRelationVo.setId(deviceAblitySetRelationPo.getId());
//            return deviceAblitySetRelationVo;
//        }).collect(Collectors.toList());
//    }

//    /**
//     * 查询 能力集对象
//     * @param request
//     * @return
//     */
//    public DeviceAblitySetRelationVo selectById(DeviceAblitySetRelationQueryRequest request) {
//
//        DeviceAblitySetRelationPo queryDeviceAblitySetRelationPo = new DeviceAblitySetRelationPo();
//
//        queryDeviceAblitySetRelationPo.setId(request.getId());
//
//        DeviceAblitySetRelationPo deviceAblitySetRelationPo = deviceAblitySetRelationMapper.selectById(queryDeviceAblitySetRelationPo.getId());
//
//        DeviceAblitySetRelationVo deviceAblitySetRelationVo = new DeviceAblitySetRelationVo();
//
//        if(deviceAblitySetRelationPo!=null){
//            deviceAblitySetRelationVo.setName(deviceAblitySetRelationPo.getName());
//            deviceAblitySetRelationVo.setRemark(deviceAblitySetRelationPo.getRemark());
//            deviceAblitySetRelationVo.setStatus(deviceAblitySetRelationPo.getStatus());
//            deviceAblitySetVo.setId(deviceAblitySetPo.getId());
//        }
//        return deviceAblitySetVo;
//    }
//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
