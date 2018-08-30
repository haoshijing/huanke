package com.huanke.iot.manage.service.device.team;

import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceTeamMapper;
import com.huanke.iot.base.dao.device.DeviceTeamSceneMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import com.huanke.iot.base.po.device.team.DeviceTeamScenePo;
import com.huanke.iot.manage.vo.request.device.team.TeamCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.team.TeamDeviceCreateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
@Slf4j
public class DeviceTeamService {
    @Autowired
    private DeviceTeamMapper deviceTeamMapper;

    @Autowired
    private DeviceTeamSceneMapper deviceTeamSceneMapper;

    @Autowired
    private DeviceMapper deviceMapper;


    /**
     * 创建新的自定义组
     * 2018-08-29
     * sixiaojun
     * @param teamCreateOrUpdateRequest
     * @return
     */
    public Integer createTeam(TeamCreateOrUpdateRequest teamCreateOrUpdateRequest){
        DeviceTeamPo deviceTeamPo=new DeviceTeamPo();
        List<DeviceTeamItemPo> deviceTeamItemPoList=new ArrayList<>();
        List<DeviceTeamScenePo> deviceTeamScenePoList=new ArrayList<>();
        deviceTeamPo.setName(teamCreateOrUpdateRequest.getName());
        deviceTeamPo.setIcon(teamCreateOrUpdateRequest.getTeamIcon());
        deviceTeamPo.setVideoCover(teamCreateOrUpdateRequest.getTeamCover());
        deviceTeamPo.setMasterUserId(teamCreateOrUpdateRequest.getCreateUserOpenId());
        deviceTeamPo.setCreateTime(System.currentTimeMillis());
        deviceTeamPo.setLastUpdateTime(System.currentTimeMillis());
        //向team表中插入相关数据
        this.deviceTeamMapper.insert(deviceTeamPo);
        for(TeamCreateOrUpdateRequest.imgOrVideo imgVideo:teamCreateOrUpdateRequest.getImgOrVideoList()){
            DeviceTeamScenePo deviceTeamScenePo=new DeviceTeamScenePo();
            deviceTeamScenePo.setTeamId(deviceTeamPo.getId());
            deviceTeamScenePo.setImgVideo(imgVideo.getImgVideo());
            deviceTeamScenePo.setCreateTime(System.currentTimeMillis());
            deviceTeamScenePo.setLastUpdateTime(System.currentTimeMillis());
            deviceTeamScenePoList.add(deviceTeamScenePo);
        }
        for(TeamDeviceCreateRequest device:teamCreateOrUpdateRequest.getTeamDeviceCreateRequestList()){
            DeviceTeamItemPo deviceTeamItemPo=new DeviceTeamItemPo();
//            deviceTeamItemPo.setDeviceId(device.getMac());
//            deviceTeamItemPo.setUserId(teamCreateOrUpdateRequest.getCreateUserOpenId());
        }
        //进行设备的批量绑定

        //进行场景的批量更新
        Boolean ret=this.deviceTeamSceneMapper.insertBatch(deviceTeamScenePoList)>0;
        if(ret){
            return deviceTeamPo.getId();
        }
        else {
            return -1;
        }
    }

    public DeviceTeamPo queryTeamByName(DeviceTeamPo deviceTeamPo){
        DeviceTeamPo queryPo=this.deviceTeamMapper.queryByName(deviceTeamPo);
        if(null != queryPo){
            return queryPo;
        }
        else {
            return null;
        }
    }

    public DevicePo queryDeviceByMac(List<TeamDeviceCreateRequest> teamDeviceList){
        DevicePo devicePo=null;
        for(TeamDeviceCreateRequest teamDevice:teamDeviceList){
            devicePo=deviceMapper.selectByMac(teamDevice.getMac());
            if(null == devicePo){
                return devicePo;
            }
        }
        return devicePo;
    }
}
