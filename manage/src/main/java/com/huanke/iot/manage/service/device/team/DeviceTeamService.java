package com.huanke.iot.manage.service.device.team;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.constant.DeviceTeamConstants;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.*;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DeviceCustomerRelationPo;
import com.huanke.iot.base.po.device.DeviceCustomerUserRelationPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import com.huanke.iot.base.po.device.team.DeviceTeamScenePo;
import com.huanke.iot.manage.common.util.QRCodeUtil;
import com.huanke.iot.manage.vo.request.device.operate.QueryInfoByCustomerRequest;
import com.huanke.iot.manage.vo.request.device.team.*;
import com.huanke.iot.manage.vo.response.device.team.DeviceTeamVo;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

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

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private DeviceCustomerRelationMapper deviceCustomerRelationMapper;

    @Autowired
    private DeviceCustomerUserRelationMapper deviceCustomerUserRelationMapper;



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
        //根据当前用户找到用户归属的customer，将改组的customerId设为当前customer
        deviceTeamPo.setCustomerId(customerUserPo.getCustomerId());
        deviceTeamPo.setName(teamCreateOrUpdateRequest.getName());
        deviceTeamPo.setIcon(teamCreateOrUpdateRequest.getTeamIcon());
        //封面
        deviceTeamPo.setVideoCover(teamCreateOrUpdateRequest.getTeamCover());
        //创建之后为托管之前，创建人和materUser为同一人
        deviceTeamPo.setCreateUserId(customerUserPo.getId());
        deviceTeamPo.setMasterUserId(customerUserPo.getId());
        //设置该组的使用状态为正常
        deviceTeamPo.setStatus(CommonConstant.STATUS_YES);
        //设置组的状态为终端组
        deviceTeamPo.setTeamStatus(DeviceTeamConstants.DEVICE_TEAM_STATUS_TERMINAL);
        deviceTeamPo.setTeamType(DeviceTeamConstants.DEVICE_TEAM_TYPE_USER);
        deviceTeamPo.setSceneDescription(teamCreateOrUpdateRequest.getSceneDescription());
        deviceTeamPo.setCreateTime(System.currentTimeMillis());
        deviceTeamPo.setLastUpdateTime(System.currentTimeMillis());
        //向team表中插入相关数据

        if(teamCreateOrUpdateRequest.getImgOrVideoList()!=null&&teamCreateOrUpdateRequest.getImgOrVideoList().size()>0){
            teamCreateOrUpdateRequest.getImgOrVideoList().stream().forEach(imgVideo -> {
                DeviceTeamScenePo deviceTeamScenePo = new DeviceTeamScenePo();
                deviceTeamScenePo.setTeamId(deviceTeamPo.getId());
                deviceTeamScenePo.setImgVideo(imgVideo.getImgVideo());
                deviceTeamScenePo.setStatus(CommonConstant.STATUS_YES);
                deviceTeamScenePo.setCreateTime(System.currentTimeMillis());
                deviceTeamScenePo.setLastUpdateTime(System.currentTimeMillis());
                deviceTeamScenePoList.add(deviceTeamScenePo);
            });
            //进行场景图册和视频册的批量更新
            this.deviceTeamSceneMapper.insertBatch(deviceTeamScenePoList);
        }
        Boolean ret=this.deviceTeamMapper.insert(deviceTeamPo)>0;
        if(ret){
            return deviceTeamPo;
        }
        else {
            return null;
        }
    }

    /**
     * 更新设备组 （只能更新组 基础信息 与设备，不能更改所有人）
     * @param teamCreateOrUpdateRequest
     * @return
     */
    public ApiResponse<Boolean> updateTeam(TeamCreateOrUpdateRequest teamCreateOrUpdateRequest){
        List<DeviceTeamScenePo> deviceTeamScenePoList = new ArrayList<>();

        try {
            DeviceTeamPo deviceTeamPo = deviceTeamMapper.selectById(teamCreateOrUpdateRequest.getId());
            if(deviceTeamPo!=null){

                deviceTeamPo.setName(teamCreateOrUpdateRequest.getName());
                deviceTeamPo.setIcon(teamCreateOrUpdateRequest.getTeamIcon());
                deviceTeamPo.setVideoCover(teamCreateOrUpdateRequest.getTeamCover());
                deviceTeamPo.setSceneDescription(teamCreateOrUpdateRequest.getSceneDescription());
                deviceTeamPo.setRemark(teamCreateOrUpdateRequest.getRemark());
            }
            deviceTeamPo.setLastUpdateTime(System.currentTimeMillis());
            //向team表中插入相关数据
            Boolean ret = this.deviceTeamMapper.updateById(deviceTeamPo)>0;

            //删除组的场景表 并重新插入
            deviceTeamSceneMapper.deleteByTeamId(deviceTeamPo.getId());

            if(teamCreateOrUpdateRequest.getImgOrVideoList()!=null&&teamCreateOrUpdateRequest.getImgOrVideoList().size()>0){
                teamCreateOrUpdateRequest.getImgOrVideoList().stream().forEach(imgVideo -> {
                    DeviceTeamScenePo deviceTeamScenePo = new DeviceTeamScenePo();
                    deviceTeamScenePo.setTeamId(deviceTeamPo.getId());
                    deviceTeamScenePo.setImgVideo(imgVideo.getImgVideo());
                    deviceTeamScenePo.setCreateTime(System.currentTimeMillis());
                    deviceTeamScenePo.setLastUpdateTime(System.currentTimeMillis());
                    deviceTeamScenePoList.add(deviceTeamScenePo);
                });
                //进行场景图册和视频册的批量更新
                this.deviceTeamSceneMapper.insertBatch(deviceTeamScenePoList);
            }
            List<TeamDeviceCreateRequest> deviceList= teamCreateOrUpdateRequest.getTeamDeviceCreateRequestList();
            //如果有新增设备，则进行设备的新增设备操作
            if(null != deviceList && 0 < deviceList.size()){
                addDeviceToTeam(deviceList,deviceTeamPo);
            }
            return  new ApiResponse<>(RetCode.OK,"更新设备组成功");
        }catch (Exception e){
            log.error("更新设备组-{}", e);
            return new ApiResponse<>(RetCode.ERROR, "更新设备组");
        }


    }

    /**
     * 将选中设备添加至组
     * sixiaojun
     * 2018-08-30
     * @param teamDeviceCreateRequestList
     * @param deviceTeamPo
     * @return
     */
    public DeviceTeamPo addDeviceToTeam(List<TeamDeviceCreateRequest> teamDeviceCreateRequestList,DeviceTeamPo deviceTeamPo){
        Boolean deviceLinkAge=false;
        List<DeviceTeamItemPo> deviceTeamItemPoList=new ArrayList<>();
        List<DevicePo> devicePoList=new ArrayList<>();
        List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList=new ArrayList<>();
        CustomerUserPo customerUserPo=this.customerUserMapper.selectCustomerByMasterUserId(deviceTeamPo.getMasterUserId());
        CustomerPo customerPo=this.customerMapper.selectByTeamId(deviceTeamPo.getId());
        for(TeamDeviceCreateRequest device:teamDeviceCreateRequestList){
            DeviceCustomerUserRelationPo deviceCustomerUserRelationPo=new DeviceCustomerUserRelationPo();
            DeviceTeamItemPo deviceTeamItemPo=new DeviceTeamItemPo();
            DevicePo devicePo=this.deviceMapper.selectByMac(device.getMac());
            devicePo.setName(device.getName());
            //该设备被添加进入组的同时也被绑定给了当前的终端用户，因此设定此处的绑定状态为已绑定
            devicePo.setBindStatus(DeviceConstant.BIND_STATUS_YES);
            //设定绑定时间
            devicePo.setBindTime(System.currentTimeMillis());
            devicePo.setLastUpdateTime(System.currentTimeMillis());
            deviceTeamItemPo.setDeviceId(devicePo.getId());
            deviceTeamItemPo.setTeamId(deviceTeamPo.getId());
            deviceTeamItemPo.setUserId(customerUserPo.getId());
            deviceTeamItemPo.setStatus(CommonConstant.STATUS_YES);
            deviceTeamItemPo.setLinkAgeStatus(device.getLinkAgeStatus());
            if(DeviceTeamConstants.DEVICE_TEAM_LINKAGE_YES == device.getLinkAgeStatus()){
                deviceLinkAge=true;
            }
            deviceTeamItemPo.setCreateTime(System.currentTimeMillis());
            deviceTeamItemPo.setLastUpdateTime(System.currentTimeMillis());
            deviceCustomerUserRelationPo.setDeviceId(devicePo.getId());
            deviceCustomerUserRelationPo.setCustomerId(customerPo.getId());
            deviceCustomerUserRelationPo.setDefineName(device.getManageName());
            deviceCustomerUserRelationPo.setOpenId(customerUserPo.getOpenId());
            deviceCustomerUserRelationPo.setStatus(CommonConstant.STATUS_YES);
            deviceCustomerUserRelationPo.setCreateTime(System.currentTimeMillis());
            deviceCustomerUserRelationPo.setLastUpdateTime(System.currentTimeMillis());
            devicePoList.add(devicePo);
            deviceTeamItemPoList.add(deviceTeamItemPo);
            deviceCustomerUserRelationPoList.add(deviceCustomerUserRelationPo);
        }
        //如果当前组中存在联动设备，则设定组为联动组
        if(deviceLinkAge){
            deviceTeamPo.setTeamType(DeviceTeamConstants.DEVICE_TEAM_TYPE_LINK);
        }
        //否则为普通终端组
        else {
            deviceTeamPo.setTeamType(DeviceTeamConstants.DEVICE_TEAM_TYPE_USER);
        }
        deviceTeamPo.setLastUpdateTime(System.currentTimeMillis());
        //更新当前组的组类型
        deviceTeamMapper.updateById(deviceTeamPo);
        //进行设备名称的批量更新
        this.deviceMapper.updateBatch(devicePoList);
        this.deviceCustomerUserRelationMapper.insertBatch(deviceCustomerUserRelationPoList);
        //进行设备的批量绑定
        Boolean ret=this.deviceTeamItemMapper.insertBatch(deviceTeamItemPoList)>0;
        if(ret){
            return deviceTeamPo;
        }
        else {
            return null;
        }
    }

    /**
     * 分页查询组列表
     * sixiaojun
     * 2018-09-01
     * @param teamListQueryRequest
     * @return
     */
    public ApiResponse<List<DeviceTeamVo>> queryTeamList(TeamListQueryRequest teamListQueryRequest) throws Exception{
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
            //获取原始创始人的相关信息
            //因为在托管时有是否删除创建人选项，所以此处需要一个逻辑判断
            if(null != deviceTeamPo.getCreateUserId()) {
                customerUserPo = this.customerUserMapper.selectByUserId(deviceTeamPo.getCreateUserId());
                deviceTeamVo.setCreateUserNickName(customerUserPo.getNickname());
                deviceTeamVo.setCreateUserOpenId(customerUserPo.getOpenId());
                deviceTeamVo.setCreateTime(deviceTeamPo.getCreateTime());
            }
            //获取当前管理员的相关信息
            customerUserPo=this.customerUserMapper.selectByUserId(deviceTeamPo.getMasterUserId());
            deviceTeamVo.setOwnerOpenId(customerUserPo.getOpenId());
            deviceTeamVo.setOwnerNickName(customerUserPo.getNickname());
            deviceTeamVo.setCover(deviceTeamPo.getVideoCover());
            deviceTeamVo.setSceneDescription(deviceTeamPo.getSceneDescription());
            //组的使用状态，1-正常，2-删除
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
        if(null == deviceTeamVoList || 0 == deviceTeamVoList.size()){
            return new ApiResponse<>(RetCode.OK,"暂无数据",null);
        }
        return  new ApiResponse<>(RetCode.OK,"查询组列表成功",deviceTeamVoList);
    }

    /**
     * 托管组给指定用户（输入指定用户openId方式）
     * @param teamTrusteeRequest
     * @return
     */
    public CustomerUserPo trusteeTeam(TeamTrusteeRequest teamTrusteeRequest){
        DeviceTeamPo deviceTeamPo=this.deviceTeamMapper.selectById(teamTrusteeRequest.getId());
        //根据masterUserId查询现持有者
        CustomerUserPo currentOwner=this.customerUserMapper.selectByUserId(deviceTeamPo.getMasterUserId());
        CustomerUserPo customerUserPo=this.customerUserMapper.selectByOpenId(teamTrusteeRequest.getOpenId());
        deviceTeamPo.setMasterUserId(customerUserPo.getId());
        //设置的组状态为托管组
        deviceTeamPo.setTeamStatus(DeviceTeamConstants.DEVICE_TEAM_STATUS_TRUSTEE);
        //变更当前设备和用户的绑定关系表，将组托管给另一个用户后同时需要改变设备与用户的绑定关系
        List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList=this.deviceCustomerUserRelationMapper.selectByOpenId(currentOwner.getOpenId());
        List<DeviceCustomerUserRelationPo> updatePos=new ArrayList<>();
        if(0 !=deviceCustomerUserRelationPoList.size()){
            deviceCustomerUserRelationPoList.stream().forEach(deviceCustomerUserRelationPo -> {
                deviceCustomerUserRelationPo.setOpenId(teamTrusteeRequest.getOpenId());
                deviceCustomerUserRelationPo.setLastUpdateTime(System.currentTimeMillis());
                updatePos.add(deviceCustomerUserRelationPo);
            });
        }
        if(teamTrusteeRequest.getDeleteCreator()){
            deviceTeamPo.setCreateUserId(null);
        }
        this.deviceCustomerUserRelationMapper.updateBatch(updatePos);
        Boolean ret=this.deviceTeamMapper.updateById(deviceTeamPo)>0;
        if(ret){
            //成功返回被托管用户的相关信息
            return customerUserPo;
        }
        else {
            return null;
        }
    }

    public ApiResponse<Boolean> deleteOneTeam(TeamDeleteRequest teamDeleteRequest) throws Exception{
        //首先查询改组中是否有绑定的设备，若存在，需要先进行解绑
        List<DeviceTeamItemPo> deviceTeamItemPoList=this.deviceTeamItemMapper.selectByTeamId(teamDeleteRequest.getTeamId());
        if(null != deviceTeamItemPoList && 0 < deviceTeamItemPoList.size()){
            //当存在设备时才进行下面操作
            List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList=new ArrayList<>();
            List<DevicePo> devicePoList=new ArrayList<>();
            //根据组中的deviceId查询与用户、客户的绑定关系
            deviceTeamItemPoList.stream().forEach(deviceTeamItemPo -> {
                DeviceCustomerUserRelationPo deviceCustomerUserRelationPo=this.deviceCustomerUserRelationMapper.selectByDeviceId(deviceTeamItemPo.getDeviceId());
                DevicePo devicePo=this.deviceMapper.selectById(deviceTeamItemPo.getDeviceId());
                devicePo.setBindStatus(DeviceConstant.BIND_STATUS_NO);
                devicePo.setBindTime(null);
                devicePo.setLastUpdateTime(System.currentTimeMillis());
                deviceCustomerUserRelationPoList.add(deviceCustomerUserRelationPo);
                devicePoList.add(devicePo);
            });
            //首先删除设备与组的绑定
            this.deviceTeamItemMapper.deleteBatch(deviceTeamItemPoList);
            //删除设备与用户、客户的绑定关系
            this.deviceCustomerUserRelationMapper.deleteBatch(deviceCustomerUserRelationPoList);
            //批量更新设备信息
            this.deviceMapper.updateBatch(devicePoList);
        }
        //进行组的删除
        DeviceTeamPo deviceTeamPo=this.deviceTeamMapper.selectById(teamDeleteRequest.getTeamId());
        //更新组的状态
        deviceTeamPo.setStatus(CommonConstant.STATUS_DEL);
        List<DeviceTeamScenePo> deviceTeamScenePoList=this.deviceTeamSceneMapper.selectImgVideoList(teamDeleteRequest.getTeamId());
        //删除场景信息
        if(null != deviceTeamItemPoList && 0 < deviceTeamItemPoList.size()) {
            this.deviceTeamSceneMapper.deleteBatch(deviceTeamScenePoList);
        }
        //更新组的状态为删除
        Boolean ret = this.deviceTeamMapper.updateById(deviceTeamPo) > 0;
        if(ret){
            return new ApiResponse<>(RetCode.OK,"删除组成功",ret);
        }
        else {
            return new ApiResponse<>(RetCode.ERROR,"删除组失败",ret);
        }
    }


    /**
     * 创建托管二维码
     * @param teamId
     * @return
     * @throws Exception
     */
    public String createQrCode(@RequestBody  Integer teamId)throws Exception{
        //根据当前传入的teamId查询到当前customer的appId等相关信息
        CustomerPo customerPo=this.customerMapper.selectByTeamId(teamId);
        String appId=customerPo.getAppid();
        String code="https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri=REDIRECT_URI&response_type=code&scope=SCOPE&state=STATE#wechat_redirect";
        return code;
    }

    /**
     * 查询设备与当前用户所对应的客户是否一致
     * @param deviceList
     * @param openId
     * @return
     */
    public DevicePo queryDeviceStatus(List<TeamDeviceCreateRequest> deviceList,String openId){
        for(TeamDeviceCreateRequest queryDevice:deviceList){
            DeviceCustomerRelationPo deviceCustomerRelationPo=this.deviceCustomerRelationMapper.selectByDeviceMac(queryDevice.getMac());
            CustomerUserPo customerUserPo=this.customerUserMapper.selectByOpenId(openId);
            //若当前设备归属的customer与user的归属的customer不一致，返回错误信息
            if(deviceCustomerRelationPo.getCustomerId() != customerUserPo.getCustomerId()){
                return this.deviceMapper.selectByMac(queryDevice.getMac());
            }
        }
        return null;
    }

    /**
     * 查询组的总数
     * @return
     */
    public Integer selectTeamCount(){
        DeviceTeamPo deviceTeamPo=new DeviceTeamPo();
        return this.deviceTeamMapper.selectCount(deviceTeamPo);
    }

    /**
     * 判断当前设备是否已被分组
     * @param teamDeviceCreateRequestList
     * @return
     */
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

    /**
     * 通过组名查询组
     * @param name
     * @return
     */
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

    /**
     * 查询客户已分配的设备
     * @param queryInfoByCustomerRequest
     * @return
     */
    public List<DevicePo> queryDevicesByCustomer(QueryInfoByCustomerRequest queryInfoByCustomerRequest){
        List<DeviceCustomerRelationPo> deviceCustomerRelationPoList=this.deviceCustomerRelationMapper.selectByCustomerId(queryInfoByCustomerRequest.getCustomerId());
        List<DevicePo> devicePoList=new ArrayList<>();
        if(null !=deviceCustomerRelationPoList){
            deviceCustomerRelationPoList.stream().forEach(deviceCustomerRelation->{
                //首先查询当前设备有没有被绑定
                if(null == this.deviceCustomerUserRelationMapper.selectByDeviceId(deviceCustomerRelation.getDeviceId())){
                    DevicePo devicePo=this.deviceMapper.selectById(deviceCustomerRelation.getDeviceId());
                    devicePoList.add(devicePo);
                }
            });
            return  devicePoList;
        }
        else {
            return null;
        }
    }


    /**
     * 查询当前用户是否已关注公众号
     * @param openId
     * @return
     */
    public Boolean queryCustomerUser(String openId){
        CustomerUserPo customerUserPo=this.customerUserMapper.selectByOpenId(openId);
        if(null != customerUserPo){
            return true;
        }
        else {
            return false;
        }
    }
}
