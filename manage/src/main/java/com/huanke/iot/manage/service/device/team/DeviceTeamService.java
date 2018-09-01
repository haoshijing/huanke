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
import com.huanke.iot.manage.vo.request.device.team.TeamListQueryRequest;
import com.huanke.iot.manage.vo.request.device.team.TeamTrusteeRequest;
import com.huanke.iot.manage.vo.response.device.team.DeviceTeamVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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
        CustomerUserPo customerUserPo=this.customerUserMapper.selectByOpenId(teamCreateOrUpdateRequest.getCreateUserOpenId());
        deviceTeamPo.setName(teamCreateOrUpdateRequest.getName());
        deviceTeamPo.setIcon(teamCreateOrUpdateRequest.getTeamIcon());
        //封面
        deviceTeamPo.setVideoCover(teamCreateOrUpdateRequest.getTeamCover());
        //创建之后为托管之前，创建人和materUser为同一人
        deviceTeamPo.setCreateUserId(customerUserPo.getId());
        deviceTeamPo.setMasterUserId(customerUserPo.getId());
        //设置该组的使用状态为正常
        deviceTeamPo.setStatus(1);
        //设置组的状态为终端组
        deviceTeamPo.setTeamStatus(1);
        deviceTeamPo.setSceneDescription(teamCreateOrUpdateRequest.getSceneDescription());
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
    public DeviceTeamPo addDeviceToTeam(TeamCreateOrUpdateRequest teamCreateOrUpdateRequest,DeviceTeamPo deviceTeamPo){
        Boolean deviceLinkAge=false;
        List<DeviceTeamItemPo> deviceTeamItemPoList=new ArrayList<>();
        List<DevicePo> devicePoList=new ArrayList<>();
        CustomerUserPo customerUserPo=this.customerUserMapper.selectByOpenId(teamCreateOrUpdateRequest.getCreateUserOpenId());
        for(TeamDeviceCreateRequest device:teamCreateOrUpdateRequest.getTeamDeviceCreateRequestList()){
            DeviceTeamItemPo deviceTeamItemPo=new DeviceTeamItemPo();
            DevicePo devicePo=this.deviceMapper.selectByMac(device.getMac());
            devicePo.setName(device.getName());
            //该设备被添加进入组的同时也被绑定给了当前的终端用户，因此设定此处的绑定状态为已绑定
            devicePo.setBindStatus(2);
            //设定绑定时间
            devicePo.setBindTime(System.currentTimeMillis());
            devicePo.setLastUpdateTime(System.currentTimeMillis());
            deviceTeamItemPo.setDeviceId(devicePo.getId());
            deviceTeamItemPo.setTeamId(deviceTeamPo.getId());
            deviceTeamItemPo.setUserId(customerUserPo.getId());
            deviceTeamItemPo.setStatus(1);
            deviceTeamItemPo.setLinkAgeStatus(device.getLinkAgeStatus());
            if(1 == device.getLinkAgeStatus()){
                deviceLinkAge=true;
            }
            deviceTeamItemPo.setCreateTime(System.currentTimeMillis());
            deviceTeamItemPo.setLastUpdateTime(System.currentTimeMillis());
            devicePoList.add(devicePo);
            deviceTeamItemPoList.add(deviceTeamItemPo);
        }
        //如果当前组中存在联动设备，则设定组为联动组
        if(deviceLinkAge){
            deviceTeamPo.setTeamType(1);
        }
        //否则为普通终端组
        else {
            deviceTeamPo.setTeamType(2);
        }
        deviceTeamPo.setLastUpdateTime(System.currentTimeMillis());
        //更新当前组的组类型
        deviceTeamMapper.updateById(deviceTeamPo);
        //进行设备名称的批量更新
        this.deviceMapper.updateBatch(devicePoList);
        //进行设备的批量绑定
        Boolean ret=this.deviceTeamItemMapper.insertBatch(deviceTeamItemPoList)>0;
        if(ret){
            return deviceTeamPo;
        }
        else {
            return null;
        }
    }

    public List<DeviceTeamVo> queryTeamList(TeamListQueryRequest teamListQueryRequest){
        //当期要查询的页
        Integer currentPage = teamListQueryRequest.getPage();
        //每页显示的数量
        Integer limit= teamListQueryRequest.getLimit();
        //偏移量
        Integer offset = (currentPage - 1) * limit;
        //查询整个列表，因此先建一个空的deviceTeamPo模型
        DeviceTeamPo queryPo=new DeviceTeamPo();
        List<DeviceTeamPo> deviceTeamPoList=this.deviceTeamMapper.selectList(queryPo,limit,offset);
        List<DeviceTeamVo> deviceTeamVoList=deviceTeamPoList.stream().map(deviceTeamPo -> {
            CustomerUserPo customerUserPo;
            DeviceTeamVo deviceTeamVo=new DeviceTeamVo();
            deviceTeamVo.setId(deviceTeamPo.getId());
            deviceTeamVo.setName(deviceTeamPo.getName());
            deviceTeamVo.setIcon(deviceTeamPo.getIcon());
            customerUserPo=this.customerUserMapper.selectByUserId(deviceTeamPo.getCreateUserId());
            deviceTeamVo.setCreateUserNickName(customerUserPo.getNickname());
            deviceTeamVo.setCreateUserOpenId(customerUserPo.getOpenId());
            deviceTeamVo.setCreateTime(deviceTeamPo.getCreateTime());
            customerUserPo=this.customerUserMapper.selectByUserId(deviceTeamPo.getMasterUserId());
            deviceTeamVo.setOwnerOpenId(customerUserPo.getOpenId());
            deviceTeamVo.setOwnerNickName(customerUserPo.getNickname());
            deviceTeamVo.setCover(deviceTeamPo.getVideoCover());
            deviceTeamVo.setSceneDescription(deviceTeamPo.getSceneDescription());
            //组的使用状态，1-正常，2-不正常
            deviceTeamVo.setStatus(deviceTeamPo.getStatus());
            //获取组的状态，终端组或是托管组
            deviceTeamVo.setTeamStatus(deviceTeamPo.getTeamStatus());
            //获取组的类型，用户组或是联动组
            deviceTeamVo.setTeamType(deviceTeamPo.getTeamType());
            deviceTeamVo.setRemark(deviceTeamPo.getRemark());
            DeviceTeamItemPo deviceTeamItemPo=new DeviceTeamItemPo();
            deviceTeamItemPo.setTeamId(deviceTeamPo.getId());
            Integer deviceCount=this.deviceTeamItemMapper.selectCount(deviceTeamItemPo);
            deviceTeamVo.setDeviceCount(deviceCount);
            List<DeviceTeamScenePo> deviceTeamScenePoList=this.deviceTeamSceneMapper.selectImgVideoList(deviceTeamPo.getId());
            List<DeviceTeamVo.ImgVideos> imgVideosList=deviceTeamScenePoList.stream().map(eachPo -> {
                DeviceTeamVo.ImgVideos imgVideos=new DeviceTeamVo.ImgVideos();
                imgVideos.setImgvideo(eachPo.getImgVideo());
                return imgVideos;
            }).collect(Collectors.toList());
            deviceTeamVo.setImgVideosList(imgVideosList);
            return deviceTeamVo;
        }).collect(Collectors.toList());
        return  deviceTeamVoList;
    }

    public CustomerUserPo trusteeTeam(TeamTrusteeRequest teamTrusteeRequest){
        DeviceTeamPo deviceTeamPo=this.deviceTeamMapper.selectById(teamTrusteeRequest.getId());
        CustomerUserPo customerUserPo=this.customerUserMapper.selectByOpenId(teamTrusteeRequest.getOpenId());
        deviceTeamPo.setMasterUserId(customerUserPo.getId());
        if(teamTrusteeRequest.getDeleteCreator()){
            deviceTeamPo.setCreateUserId(null);
        }
        Boolean ret=this.deviceTeamMapper.updateById(deviceTeamPo)>0;
        if(ret){
            return customerUserPo;
        }
        else {
            return null;
        }
    }


    public Integer selectTeamCount(){
        DeviceTeamPo deviceTeamPo=new DeviceTeamPo();
        return this.deviceTeamMapper.selectCount(deviceTeamPo);
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

    public Boolean queryCustomerUser(String openId){
        CustomerUserPo customerUserPo=this.customerUserMapper.selectByOpenId(openId);
        if(null != customerUserPo){
            return true;
        }
        else {
            return false;
        }
    }

    public DevicePo queryDeviceByMac(List<TeamDeviceCreateRequest> teamDeviceList){
        DevicePo devicePo=null;
        for(TeamDeviceCreateRequest teamDevice:teamDeviceList){
            devicePo=this.deviceMapper.selectByMac(teamDevice.getMac());
            if(null == devicePo){
                return devicePo;
            }
        }
        return devicePo;
    }
}
