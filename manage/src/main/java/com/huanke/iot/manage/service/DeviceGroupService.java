package com.huanke.iot.manage.service;

import com.huanke.iot.base.dao.impl.device.DeviceGroupMapper;
import com.huanke.iot.base.dao.impl.user.AppUserMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.DeviceGroupPo;
import com.huanke.iot.base.po.user.AppUserPo;
import com.huanke.iot.manage.controller.device.request.DeviceGroupQueryRequest;
import com.huanke.iot.manage.controller.device.response.DeviceGroupItemVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DeviceGroupService {

    public static final String MEMO = "1、专家诊断，发现楼宇主要污染源和潜在风险； \n" +
            "2、系统设计，针对楼宇机电系统和运营策略，定制净化技术和实施要点； \n" +
            "3、无缝接入，低阻力嵌入式模块产品，无缝接入空调系统和管道系统内部； \n" +
            "4、净霾除醛，即能去除PM2.5，也能净化甲醛、苯、TVOC等污染物； \n" +
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
    private AppUserMapper appUserMapper;

    public List<DeviceGroupItemVo> selectList(DeviceGroupQueryRequest request) {
        DeviceGroupPo queryGroup = new DeviceGroupPo();
        queryGroup.setGroupName(request.getName());
        queryGroup.setStatus(1);
        Integer offset = (request.getPage() - 1)*request.getLimit();
        Integer limit = request.getLimit();
        List<DeviceGroupPo> deviceGroupPos = deviceGroupMapper.selectList(queryGroup,limit,offset);

        return deviceGroupPos.stream().map(deviceGroupPo -> {
            DeviceGroupItemVo itemVo = new DeviceGroupItemVo();
            String icon = deviceGroupPo.getIcon();
            if (StringUtils.isEmpty(icon)) {
                icon = DEFAULT_ICON;
            }

            String videoUrl = deviceGroupPo.getVideoUrl();
            if (StringUtils.isEmpty(videoUrl)) {
                videoUrl = DEFAULT_VIDEO_URl;
            }

            String videoCover = deviceGroupPo.getVideoCover();
            if (StringUtils.isEmpty(videoCover)) {
                videoCover = DEFAULT_COVER;
            }

            String memo = deviceGroupPo.getMemo();
            if (StringUtils.isEmpty(memo)) {
                memo = MEMO;
            }

            itemVo.setGroupName(deviceGroupPo.getGroupName());
            itemVo.setId(deviceGroupPo.getId());
            itemVo.setIcon(icon);
            DeviceGroupItemPo groupItemPo = new DeviceGroupItemPo();
            groupItemPo.setGroupId(deviceGroupPo.getId());
            groupItemPo.setIsMaster(1);
            List<DeviceGroupItemPo> itemPos = deviceGroupMapper.queryGroupItems(groupItemPo);
            if(itemPos.size() > 0){
                DeviceGroupItemPo deviceGroupItemPo = itemPos.get(0);
                Integer userId= deviceGroupItemPo.getUserId();
                AppUserPo appUserPo = appUserMapper.selectById(userId);
                if(appUserPo != null){
                    itemVo.setMaskNickname(appUserPo.getNickname());
                }
            }
            itemVo.setMemo(memo);
            itemVo.setVideoCover(videoCover);
            itemVo.setVideoUrl(videoUrl);
            return itemVo;
        }).collect(Collectors.toList());
    }

    public Integer selectCount(DeviceGroupQueryRequest request) {
        DeviceGroupPo deviceGroupPo = new DeviceGroupPo();
        deviceGroupPo.setGroupName(request.getName());
        deviceGroupPo.setStatus(1);
        return deviceGroupMapper.selectCount(deviceGroupPo);
    }

    public void updateGroup(DeviceGroupPo deviceGroupPo) {
         deviceGroupMapper.updateById(deviceGroupPo);
    }
}
