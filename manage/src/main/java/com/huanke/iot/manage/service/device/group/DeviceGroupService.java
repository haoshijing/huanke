package com.huanke.iot.manage.service.device.group;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.device.DeviceGroupItemMapper;
import com.huanke.iot.base.dao.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.device.DeviceGroupSceneMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.device.group.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.group.DeviceGroupPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.group.DeviceGroupScenePo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.vo.request.device.group.GroupCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.group.GroupQueryRequest;
import com.huanke.iot.manage.vo.request.device.operate.DeviceCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.operate.DeviceQueryRequest;
import com.huanke.iot.manage.vo.response.device.group.DeviceGroupDetailVo;
import com.huanke.iot.manage.vo.response.device.group.DeviceGroupListVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DeviceGroupService {

    public static final String MEMO = "1、专家诊断，发现楼宇主要污染源和潜在风险； \n" +
            "2、系统设计，针对楼宇机电系统和运营策略，定制净化技术和阿萨德撒git实施要点； \n" +
            "3、无缝接入，低阻力嵌入式模块产品，无缝接入空调系统和管道系统内部； \n" +
            "4、净霾除醛，即能去除PM2.5，也能净化甲醛、苯； \n" +
            "5、除味杀菌，纳米光子除臭杀菌技术，去除率高达99.9%以上； \n" +
            "6、无忧维护，模块易插拨清洗，无需更换滤芯耗材，全程无二次污染； \n" +
            "7、持续高效，全程低阻力运行，比转统净化节能90%以上； \n" +
            "8、智能管理，搭载智能可视化管理软件，方便中控手机端随时管控； \n" +
            "9、多屏展示，前台大屏、电梯间、手机扫码同步展示，宣传企业社会责任；";

    public static final String DEFAULT_COVER = "http://idcfota.oss-cn-hangzhou.aliyuncs.com/group.png";

    public static final String DEFAULT_VIDEO_URl = "http://idcfota.oss-cn-hangzhou.aliyuncs.com/default.mp4";

    public static final String DEFAULT_ICON = "http://idcfota.oss-cn-hangzhou.aliyuncs.com/group.png";

    @Autowired
    private DeviceGroupMapper deviceGroupMapper;

    @Autowired
    private DeviceMapper deviceMapper;

    @Autowired
    private DeviceGroupItemMapper deviceGroupItemMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private DeviceModelMapper deviceModelMapper;

    @Autowired
    private DeviceGroupSceneMapper deviceGroupSceneMapper;

    @Autowired
    private CustomerService customerService;

    /**
     * 2018-08-18
     * 添加或更新集群，向集群中添加设备
     * sixiaojun
     *
     * @param groupCreateOrUpdateRequest
     * @return
     */
    public ApiResponse<DeviceGroupPo> createOrUpdateGroup(GroupCreateOrUpdateRequest groupCreateOrUpdateRequest) throws Exception {
        DeviceGroupPo insertOrUpdatePo = new DeviceGroupPo();
        BeanUtils.copyProperties(groupCreateOrUpdateRequest, insertOrUpdatePo);
        if (null != groupCreateOrUpdateRequest.getId() && 0 < groupCreateOrUpdateRequest.getId()) {
            //若id为非空则进行更新操作
            insertOrUpdatePo.setLastUpdateTime(System.currentTimeMillis());
            this.deviceGroupMapper.updateById(insertOrUpdatePo);
        }
        //否则进行新增
        else {
            insertOrUpdatePo.setStatus(CommonConstant.STATUS_YES);
            insertOrUpdatePo.setCreateTime(System.currentTimeMillis());
            insertOrUpdatePo.setLastUpdateTime(System.currentTimeMillis());
            this.deviceGroupMapper.insert(insertOrUpdatePo);
            //新增完之后获取groupId
            groupCreateOrUpdateRequest.setId(insertOrUpdatePo.getId());
        }
        //更新集群相册
        List<DeviceGroupScenePo> deviceGroupScenePoList = this.deviceGroupSceneMapper.selectImgVideoList(groupCreateOrUpdateRequest.getId());
        //数据库中已存有记录则先删除记录
        if (null != deviceGroupScenePoList && 0 < deviceGroupScenePoList.size()) {
            this.deviceGroupSceneMapper.deleteBatch(deviceGroupScenePoList);
        }
        deviceGroupScenePoList.clear();
        if (null != groupCreateOrUpdateRequest.getImageVideoList() && 0 < groupCreateOrUpdateRequest.getImageVideoList().size()) {
            groupCreateOrUpdateRequest.getImageVideoList().forEach(imageVideo -> {
                DeviceGroupScenePo deviceGroupScenePo = new DeviceGroupScenePo();
                deviceGroupScenePo.setGroupId(groupCreateOrUpdateRequest.getId());
                deviceGroupScenePo.setImgVideo(imageVideo.getImgVideo());
                deviceGroupScenePo.setCreateTime(System.currentTimeMillis());
                deviceGroupScenePo.setLastUpdateTime(System.currentTimeMillis());
                deviceGroupScenePo.setStatus(CommonConstant.STATUS_YES);
                deviceGroupScenePoList.add(deviceGroupScenePo);
            });
            this.deviceGroupSceneMapper.insertBatch(deviceGroupScenePoList);
        }
        //向集群中添加设备
        //如设备列表中无设备则不进行任何操作
        if (null == groupCreateOrUpdateRequest.getDeviceQueryRequest().getDeviceList() || 0 == groupCreateOrUpdateRequest.getDeviceQueryRequest().getDeviceList().size()) {
            return new ApiResponse<>(RetCode.OK, "新增或更新成功", insertOrUpdatePo);
        } else {
            //查询并删除集群中已有的设备
            List<DeviceGroupItemPo> deviceGroupItemPoList = this.deviceGroupItemMapper.selectByGroupId(insertOrUpdatePo.getId());
            //存在已有设备时才进行删除
            if (null != deviceGroupItemPoList && 0 < deviceGroupItemPoList.size()) {
                this.deviceGroupItemMapper.deleteBatch(deviceGroupItemPoList);
            }
            deviceGroupItemPoList.clear();
            //重新添加设备
            for (DeviceQueryRequest.DeviceQueryList deviceList : groupCreateOrUpdateRequest.getDeviceQueryRequest().getDeviceList()) {
                DevicePo devicePo = this.deviceMapper.selectByMac(deviceList.getMac());
                DeviceGroupItemPo deviceGroupItemPo = new DeviceGroupItemPo();
                deviceGroupItemPo.setGroupId(insertOrUpdatePo.getId());
                deviceGroupItemPo.setDeviceId(devicePo.getId());
                deviceGroupItemPo.setStatus(CommonConstant.STATUS_YES);
                deviceGroupItemPo.setCreateTime(System.currentTimeMillis());
                deviceGroupItemPo.setLastUpdateTime(System.currentTimeMillis());
                deviceGroupItemPoList.add(deviceGroupItemPo);
            }
            //进行批量插入
            this.deviceGroupItemMapper.insertBatch(deviceGroupItemPoList);
            return new ApiResponse<>(RetCode.OK, "新增集群及设备成功", insertOrUpdatePo);
        }
    }

    /**
     * 分页查询群列表
     * 2018-09-25
     * sixiaojun
     *
     * @param groupQueryRequest
     * @return
     * @throws Exception
     */
    public ApiResponse<List<DeviceGroupListVo>> queryGroupByPage(GroupQueryRequest groupQueryRequest) throws Exception {
        //获取该二级域名客户的主键
        Integer customerId = customerService.obtainCustomerId(false);

        List<DeviceGroupListVo> deviceGroupListVoList = new ArrayList<>();
        Integer offset = (groupQueryRequest.getPage() - 1) * groupQueryRequest.getLimit();
        Integer limit = groupQueryRequest.getLimit();
        DeviceGroupPo queryPo = new DeviceGroupPo();
        queryPo.setStatus(null);
        BeanUtils.copyProperties(groupQueryRequest, queryPo);

        if(queryPo.getCustomerId()==null){
            queryPo.setCustomerId(customerId);
        }
        List<DeviceGroupPo> deviceGroupPoList = this.deviceGroupMapper.selectList(queryPo, limit, offset);
        if (null != deviceGroupPoList) {
            deviceGroupPoList.stream().forEach(deviceGroupPo -> {
                DeviceGroupListVo deviceGroupListVo = new DeviceGroupListVo();
                deviceGroupListVo.setId(deviceGroupPo.getId());
                deviceGroupListVo.setName(deviceGroupPo.getName());
                deviceGroupListVo.setIntroduction(deviceGroupPo.getIntroduction());
                deviceGroupListVo.setRemark(deviceGroupPo.getRemark());
                deviceGroupListVo.setStatus(deviceGroupPo.getStatus());
                deviceGroupListVo.setLocation(deviceGroupPo.getCreateLocation());
                //查询当前集群的客户信息
                CustomerPo customerPo = this.customerMapper.selectById(deviceGroupPo.getCustomerId());
                deviceGroupListVo.setCustomerId(customerPo.getId());
                deviceGroupListVo.setCustomerName(customerPo.getName());
                //查询当前集群中的设备总量
                DeviceGroupItemPo groupItemPo = new DeviceGroupItemPo();
                groupItemPo.setGroupId(deviceGroupPo.getId());
                Integer deviceCount = this.deviceGroupItemMapper.selectCount(groupItemPo);
                deviceGroupListVo.setDeviceCount(deviceCount);
                deviceGroupListVo.setCreateTime(deviceGroupPo.getCreateTime());
                deviceGroupListVoList.add(deviceGroupListVo);
            });
        }
        return new ApiResponse<>(RetCode.OK, "集群查询成功", deviceGroupListVoList);
    }


    /**
     * 查询集群总数
     * sixiaojun
     * 2018-09-25
     *
     * @return
     * @throws Exception
     */
    public ApiResponse<Integer> queryGroupCount(Integer status) throws Exception {
        //获取该二级域名客户的主键
        Integer customerId = customerService.obtainCustomerId(false);

        DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
        deviceGroupPo.setStatus(status);
        deviceGroupPo.setCustomerId(customerId);
        Integer groupCount = this.deviceGroupMapper.selectCount(deviceGroupPo);
        return new ApiResponse<>(RetCode.OK, "查询集群总数成功", groupCount);
    }

    /**
     * 根据id查询集群详情
     * sixiaojun
     * 2018-09-26
     *
     * @param groupId
     * @return
     * @throws Exception
     */
    public ApiResponse<DeviceGroupDetailVo> queryGroupById(Integer groupId) throws Exception {
        DeviceGroupDetailVo deviceGroupDetailVo = new DeviceGroupDetailVo();
        DeviceGroupPo deviceGroupPo = this.deviceGroupMapper.selectById(groupId);
        if (null != deviceGroupPo) {
            BeanUtils.copyProperties(deviceGroupPo, deviceGroupDetailVo);
            //查询集群下设备
            List<DeviceGroupItemPo> deviceGroupItemPoList = this.deviceGroupItemMapper.selectByGroupId(groupId);
            if (null != deviceGroupItemPoList && 0 < deviceGroupItemPoList.size()) {
                List<DeviceGroupDetailVo.DeviceInGroup> deviceInGroupList = new ArrayList<>();
                deviceGroupItemPoList.stream().forEach(deviceGroupItemPo -> {
                    DeviceGroupDetailVo.DeviceInGroup device = new DeviceGroupDetailVo.DeviceInGroup();
                    DevicePo devicePo = this.deviceMapper.selectById(deviceGroupItemPo.getDeviceId());
                    BeanUtils.copyProperties(devicePo, device);
                    deviceInGroupList.add(device);
                });
                deviceGroupDetailVo.setDeviceList(deviceInGroupList);
            }
            //查询集群相册
            List<DeviceGroupScenePo> deviceGroupScenePoList = this.deviceGroupSceneMapper.selectImgVideoList(deviceGroupPo.getId());
            if (null != deviceGroupScenePoList && 0 < deviceGroupScenePoList.size()) {
                List<DeviceGroupDetailVo.ImageVideo> imageVideoList = new ArrayList<>();
                deviceGroupScenePoList.stream().forEach(deviceGroupScenePo -> {
                    DeviceGroupDetailVo.ImageVideo imageVideo = new DeviceGroupDetailVo.ImageVideo();
                    imageVideo.setImgVideo(deviceGroupScenePo.getImgVideo());
                    imageVideoList.add(imageVideo);
                });
                deviceGroupDetailVo.setImageVideoList(imageVideoList);
            }
            return new ApiResponse<>(RetCode.OK, "查询集群详情成功", deviceGroupDetailVo);
        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "集群不存在");
        }
    }

    public ApiResponse<Boolean> deleteOneGroup(Integer groupId) throws Exception {
        DeviceGroupPo deviceGroupPo = this.deviceGroupMapper.selectById(groupId);
        if (null != deviceGroupPo) {
            //首先删除集群的中设备
            List<DeviceGroupItemPo> deviceGroupItemPoList = this.deviceGroupItemMapper.selectByGroupId(groupId);
            if (null != deviceGroupItemPoList && 0 < deviceGroupItemPoList.size()) {
                //删除集群下的设备
                this.deviceGroupItemMapper.deleteBatch(deviceGroupItemPoList);
            }
            //查询集群相册
            List<DeviceGroupScenePo> deviceGroupScenePoList = this.deviceGroupSceneMapper.selectImgVideoList(deviceGroupPo.getId());
            if (null != deviceGroupScenePoList && 0 < deviceGroupScenePoList.size()) {
                //删除集群相册
                this.deviceGroupSceneMapper.deleteBatch(deviceGroupScenePoList);
            }
            //最后删除集群
            this.deviceGroupMapper.deleteById(deviceGroupPo.getId());
            return new ApiResponse<>(RetCode.OK, "删除集群成功", true);
        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "集群不存在", false);
        }
    }

    /**
     * 查询设备列的集群信息，若存在直接返回集群的相关信息
     *
     * @param deviceLists
     * @return
     */
    public DeviceGroupPo queryGroupName(List<DeviceQueryRequest.DeviceQueryList> deviceLists) {
        DeviceGroupPo deviceGroupPo = null;
        for (DeviceQueryRequest.DeviceQueryList deviceList : deviceLists) {
            DevicePo devicePo = deviceMapper.selectByMac(deviceList.getMac());
            //若查询到有设备存在集群中，返回该集群的相关信息
            if (null != deviceGroupItemMapper.selectByDeviceId(devicePo.getId())) {
                DeviceGroupItemPo deviceGroupItemPo = deviceGroupItemMapper.selectByDeviceId(devicePo.getId());
                deviceGroupPo = deviceGroupMapper.selectById(deviceGroupItemPo.getGroupId());
            }
        }
        return deviceGroupPo;
    }

    /**
     * 通过集群名查询集群ID
     *
     * @param groupCreateOrUpdateRequest
     * @return
     */
    public DeviceGroupPo queryIdByName(GroupCreateOrUpdateRequest groupCreateOrUpdateRequest) {
        DeviceGroupPo queryPo = new DeviceGroupPo();
        queryPo.setName(groupCreateOrUpdateRequest.getName());
        queryPo.setCustomerId(groupCreateOrUpdateRequest.getCustomerId());
        DeviceGroupPo deviceGroupPo = deviceGroupMapper.queryByName(queryPo);
        if (null != deviceGroupPo) {
            return deviceGroupPo;
        } else {
            return null;
        }
    }

    /**
     * 判定设备列表中的设备是否存在集群冲突
     *
     * @param deviceLists
     * @return
     */
    public Boolean isGroupConflict(List<DeviceQueryRequest.DeviceQueryList> deviceLists) {
        //获取设备列表中第一个不为空的设备的集群ID
        int compareGroupId = -1;
        for (DeviceQueryRequest.DeviceQueryList device : deviceLists) {
            DevicePo devicePo = deviceMapper.selectByMac(device.getMac());
            if (null != deviceGroupItemMapper.selectByDeviceId(devicePo.getId())) {
                compareGroupId = deviceGroupItemMapper.selectByDeviceId(devicePo.getId()).getGroupId();
            }
        }
        //存在有集群的设备才进行比较
        if (-1 != compareGroupId) {
            for (DeviceQueryRequest.DeviceQueryList device : deviceLists) {
                DevicePo devicePo = deviceMapper.selectByMac(device.getMac());
                if (null != deviceGroupItemMapper.selectByDeviceId(devicePo.getId())) {
                    Integer currentGroupId = deviceGroupItemMapper.selectByDeviceId(devicePo.getId()).getGroupId();
                    //若当前设备的集群ID与第一个设备的集群ID，则判定当前设备列表中有多个集群，集群冲突
                    if (compareGroupId != currentGroupId) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 2018-08-20
     * sixiaojun
     * 根据mac地址查询设备表中是否存在相同mac地址的设备，如存在，返回DevicePo，新增失败
     *
     * @param deviceList
     * @return devicePo
     */
    public DeviceQueryRequest.DeviceQueryList queryDeviceByMac(DeviceQueryRequest deviceList) {
        for (DeviceQueryRequest.DeviceQueryList device : deviceList.getDeviceList()) {
            DevicePo devicePo = deviceMapper.selectByMac(device.getMac());
            if (null == devicePo) {
                return device;
            }
        }
        return null;
    }
//
//
//    public List<DeviceGroupItemVo> selectList(int page) {
//        //当期要查询的页
//        Integer currentPage = page;
//        //每页显示的数量
//        Integer limit= 20;
//        //偏移量
//        Integer offset = (currentPage - 1) * limit;
//        DeviceGroupPo queryGroup = new DeviceGroupPo();
//        List<DeviceGroupPo> deviceGroupPos = deviceGroupMapper.selectList(queryGroup, limit, offset);
//        return deviceGroupPos.stream().map(deviceGroupPo -> {
//            DeviceGroupItemVo itemVo = new DeviceGroupItemVo();
//            String icon = deviceGroupPo.getIcon();
//            if (StringUtils.isEmpty(icon)) {
//                icon = DEFAULT_ICON;
//            }
//
//            String videoUrl = deviceGroupPo.getVideoUrl();
//            if (StringUtils.isEmpty(videoUrl)) {
//                videoUrl = DEFAULT_VIDEO_URl;
//            }
//
//            String videoCover = deviceGroupPo.getVideoCover();
//            if (StringUtils.isEmpty(videoCover)) {
//                videoCover = DEFAULT_COVER;
//            }
//
//            String memo = deviceGroupPo.getMemo();
//            if (StringUtils.isEmpty(memo)) {
//                memo = MEMO;
//            }
//            itemVo.setGroupName(deviceGroupPo.getGroupName());
//            itemVo.setId(deviceGroupPo.getId());
//            itemVo.setIcon(icon);
//            Integer userId = deviceGroupPo.getUserId();
//            AppUserPo appUserPo = appUserMapper.selectById(userId);
//            if (appUserPo != null) {
//                itemVo.setMaskNickname(appUserPo.getNickname());
//            }
//            itemVo.setMemo(memo);
//            itemVo.setVideoCover(videoCover);
//            itemVo.setVideoUrl(videoUrl);
//            return itemVo;
//        }).collect(Collectors.toList());
//    }
//
//
//    public void updateGroup(DeviceGroupPo deviceGroupPo) {
//        deviceGroupMapper.updateById(deviceGroupPo);
//    }
}