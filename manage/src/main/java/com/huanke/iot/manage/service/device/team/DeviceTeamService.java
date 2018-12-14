package com.huanke.iot.manage.service.device.team;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.constant.DeviceTeamConstants;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.customer.CustomerUserMapper;
import com.huanke.iot.base.dao.device.*;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DeviceCustomerRelationPo;
import com.huanke.iot.base.po.device.DeviceCustomerUserRelationPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import com.huanke.iot.base.po.device.team.DeviceTeamScenePo;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.vo.request.device.operate.QueryInfoByCustomerRequest;
import com.huanke.iot.manage.vo.request.device.team.TeamCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.team.TeamDeleteRequest;
import com.huanke.iot.manage.vo.request.device.team.TeamListQueryRequest;
import com.huanke.iot.manage.vo.request.device.team.TeamTrusteeRequest;
import com.huanke.iot.manage.vo.response.device.team.DeviceTeamVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.net.URLEncoder;
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

    @Autowired
    private CustomerService customerService;


    /**
     * 创建新的自定义组
     * 2018-08-29
     * sixiaojun
     *
     * @param teamCreateOrUpdateRequest
     * @return
     */
    public ApiResponse<Integer> createNewOrUpdateTeam(TeamCreateOrUpdateRequest teamCreateOrUpdateRequest) throws Exception {
        DeviceTeamPo deviceTeamPo = new DeviceTeamPo();
        //根据微信用户openId查询对应的userId
        CustomerUserPo customerUserPo = this.customerUserMapper.selectByOpenId(teamCreateOrUpdateRequest.getCreateUserOpenId());
        //根据当前用户找到用户归属的customer，将改组的customerId设为当前customer
        deviceTeamPo.setCustomerId(customerUserPo.getCustomerId());
        deviceTeamPo.setName(teamCreateOrUpdateRequest.getName());
        deviceTeamPo.setIcon(teamCreateOrUpdateRequest.getIcon());
        //封面
        deviceTeamPo.setVideoCover(teamCreateOrUpdateRequest.getCover());
        //创建之后为托管之前，创建人和materUser为同一人
        deviceTeamPo.setCreateUserId(customerUserPo.getId());
        deviceTeamPo.setMasterUserId(customerUserPo.getId());
        //设置组的状态为终端组
        deviceTeamPo.setTeamStatus(DeviceTeamConstants.DEVICE_TEAM_STATUS_TERMINAL);
        deviceTeamPo.setTeamType(DeviceTeamConstants.DEVICE_TEAM_TYPE_USER);
        deviceTeamPo.setSceneDescription(teamCreateOrUpdateRequest.getSceneDescription());
        //若id非空则进行组的更新操作
        if (null != teamCreateOrUpdateRequest.getId() && 0 < teamCreateOrUpdateRequest.getId()) {
            deviceTeamPo.setId(teamCreateOrUpdateRequest.getId());
            deviceTeamPo.setLastUpdateTime(System.currentTimeMillis());
            this.deviceTeamMapper.updateById(deviceTeamPo);
        }
        //否则进行新增
        else {
            //设置该组的使用状态为正常
            deviceTeamPo.setStatus(CommonConstant.STATUS_YES);
            deviceTeamPo.setCreateTime(System.currentTimeMillis());
            deviceTeamPo.setLastUpdateTime(System.currentTimeMillis());
            //新建组的数据
            this.deviceTeamMapper.insert(deviceTeamPo);
        }
        List<DeviceTeamScenePo> deviceTeamImgScenePoList = this.deviceTeamSceneMapper.selectImgVideoList(deviceTeamPo.getId(), DeviceTeamConstants.IMAGE_VIDEO_MARK_IMAGE);
        //若存在则删除重新插入
        if (null != deviceTeamImgScenePoList && 0 < deviceTeamImgScenePoList.size()) {
            this.deviceTeamSceneMapper.deleteBatch(deviceTeamImgScenePoList);
        }
        List<DeviceTeamScenePo> deviceTeamVideoScenePoList = this.deviceTeamSceneMapper.selectImgVideoList(deviceTeamPo.getId(), DeviceTeamConstants.IMAGE_VIDEO_MARK_VIDEO);
        //若存在则删除重新插入
        if (null != deviceTeamVideoScenePoList && 0 < deviceTeamVideoScenePoList.size()) {
            this.deviceTeamSceneMapper.deleteBatch(deviceTeamVideoScenePoList);
        }
        deviceTeamImgScenePoList.clear();
        deviceTeamVideoScenePoList.clear();
        //插入图册相关数据
        if (teamCreateOrUpdateRequest.getImagesList() != null && teamCreateOrUpdateRequest.getImagesList().size() > 0) {
            teamCreateOrUpdateRequest.getImagesList().stream().forEach(image -> {
                DeviceTeamScenePo deviceTeamScenePo = new DeviceTeamScenePo();
                deviceTeamScenePo.setTeamId(deviceTeamPo.getId());
                deviceTeamScenePo.setImgVideo(image.getImage());
                deviceTeamScenePo.setImgVideoMark(DeviceTeamConstants.IMAGE_VIDEO_MARK_IMAGE);
                deviceTeamScenePo.setStatus(CommonConstant.STATUS_YES);
                deviceTeamScenePo.setCreateTime(System.currentTimeMillis());
                deviceTeamScenePo.setLastUpdateTime(System.currentTimeMillis());
                deviceTeamImgScenePoList.add(deviceTeamScenePo);
            });
            this.deviceTeamSceneMapper.insertBatch(deviceTeamImgScenePoList);
        }
        //插入视频相关数据
        if (teamCreateOrUpdateRequest.getVideosList() != null && teamCreateOrUpdateRequest.getVideosList().size() > 0) {
            teamCreateOrUpdateRequest.getVideosList().stream().forEach(video -> {
                DeviceTeamScenePo deviceTeamScenePo = new DeviceTeamScenePo();
                deviceTeamScenePo.setTeamId(deviceTeamPo.getId());
                deviceTeamScenePo.setImgVideo(video.getVideo());
                deviceTeamScenePo.setImgVideoMark(DeviceTeamConstants.IMAGE_VIDEO_MARK_VIDEO);
                deviceTeamScenePo.setStatus(CommonConstant.STATUS_YES);
                deviceTeamScenePo.setCreateTime(System.currentTimeMillis());
                deviceTeamScenePo.setLastUpdateTime(System.currentTimeMillis());
                deviceTeamVideoScenePoList.add(deviceTeamScenePo);
            });
            this.deviceTeamSceneMapper.insertBatch(deviceTeamVideoScenePoList);
        }
        if (null == teamCreateOrUpdateRequest.getTeamDeviceCreateRequestList() || 0 == teamCreateOrUpdateRequest.getTeamDeviceCreateRequestList().size()) {
            return new ApiResponse<>(RetCode.OK, "新建或更新组成功", deviceTeamPo.getId());
        }
        //向组中加入设备
        else {
            Boolean deviceLinkAge = false;
            List<DevicePo> devicePoList = new ArrayList<>();
            //查询并删除当前组中已存在的设备
            List<DeviceTeamItemPo> deviceTeamItemPoList = this.deviceTeamItemMapper.selectByTeamId(deviceTeamPo.getId());
            //存在设备时才进行删除
            if (null != deviceTeamItemPoList && 0 < deviceTeamItemPoList.size()) {
                deviceTeamItemPoList.stream().forEach(deviceTeamItemPo -> {
                    DevicePo devicePo = this.deviceMapper.selectById(deviceTeamItemPo.getDeviceId());
                    devicePo.setBindStatus(null);
                    devicePo.setBindTime(null);
                    devicePoList.add(devicePo);
                });
                this.deviceMapper.updateBatch(devicePoList);
                this.deviceTeamItemMapper.deleteBatch(deviceTeamItemPoList);
            }
            List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList = this.deviceCustomerUserRelationMapper.selectByOpenId(teamCreateOrUpdateRequest.getCreateUserOpenId());
            //存在设备用户关系时进行删除
            if (null != deviceCustomerUserRelationPoList && 0 < deviceCustomerUserRelationPoList.size()) {
                this.deviceCustomerUserRelationMapper.deleteBatch(deviceCustomerUserRelationPoList);
            }
            deviceTeamItemPoList.clear();
            deviceCustomerUserRelationPoList.clear();
            devicePoList.clear();
            CustomerPo customerPo = this.customerMapper.selectByTeamId(deviceTeamPo.getId());
            for (TeamCreateOrUpdateRequest.TeamDeviceCreateRequest device : teamCreateOrUpdateRequest.getTeamDeviceCreateRequestList()) {
                DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = new DeviceCustomerUserRelationPo();
                DeviceTeamItemPo deviceTeamItemPo = new DeviceTeamItemPo();
                DevicePo devicePo = this.deviceMapper.selectByMac(device.getMac());
                devicePo.setName(device.getName());
                //该设备被添加进入组的同时也被绑定给了当前的终端用户，因此设定此处的绑定状态为已绑定
                devicePo.setBindStatus(DeviceConstant.BIND_STATUS_YES);
                //设定绑定时间
                devicePo.setBindTime(System.currentTimeMillis());
                devicePo.setLastUpdateTime(System.currentTimeMillis());
                deviceTeamItemPo.setDeviceId(devicePo.getId());
                deviceTeamItemPo.setTeamId(deviceTeamPo.getId());
                deviceTeamItemPo.setManageName(device.getManageName());
                deviceTeamItemPo.setUserId(customerUserPo.getId());
                deviceTeamItemPo.setStatus(CommonConstant.STATUS_YES);
                deviceTeamItemPo.setLinkAgeStatus(device.getLinkAgeStatus());
                if (DeviceTeamConstants.DEVICE_TEAM_LINKAGE_YES == device.getLinkAgeStatus()) {
                    deviceLinkAge = true;
                }
                deviceTeamItemPo.setCreateTime(System.currentTimeMillis());
                deviceTeamItemPo.setLastUpdateTime(System.currentTimeMillis());
                deviceCustomerUserRelationPo.setDeviceId(devicePo.getId());
                deviceCustomerUserRelationPo.setCustomerId(customerPo.getId());
                deviceCustomerUserRelationPo.setOpenId(customerUserPo.getOpenId());
                deviceCustomerUserRelationPo.setStatus(CommonConstant.STATUS_YES);
                deviceCustomerUserRelationPo.setCreateTime(System.currentTimeMillis());
                deviceCustomerUserRelationPo.setLastUpdateTime(System.currentTimeMillis());
                devicePoList.add(devicePo);
                deviceTeamItemPoList.add(deviceTeamItemPo);
                deviceCustomerUserRelationPoList.add(deviceCustomerUserRelationPo);
            }
            //如果当前组中存在联动设备，则设定组为联动组
            if (deviceLinkAge) {
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
            this.deviceTeamItemMapper.insertBatch(deviceTeamItemPoList);
            return new ApiResponse<>(RetCode.OK, "新建或更新组及设备成功", deviceTeamPo.getId());
        }
    }

    /**
     * 分页查询组列表
     * sixiaojun
     * 2018-09-01
     *
     * @param teamListQueryRequest
     * @return
     */
    public ApiResponse<List<DeviceTeamVo>> queryTeamList(TeamListQueryRequest teamListQueryRequest) throws Exception {

        //获取该二级域名客户的主键
        Integer customerId = customerService.obtainCustomerId(false);

        //当期要查询的页
        Integer currentPage = teamListQueryRequest.getPage();
        //每页显示的数量
        Integer limit = teamListQueryRequest.getLimit();
        //偏移量
        Integer offset = (currentPage - 1) * limit;
        //查询整个列表，因此先建一个空的deviceTeamPo模型
        DeviceTeamPo queryPo = new DeviceTeamPo();
        queryPo.setName(teamListQueryRequest.getName());
        queryPo.setTeamType(teamListQueryRequest.getTeamType());
        queryPo.setMasterUserId(teamListQueryRequest.getMasterUserId());
        queryPo.setCreateUserId(teamListQueryRequest.getCreateUserId());
        queryPo.setStatus(null == teamListQueryRequest.getStatus() ? CommonConstant.STATUS_YES : teamListQueryRequest.getStatus());

        queryPo.setCustomerId(teamListQueryRequest.getCustomerId() == null ? customerId : customerId);

        List<DeviceTeamPo> deviceTeamPoList = this.deviceTeamMapper.selectList(queryPo, limit, offset);
        List<DeviceTeamVo> deviceTeamVoList = deviceTeamPoList.stream().map(deviceTeamPo -> {
            CustomerUserPo customerUserPo;
            DeviceTeamVo deviceTeamVo = new DeviceTeamVo();
            deviceTeamVo.setId(deviceTeamPo.getId());
            deviceTeamVo.setName(deviceTeamPo.getName());
            deviceTeamVo.setIcon(deviceTeamPo.getIcon());
            //获取原始创始人的相关信息
            //因为在托管时有是否删除创建人选项，所以此处需要一个逻辑判断
            if (null != deviceTeamPo.getCreateUserId()) {
                customerUserPo = this.customerUserMapper.selectByUserId(deviceTeamPo.getCreateUserId());
                //若用户已取消关注则不再显示创建人的相关信息
                if (null != customerUserPo) {
                    deviceTeamVo.setCreateUserNickName(customerUserPo.getNickname());
                    deviceTeamVo.setCreateUserOpenId(customerUserPo.getOpenId());
                    deviceTeamVo.setCreateUserId(customerUserPo.getId());
                }
                deviceTeamVo.setCreateTime(deviceTeamPo.getCreateTime());
            }
            //获取当前管理员的相关信息
            customerUserPo = this.customerUserMapper.selectByUserId(deviceTeamPo.getMasterUserId());
            deviceTeamVo.setMasterOpenId(customerUserPo.getOpenId());
            deviceTeamVo.setMasterNickName(customerUserPo.getNickname());
            //获取管理员所归属的客户
            deviceTeamVo.setCustomerId(customerUserPo.getCustomerId());
            CustomerPo customerPo = this.customerMapper.selectById(customerUserPo.getCustomerId());
            deviceTeamVo.setCustomerName(customerPo.getName());
            deviceTeamVo.setCover(deviceTeamPo.getVideoCover());
            deviceTeamVo.setSceneDescription(deviceTeamPo.getSceneDescription());

            deviceTeamVo.setMasterUserId(deviceTeamPo.getMasterUserId());
            deviceTeamVo.setCreateUserId(deviceTeamPo.getCreateUserId());
            //组的使用状态，1-正常，2-删除
            deviceTeamVo.setStatus(deviceTeamPo.getStatus());
            //获取组的状态，终端组或是托管组
            deviceTeamVo.setTeamStatus(deviceTeamPo.getTeamStatus());
            //获取组的类型，用户组或是联动组
            deviceTeamVo.setTeamType(deviceTeamPo.getTeamType());
            deviceTeamVo.setRemark(deviceTeamPo.getRemark());
            List<DeviceTeamItemPo> deviceTeamItemPos = deviceTeamItemMapper.selectByTeamId(deviceTeamPo.getId());
            if (deviceTeamItemPos != null && deviceTeamItemPos.size() > 0) {
                List<DeviceTeamVo.DeviceTeamItemVo> deviceTeamItemVos = deviceTeamItemPos.stream().map(deviceTeamItemPo -> {
                    DeviceTeamVo.DeviceTeamItemVo deviceTeamItemVo = new DeviceTeamVo.DeviceTeamItemVo();
                    deviceTeamItemVo.setId(deviceTeamItemPo.getId());
                    deviceTeamItemVo.setDeviceId(deviceTeamItemPo.getDeviceId());
                    //根据deviceId查询deviceName和mac
                    DevicePo devicePo = this.deviceMapper.selectById(deviceTeamItemPo.getDeviceId());
                    deviceTeamItemVo.setName(devicePo.getName());
                    deviceTeamItemVo.setMac(devicePo.getMac());
                    deviceTeamItemPo.setManageName(deviceTeamItemPo.getManageName());
                    deviceTeamItemVo.setLinkAgeStatus(deviceTeamItemPo.getLinkAgeStatus());
                    deviceTeamItemVo.setStatus(deviceTeamItemPo.getStatus());
                    deviceTeamItemVo.setTeamId(deviceTeamItemPo.getTeamId());
                    deviceTeamItemVo.setUserId(deviceTeamItemPo.getUserId());
                    return deviceTeamItemVo;
                }).collect(Collectors.toList());
                deviceTeamVo.setTeamDeviceCreateRequestList(deviceTeamItemVos);
                deviceTeamVo.setDeviceCount(deviceTeamItemVos.size());
            } else {
                deviceTeamVo.setTeamDeviceCreateRequestList(null);
                deviceTeamVo.setDeviceCount(0);
            }
            //查询图册
            List<DeviceTeamScenePo> deviceTeamImgScenePoList = this.deviceTeamSceneMapper.selectImgVideoList(deviceTeamPo.getId(), DeviceTeamConstants.IMAGE_VIDEO_MARK_IMAGE);
            List<DeviceTeamVo.Images> imagesList = deviceTeamImgScenePoList.stream().map(eachPo -> {
                DeviceTeamVo.Images image = new DeviceTeamVo.Images();
                image.setImage(eachPo.getImgVideo());
                return image;
            }).collect(Collectors.toList());
            deviceTeamVo.setImagesList(imagesList);
            //查询视频
            List<DeviceTeamScenePo> deviceTeamVideoScenePoList = this.deviceTeamSceneMapper.selectImgVideoList(deviceTeamPo.getId(), DeviceTeamConstants.IMAGE_VIDEO_MARK_VIDEO);
            List<DeviceTeamVo.Videos> videosList = deviceTeamVideoScenePoList.stream().map(eachPo -> {
                DeviceTeamVo.Videos video = new DeviceTeamVo.Videos();
                video.setVideo(eachPo.getImgVideo());
                return video;
            }).collect(Collectors.toList());
            deviceTeamVo.setVideosList(videosList);
            return deviceTeamVo;
        }).collect(Collectors.toList());
        if (null == deviceTeamVoList || 0 == deviceTeamVoList.size()) {
            return new ApiResponse<>(RetCode.OK, "暂无数据", null);
        }
        return new ApiResponse<>(RetCode.OK, "查询组列表成功", deviceTeamVoList);
    }


    /**
     * 查询设备组详情
     * caik
     * 2018-09-01
     *
     * @param teamId
     * @return
     */
    public ApiResponse<DeviceTeamVo> queryTeamById(Integer teamId) throws Exception {

        DeviceTeamPo deviceTeamPo = this.deviceTeamMapper.selectById(teamId);

        if (null != deviceTeamPo) {
            CustomerUserPo customerUserPo;
            DeviceTeamVo deviceTeamVo = new DeviceTeamVo();
            deviceTeamVo.setId(deviceTeamPo.getId());
            deviceTeamVo.setName(deviceTeamPo.getName());
            deviceTeamVo.setIcon(deviceTeamPo.getIcon());

            //获取原始创始人的相关信息
            //因为在托管时有是否删除创建人选项，所以此处需要一个逻辑判断
            if (null != deviceTeamPo.getCreateUserId()) {
                customerUserPo = this.customerUserMapper.selectByUserId(deviceTeamPo.getCreateUserId());
                deviceTeamVo.setCreateUserNickName(customerUserPo.getNickname());
                deviceTeamVo.setCreateUserOpenId(customerUserPo.getOpenId());
                deviceTeamVo.setCreateUserId(customerUserPo.getId());
                deviceTeamVo.setCreateTime(deviceTeamPo.getCreateTime());
            }

            //获取当前管理员的相关信息
            customerUserPo = this.customerUserMapper.selectByUserId(deviceTeamPo.getMasterUserId());
            deviceTeamVo.setMasterOpenId(customerUserPo.getOpenId());
            deviceTeamVo.setMasterNickName(customerUserPo.getNickname());
            //获取管理员所归属的客户
            deviceTeamVo.setCustomerId(customerUserPo.getCustomerId());
            CustomerPo customerPo = this.customerMapper.selectById(customerUserPo.getCustomerId());
            deviceTeamVo.setCustomerName(customerPo.getName());
            deviceTeamVo.setCover(deviceTeamPo.getVideoCover());
            deviceTeamVo.setSceneDescription(deviceTeamPo.getSceneDescription());

            deviceTeamVo.setMasterUserId(deviceTeamPo.getMasterUserId());
            deviceTeamVo.setCreateUserId(deviceTeamPo.getCreateUserId());
            //组的使用状态，1-正常，2-删除
            deviceTeamVo.setStatus(deviceTeamPo.getStatus());
            //获取组的状态，终端组或是托管组
            deviceTeamVo.setTeamStatus(deviceTeamPo.getTeamStatus());
            //获取组的类型，用户组或是联动组
            deviceTeamVo.setTeamType(deviceTeamPo.getTeamType());
            deviceTeamVo.setRemark(deviceTeamPo.getRemark());

            List<DeviceTeamScenePo> deviceImgTeamScenePoList = this.deviceTeamSceneMapper.selectImgVideoList(deviceTeamPo.getId(), DeviceTeamConstants.IMAGE_VIDEO_MARK_IMAGE);
            List<DeviceTeamVo.Images> imagesList = deviceImgTeamScenePoList.stream().map(eachPo -> {
                DeviceTeamVo.Images image = new DeviceTeamVo.Images();
                image.setImage(eachPo.getImgVideo());
                return image;
            }).collect(Collectors.toList());
            deviceTeamVo.setImagesList(imagesList);

            List<DeviceTeamScenePo> deviceVideoTeamScenePoList = this.deviceTeamSceneMapper.selectImgVideoList(deviceTeamPo.getId(), DeviceTeamConstants.IMAGE_VIDEO_MARK_VIDEO);
            List<DeviceTeamVo.Videos> videosList = deviceVideoTeamScenePoList.stream().map(eachPo -> {
                DeviceTeamVo.Videos video = new DeviceTeamVo.Videos();
                video.setVideo(eachPo.getImgVideo());
                return video;
            }).collect(Collectors.toList());
            deviceTeamVo.setVideosList(videosList);

            List<DeviceTeamItemPo> deviceTeamItemPos = deviceTeamItemMapper.selectByTeamId(deviceTeamPo.getId());
            if (deviceTeamItemPos != null && deviceTeamItemPos.size() > 0) {
                List<DeviceTeamVo.DeviceTeamItemVo> deviceTeamItemVos = deviceTeamItemPos.stream().map(deviceTeamItemPo -> {
                    DeviceTeamVo.DeviceTeamItemVo deviceTeamItemVo = new DeviceTeamVo.DeviceTeamItemVo();
                    deviceTeamItemVo.setId(deviceTeamItemPo.getId());
                    deviceTeamItemVo.setDeviceId(deviceTeamItemPo.getDeviceId());
                    //根据deviceId查询deviceName和mac
                    DevicePo devicePo = this.deviceMapper.selectById(deviceTeamItemPo.getDeviceId());
                    deviceTeamItemVo.setName(devicePo.getName());
                    deviceTeamItemVo.setMac(devicePo.getMac());
                    deviceTeamItemPo.setManageName(deviceTeamItemPo.getManageName());
                    deviceTeamItemVo.setLinkAgeStatus(deviceTeamItemPo.getLinkAgeStatus());
                    deviceTeamItemVo.setStatus(deviceTeamItemPo.getStatus());
                    deviceTeamItemVo.setTeamId(deviceTeamItemPo.getTeamId());
                    deviceTeamItemVo.setUserId(deviceTeamItemPo.getUserId());
                    return deviceTeamItemVo;
                }).collect(Collectors.toList());
                deviceTeamVo.setTeamDeviceCreateRequestList(deviceTeamItemVos);
                deviceTeamVo.setDeviceCount(deviceTeamItemVos.size());
            } else {
                deviceTeamVo.setTeamDeviceCreateRequestList(null);
                deviceTeamVo.setDeviceCount(0);
            }

            return new ApiResponse<>(RetCode.OK, "查询组成功", deviceTeamVo);

        } else {
            return new ApiResponse<>(RetCode.OK, "不存在该组", null);
        }
    }

    /**
     * 托管组给指定用户（输入指定用户openId方式）
     *
     * @param teamTrusteeRequest
     * @return
     */
    @Transactional
    public CustomerUserPo trusteeTeam(TeamTrusteeRequest teamTrusteeRequest) {
        DeviceTeamPo deviceTeamPo = this.deviceTeamMapper.selectById(teamTrusteeRequest.getId());
        Integer masterUserId = deviceTeamPo.getMasterUserId();
        Integer teamId = deviceTeamPo.getId();
        //判断设备组下设备是否该用户是主绑定人
        List<DeviceTeamItemPo> deviceTeamItemPos = deviceTeamItemMapper.selectByTeamId(teamId);
        List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPos = deviceCustomerUserRelationMapper.selectByUserId(masterUserId);
        List<Integer> deviceIdList = deviceCustomerUserRelationPos.stream().map(e -> e.getDeviceId()).collect(Collectors.toList());
        for (DeviceTeamItemPo deviceTeamItemPo : deviceTeamItemPos) {
            if(!deviceIdList.contains(deviceTeamItemPo.getDeviceId())){
                throw new BusinessException("无法托管非该用户主绑定设备，设备id=" + deviceTeamItemPo.getDeviceId());
            }
        }
        //根据masterUserId查询现持有者
        CustomerUserPo currentOwner = this.customerUserMapper.selectByUserId(masterUserId);
        CustomerUserPo customerUserPo = this.customerUserMapper.selectByOpenId(teamTrusteeRequest.getOpenId());
        deviceTeamPo.setMasterUserId(customerUserPo.getId());
        //设置的组状态为托管组
        deviceTeamPo.setTeamStatus(DeviceTeamConstants.DEVICE_TEAM_STATUS_TRUSTEE);
        //变更当前设备和用户的绑定关系表，将组托管给另一个用户后同时需要改变设备与用户的绑定关系
        List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList = this.deviceCustomerUserRelationMapper.selectByOpenId(currentOwner.getOpenId());
        List<DeviceCustomerUserRelationPo> updatePos = new ArrayList<>();
        if (0 != deviceCustomerUserRelationPoList.size()) {
            deviceCustomerUserRelationPoList.stream().forEach(deviceCustomerUserRelationPo -> {
                deviceCustomerUserRelationPo.setOpenId(teamTrusteeRequest.getOpenId());
                deviceCustomerUserRelationPo.setLastUpdateTime(System.currentTimeMillis());
                updatePos.add(deviceCustomerUserRelationPo);
            });
        }
        if (teamTrusteeRequest.getDeleteCreator()) {
            deviceTeamPo.setCreateUserId(null);
        }
        this.deviceCustomerUserRelationMapper.updateBatch(updatePos);
        Boolean ret = this.deviceTeamMapper.updateById(deviceTeamPo) > 0;
        List<Integer> updateItemIds = deviceTeamItemPos.stream().map(e -> e.getId()).collect(Collectors.toList());
        deviceTeamItemMapper.trusteeTeamItems(updateItemIds, customerUserPo.getId());
        if (ret) {
            //成功返回被托管用户的相关信息
            return customerUserPo;
        } else {
            return null;
        }
    }

    public ApiResponse<Boolean> deleteOneTeam(TeamDeleteRequest teamDeleteRequest) throws Exception {
        //首先查询改组中是否有绑定的设备，若存在，需要先进行解绑
        List<DeviceTeamItemPo> deviceTeamItemPoList = this.deviceTeamItemMapper.selectByTeamId(teamDeleteRequest.getTeamId());
        if (null != deviceTeamItemPoList && 0 < deviceTeamItemPoList.size()) {
            //当存在设备时才进行下面操作
            List<DeviceCustomerUserRelationPo> deviceCustomerUserRelationPoList = new ArrayList<>();
            List<DevicePo> devicePoList = new ArrayList<>();
            //根据组中的deviceId查询与用户、客户的绑定关系
            deviceTeamItemPoList.stream().forEach(deviceTeamItemPo -> {
                DeviceCustomerUserRelationPo deviceCustomerUserRelationPo = this.deviceCustomerUserRelationMapper.selectByDeviceId(deviceTeamItemPo.getDeviceId());
                DevicePo devicePo = this.deviceMapper.selectById(deviceTeamItemPo.getDeviceId());
                devicePo.setBindStatus(DeviceConstant.BIND_STATUS_NO);
                devicePo.setBindTime(null);
                devicePo.setLastUpdateTime(System.currentTimeMillis());
                deviceCustomerUserRelationPoList.add(deviceCustomerUserRelationPo);
                devicePoList.add(devicePo);
            });
            //首先删除设备与组的绑定
            this.deviceTeamItemMapper.deleteByTeamId(teamDeleteRequest.getTeamId());
            //删除设备与用户、客户的绑定关系
            this.deviceCustomerUserRelationMapper.deleteBatch(deviceCustomerUserRelationPoList);
            //批量更新设备信息
            this.deviceMapper.updateBatch(devicePoList);
        }
//        List<DeviceTeamScenePo> deviceTeamScenePoList = this.deviceTeamSceneMapper.selectImgVideoList(teamDeleteRequest.getTeamId());
        //删除场景信息
//        if (null != deviceTeamItemPoList && 0 < deviceTeamItemPoList.size()) {
//        }
        this.deviceTeamSceneMapper.deleteByTeamId(teamDeleteRequest.getTeamId());
        //进行组的删除
        DeviceTeamPo deviceTeamPo = this.deviceTeamMapper.selectById(teamDeleteRequest.getTeamId());
        //更新组的状态
        deviceTeamPo.setStatus(CommonConstant.STATUS_DEL);

        //更新组的状态为删除
        Boolean ret = this.deviceTeamMapper.updateById(deviceTeamPo) > 0;
        if (ret) {
            return new ApiResponse<>(RetCode.OK, "删除组成功", ret);
        } else {
            return new ApiResponse<>(RetCode.ERROR, "删除组失败", ret);
        }
    }


    /**
     * 创建托管二维码
     *
     * @param teamId
     * @return
     * @throws Exception
     */
    public String createQrCode(@RequestBody Integer teamId) throws Exception {
        //根据当前传入的teamId查询到当前customer的appId等相关信息
        CustomerPo customerPo = this.customerMapper.selectByTeamId(teamId);
        String appId = customerPo.getAppid();
        String redirect_uri = "http://" + customerPo.getSLD() + ".hcocloud.com/h5/auth";
        String code = "https://open.weixin.qq.com/connect/oauth2/authorize?appid="+appId+"&redirect_uri="
                + URLEncoder.encode(redirect_uri, "UTF-8")+ "?teamId=" + teamId +"&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
        return code;
    }

    /**
     * 查询设备与当前用户所对应的客户是否一致
     *
     * @param deviceList
     * @param openId
     * @return
     */
    public DevicePo queryDeviceStatus(List<TeamCreateOrUpdateRequest.TeamDeviceCreateRequest> deviceList, String openId) {
        for (TeamCreateOrUpdateRequest.TeamDeviceCreateRequest queryDevice : deviceList) {
            DeviceCustomerRelationPo deviceCustomerRelationPo = this.deviceCustomerRelationMapper.selectByDeviceMac(queryDevice.getMac());
            CustomerUserPo customerUserPo = this.customerUserMapper.selectByOpenId(openId);
            //若当前设备归属的customer与user的归属的customer不一致，返回错误信息
            if (deviceCustomerRelationPo.getCustomerId() != customerUserPo.getCustomerId()) {
                return this.deviceMapper.selectByMac(queryDevice.getMac());
            }
        }
        return null;
    }

    /**
     * 查询组的总数
     *
     * @return
     */
    public Integer selectTeamCount(Integer status) {
        DeviceTeamPo deviceTeamPo = new DeviceTeamPo();
        //获取该二级域名客户的主键
        Integer customerId = customerService.obtainCustomerId(false);
        deviceTeamPo.setStatus(status);
        deviceTeamPo.setCustomerId(customerId);
        return this.deviceTeamMapper.selectCount(deviceTeamPo);
    }

    /**
     * 2018-08-20
     * sixiaojun
     * 根据设备列表中的设备mac查询某个设备是否已被分配
     *
     * @param deviceList
     * @return
     */
    public TeamCreateOrUpdateRequest.TeamDeviceCreateRequest queryDeviceCustomer(TeamCreateOrUpdateRequest deviceList) {
        Integer customerId = this.customerService.obtainCustomerId(false);
        //二、三级客户
        if (null != customerId) {
            log.info("当前客户id不为空");
            for (TeamCreateOrUpdateRequest.TeamDeviceCreateRequest device : deviceList.getTeamDeviceCreateRequestList()) {
                DeviceCustomerRelationPo deviceCustomerRelationPo = this.deviceCustomerRelationMapper.selectByDeviceMac(device.getMac());
                //如果当前设备已被分配或未被分配则返回错误
                if (null == deviceCustomerRelationPo || (null != deviceCustomerRelationPo && customerId != deviceCustomerRelationPo.getCustomerId())) {
                    return device;
                }
            }
        } else {
            //超管
            log.info("当前客户id为空,身份为超级管理员");
            for (TeamCreateOrUpdateRequest.TeamDeviceCreateRequest device : deviceList.getTeamDeviceCreateRequestList()) {
                DeviceCustomerRelationPo deviceCustomerRelationPo = this.deviceCustomerRelationMapper.selectByDeviceMac(device.getMac());
                //如果当前设备已被分配或未被分配则返回错误
                if (null == deviceCustomerRelationPo) {
                    return device;
                }
            }
        }
        return null;
    }


    /**
     * 2018-08-20
     * sixiaojun
     * 根据mac地址查询设备表中是否存在相同mac地址的设备，如存在，返回DevicePo，新增失败
     *
     * @param deviceList
     * @return devicePo
     */
    public TeamCreateOrUpdateRequest.TeamDeviceCreateRequest queryDeviceByMac(TeamCreateOrUpdateRequest deviceList) {
        for (TeamCreateOrUpdateRequest.TeamDeviceCreateRequest device : deviceList.getTeamDeviceCreateRequestList()) {
            DevicePo devicePo = deviceMapper.selectByMac(device.getMac());
            if (null == devicePo) {
                return device;
            }
        }
        return null;
    }

    /**
     * 判断当前用户下 当前设备是否已被分组
     *
     * @param teamDeviceCreateRequest
     * @return
     */
    public TeamCreateOrUpdateRequest.TeamDeviceCreateRequest isDeviceHasTeam(TeamCreateOrUpdateRequest teamDeviceCreateRequest) {
        //当跟新组状态时，与当前openId一致的设备不做筛选
        if (null != teamDeviceCreateRequest.getId() && 0 < teamDeviceCreateRequest.getId()) {
            for (TeamCreateOrUpdateRequest.TeamDeviceCreateRequest device : teamDeviceCreateRequest.getTeamDeviceCreateRequestList()) {
                DevicePo devicePo = this.deviceMapper.selectByMac(device.getMac());
                DeviceTeamItemPo queryDeviceTeamItemPo = this.deviceTeamItemMapper.selectByJoinOpenId(devicePo.getId(), teamDeviceCreateRequest.getCreateUserOpenId());
                if (null != queryDeviceTeamItemPo) {
                    continue;
                } else {
                    queryDeviceTeamItemPo = this.deviceTeamItemMapper.selectByDeviceMac(device.getMac());
                    if (null != queryDeviceTeamItemPo) {
                        return device;
                    }
                }
            }
        } else {
            for (TeamCreateOrUpdateRequest.TeamDeviceCreateRequest device : teamDeviceCreateRequest.getTeamDeviceCreateRequestList()) {
                //若查询到该设备已有组，则返回该设备
                DeviceTeamItemPo queryDeviceTeamItemPo = this.deviceTeamItemMapper.selectByDeviceMac(device.getMac());
                if (null != queryDeviceTeamItemPo) {
                    return device;
                }
            }
        }
        return null;
    }

    /**
     * 通过组名查询组
     *
     * @param name
     * @return
     */
    public DeviceTeamPo queryTeamByName(String name) {
        DeviceTeamPo deviceTeamPo = new DeviceTeamPo();
        deviceTeamPo.setName(name);
        DeviceTeamPo queryPo = this.deviceTeamMapper.queryByName(deviceTeamPo);
        if (null != queryPo) {
            return queryPo;
        } else {
            return null;
        }
    }

    /**
     * 查询客户已分配的设备
     *
     * @param queryInfoByCustomerRequest
     * @return
     */
    public List<DevicePo> queryDevicesByCustomer(QueryInfoByCustomerRequest queryInfoByCustomerRequest) {
        List<DeviceCustomerRelationPo> deviceCustomerRelationPoList = this.deviceCustomerRelationMapper.selectByCustomerId(queryInfoByCustomerRequest.getCustomerId());
        List<DevicePo> devicePoList = new ArrayList<>();
        if (null != deviceCustomerRelationPoList) {
            deviceCustomerRelationPoList.stream().forEach(deviceCustomerRelation -> {
                //首先查询当前设备有没有被绑定
                if (null == this.deviceCustomerUserRelationMapper.selectByDeviceId(deviceCustomerRelation.getDeviceId())) {
                    DevicePo devicePo = this.deviceMapper.selectById(deviceCustomerRelation.getDeviceId());
                    devicePoList.add(devicePo);
                }
            });
            return devicePoList;
        } else {
            return null;
        }
    }


    /**
     * 查询当前用户是否已关注公众号
     *
     * @param openId
     * @return
     */
    public CustomerUserPo queryCustomerUser(String openId) {
        CustomerUserPo customerUserPo = this.customerUserMapper.selectByOpenId(openId);
        return customerUserPo;
    }
}
