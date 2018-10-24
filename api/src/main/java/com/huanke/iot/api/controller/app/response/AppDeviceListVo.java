package com.huanke.iot.api.controller.app.response;

import lombok.Data;
import org.assertj.core.util.Lists;

import java.util.List;

/**
 * @author zhaody
 * @version 2018年10月24日 09:59
 **/
@Data
public class AppDeviceListVo {
    public List<DeviceTeamData> teamDataList;
    @Data
    public static class DeviceTeamData{
        private Integer teamId;
        private String teamName;
        private String icon;
        private String videoUrl;
        private String videoCover;
        private String memo;
        private String qrcode;
        private List<String> adImages;
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
        private Integer deviceId;
        /**
         * MAC
         */
        private String mac;
        /**
         * 微信生成设备deviceId
         */
        private String wxDeviceId;
        /**
         * 从设备数量
         */
        private Integer childDeviceCount;
        /**
         * 设备名称
         */
        private String deviceName;
        /**
         * 客户名称
         */
        private String customerName;
        /**
         * 设备图标
         */
        private String icon;
        /**
         * 设备pm值
         */
        private String pm;
        private String co2;
        private String tem;
        private String hum;
        private String tvoc;
        private String hcho;
        private String deviceTypeName;
        private String deviceModelName;
        private Integer typeId;
        private String typeNo;
        private String androidFormatName;
        private String androidFormatId;
        /**
         * 1-在线，2-离线
         */
        private Integer onlineStatus;
        private String location;
    }
}
