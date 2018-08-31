package com.huanke.iot.manage.service.device.team;

import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.DeviceTeamItemMapper;
import com.huanke.iot.base.dao.device.DeviceTeamMapper;
import com.huanke.iot.base.dao.device.DeviceTeamSceneMapper;
import com.huanke.iot.base.po.customer.CustomerUserPo;
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
    private DeviceTeamItemMapper deviceTeamItemMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private CustomerUserMapper customerUserMapper;


    /**
     * 创建新的自定义组
     * 2018-08-29
     * sixiaojun
     * @param teamCreateOrUpdateRequest
     * @return
     */
    public DeviceTeamPo createTeam(TeamCreateOrUpdateRequest teamCreateOrUpdateRequest){
        DeviceTeamPo deviceTeamPo=new DeviceTeamPo();
        List<DeviceTeamScenePo> deviceTeamScenePoList=new ArrayList<>();
        //根据微信用户openId查询对应的userId
        CustomerUserPo customerUserPo=customerUserMapper.selectByOpenId(teamCreateOrUpdateRequest.getCreateUserOpenId());
        deviceTeamPo.setName(teamCreateOrUpdateRequest.getName());
        deviceTeamPo.setIcon(teamCreateOrUpdateRequest.getTeamIcon());
        deviceTeamPo.setVideoCover(teamCreateOrUpdateRequest.getTeamCover());
        deviceTeamPo.setMasterUserId(customerUserPo.getId());
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
        //进行场景图册和视频册的批量更新
        Boolean ret=this.deviceTeamSceneMapper.insertBatch(deviceTeamScenePoList)>0;
        if(ret){
            return deviceTeamPo;
        }
        else {
            return null;
        }
    }

    /**
     * 将选中设备添加至组
     * sixiaojun
     * 2018-08-30
     * @param teamCreateOrUpdateRequest
     * @param deviceTeamPo
     * @return
     */
    public Boolean addDeviceToTeam(TeamCreateOrUpdateRequest teamCreateOrUpdateRequest,DeviceTeamPo deviceTeamPo){
        List<DeviceTeamItemPo> deviceTeamItemPoList=new ArrayList<>();
        List<DevicePo> devicePoList=new ArrayList<>();
        CustomerUserPo customerUserPo=customerUserMapper.selectByOpenId(teamCreateOrUpdateRequest.getCreateUserOpenId());
        for(TeamDeviceCreateRequest device:teamCreateOrUpdateRequest.getTeamDeviceCreateRequestList()){
            DeviceTeamItemPo deviceTeamItemPo=new DeviceTeamItemPo();
            DevicePo devicePo=this.deviceMapper.selectByMac(device.getMac());
            devicePo.setName(device.getName());
            devicePo.setLastUpdateTime(System.currentTimeMillis());
            deviceTeamItemPo.setDeviceId(devicePo.getId());
            deviceTeamItemPo.setTeamId(deviceTeamPo.getId());
            deviceTeamItemPo.setUserId(customerUserPo.getId());
            deviceTeamItemPo.setCreateTime(System.currentTimeMillis());
            //设置组的状态为正常
            deviceTeamItemPo.setStatus(1);
            deviceTeamItemPo.setCreateTime(System.currentTimeMillis());
            deviceTeamItemPo.setLastUpdateTime(System.currentTimeMillis());

            devicePoList.add(devicePo);
            deviceTeamItemPoList.add(deviceTeamItemPo);
        }
        //进行设备名称的批量更新
        this.deviceMapper.updateBatch(devicePoList);
        //进行设备的批量绑定
        Boolean ret=this.deviceTeamItemMapper.insertBatch(deviceTeamItemPoList)>0;
        if(ret){
            return true;
        }
        else {
            return false;
        }
    }

    public DevicePo isDeviceHasTeam(List<TeamDeviceCreateRequest> teamDeviceCreateRequestList){
        for(TeamDeviceCreateRequest teamDeviceCreateRequest:teamDeviceCreateRequestList){
            DevicePo devicePo=this.deviceMapper.selectByMac(teamDeviceCreateRequest.getMac());
            //若查询到该设备已有组，则返回该设备
            if(null != this.deviceTeamItemMapper.selectByDeviceId(devicePo.getId())){
                return devicePo;
            }
        }
        return null;
    }

    public DeviceTeamPo queryTeamByName(String name){
        DeviceTeamPo deviceTeamPo=new DeviceTeamPo();
        deviceTeamPo.setName(name);
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
