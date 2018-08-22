package com.huanke.iot.manage.service.device.group;

import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.device.DeviceGroupItemMapper;
import com.huanke.iot.base.dao.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.po.device.group.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.group.DeviceGroupPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.manage.vo.request.device.group.DeviceGroupCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.operate.DeviceCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.operate.DeviceQueryRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    /**
     * 2018-08-18
     * 添加新的集群
     * sixiaojun
     * @param deviceGroupCreateOrUpdate
     * @return
     */
    public DeviceGroupPo createGroup(DeviceGroupCreateOrUpdateRequest deviceGroupCreateOrUpdate){
        DeviceGroupPo queryPo=new DeviceGroupPo();
        queryPo.setName(deviceGroupCreateOrUpdate.getName());
        queryPo.setCustomerId(deviceGroupCreateOrUpdate.getCustomerId());
        DeviceGroupPo deviceGroupPo=deviceGroupMapper.queryByName(queryPo);
        if(null != deviceGroupPo){
            return null;
        }
        else {
            DeviceGroupPo insertPo=new DeviceGroupPo();
            insertPo.setName(deviceGroupCreateOrUpdate.getName());
            insertPo.setCustomerId(deviceGroupCreateOrUpdate.getCustomerId());
            insertPo.setStatus(1);
            insertPo.setCreateTime(System.currentTimeMillis());
            insertPo.setLastUpdateTime(System.currentTimeMillis());
            deviceGroupMapper.insert(insertPo);
            return insertPo;
        }
    }

    /**
     * 向集群中添加选中的设备
     * @param deviceGroupCreateOrUpdateRequest
     * @return
     */
    public Boolean addDeviceToGroup(DeviceGroupCreateOrUpdateRequest deviceGroupCreateOrUpdateRequest,DeviceGroupPo deviceGroupPo){
        //判断当前设备列表中是否存在集群冲突
        if(isGroupConflict(deviceGroupCreateOrUpdateRequest.getDeviceQueryRequest().getDeviceList())){
            return false;
        }
        else {
            for (DeviceQueryRequest.DeviceList deviceList : deviceGroupCreateOrUpdateRequest.getDeviceQueryRequest().getDeviceList()) {
                DevicePo devicePo = deviceMapper.selectByMac(deviceList.getMac());
                DeviceGroupItemPo deviceGroupItemPo = new DeviceGroupItemPo();
                deviceGroupItemPo.setGroupId(deviceGroupPo.getId());
                deviceGroupItemPo.setDeviceId(devicePo.getId());
                deviceGroupItemPo.setStatus(1);
                deviceGroupItemPo.setCreateTime(System.currentTimeMillis());
                deviceGroupItemPo.setLastUpdateTime(System.currentTimeMillis());
                deviceGroupItemMapper.insert(deviceGroupItemPo);
            }
            return true;
        }
    }

    /**
     * 查询设备列的集群信息，若存在直接返回集群的相关信息
     * @param deviceLists
     * @return
     */
    public DeviceGroupPo queryGroupName(List<DeviceQueryRequest.DeviceList> deviceLists){
        DeviceGroupPo deviceGroupPo=null;
        for (DeviceQueryRequest.DeviceList deviceList : deviceLists) {
            DevicePo devicePo = deviceMapper.selectByMac(deviceList.getMac());
            //若查询到有设备存在集群中，返回该集群的相关信息
            if (null != deviceGroupItemMapper.selectByDeviceId(devicePo.getId())) {
                DeviceGroupItemPo deviceGroupItemPo=deviceGroupItemMapper.selectByDeviceId(devicePo.getId());
                deviceGroupPo= deviceGroupMapper.selectById(deviceGroupItemPo.getGroupId());
            }
        }
        return deviceGroupPo;
    }
    /**
     * 通过集群名查询集群ID
     * @param deviceGroupCreateOrUpdateRequest
     * @return
     */
    public DeviceGroupPo queryIdByName(DeviceGroupCreateOrUpdateRequest deviceGroupCreateOrUpdateRequest){
        DeviceGroupPo queryPo=new DeviceGroupPo();
        queryPo.setName(deviceGroupCreateOrUpdateRequest.getName());
        queryPo.setCustomerId(deviceGroupCreateOrUpdateRequest.getCustomerId());
        DeviceGroupPo deviceGroupPo=deviceGroupMapper.queryByName(queryPo);
        if(null != deviceGroupPo){
            return deviceGroupPo;
        }
        else {
            return null;
        }
    }

    /**
     * 判定设备列表中的设备是否存在集群冲突
     * @param deviceLists
     * @return
     */
    public Boolean isGroupConflict(List<DeviceQueryRequest.DeviceList> deviceLists){
        //获取设备列表中第一个不为空的设备的集群ID
        int compareGroupId=-1;
        for(DeviceQueryRequest.DeviceList device:deviceLists){
            DevicePo devicePo=deviceMapper.selectByMac(device.getMac());
            if(null != deviceGroupItemMapper.selectByDeviceId(devicePo.getId())){
                compareGroupId=deviceGroupItemMapper.selectByDeviceId(devicePo.getId()).getGroupId();
            }
        }
        //存在有集群的设备才进行比较
        if(-1 != compareGroupId) {
            for (DeviceQueryRequest.DeviceList device: deviceLists) {
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
//    public Integer selectCount(DeviceGroupQueryRequest request) {
//        DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
//        deviceGroupPo.setGroupName(request.getName());
//        deviceGroupPo.setStatus(1);
//        return deviceGroupMapper.selectCount(deviceGroupPo);
//    }
//
//    public void updateGroup(DeviceGroupPo deviceGroupPo) {
//        deviceGroupMapper.updateById(deviceGroupPo);
//    }
}
