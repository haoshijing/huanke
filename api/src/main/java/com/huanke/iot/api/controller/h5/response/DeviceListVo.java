package com.huanke.iot.api.controller.h5.response;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月10日 09:59
 **/
@Data
public class DeviceListVo {
    public List<DeviceGroupData> groupDataList;
    @Data
    public static class DeviceGroupData{
        private Integer groupId;
        private String groupName;
        private String icon;
        private String videoUrl;
        private String videoCover;
        private String memo;
        private List<DeviceItemPo> deviceItemPos = Lists.newArrayList();
        public void addItemPo(DeviceItemPo deviceItemPo){
            deviceItemPos.add(deviceItemPo);
        }
    }

    @Data
    public static class DeviceItemPo{
        /**
         * 设备id
         */
        private String deviceId;
        /**
         * 设备名称
         */
        private String deviceName;
        /**
         * 设备图标
         */
        private String icon;
        /**
         * 设备pm值
         */
        private String pm;
        private String deviceTypeName;
        /**
         * 1-在线，2-离线
         */
        private Integer onlineStatus;
    }


    public static void main(String[] args) {
        DeviceListVo deviceListVo = new DeviceListVo();
        DeviceGroupData deviceGroupData = new DeviceGroupData();
        deviceGroupData.setGroupId(0);
        deviceGroupData.setGroupName("默认组");
        DeviceItemPo deviceItemPo  = new DeviceItemPo();
        deviceItemPo.setIcon("http:xxxxxx");
        deviceItemPo.setDeviceName("测试设备1");
        deviceItemPo.setDeviceId("xxxxxx");
        deviceItemPo.setOnlineStatus(1);
        deviceItemPo.setPm("20");
        deviceGroupData.setDeviceItemPos(Lists.newArrayList(deviceItemPo));
        deviceListVo.setGroupDataList(Lists.newArrayList(deviceGroupData));
        System.out.println(JSON.toJSONString(deviceListVo));
    }
}
