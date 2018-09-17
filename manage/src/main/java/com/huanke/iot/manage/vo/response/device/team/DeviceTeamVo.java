package com.huanke.iot.manage.vo.response.device.team;

import com.huanke.iot.base.constant.CommonConstant;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.List;

@Data
public class DeviceTeamVo {
    //组的Id
    private Integer id;
    private String name;
    private String icon;
    private String createUserNickName;
    private String createUserOpenId;
    private Integer createUserId;
    private Long createTime;
    private String masterOpenId;
    private Integer masterUserId;
    private String masterNickName;
    private String cover;
    private List<ImgVideos> imgVideosList;
    private String sceneDescription;
    private Integer status = CommonConstant.STATUS_YES;
    private Integer teamStatus;
    private Integer teamType;
    private String remark;
    private Integer deviceCount;

    private List<DeviceTeamItemVo> deviceTeamItemVos;


    @Data
    public static class ImgVideos {
        private String imgvideo;
    }

    @Data
    public static class DeviceTeamItemVo {
        /**
         * 设备编组id
         */
        private Integer id;
        /**
         * 设备id
         */
        private Integer deviceId;

        private Integer userId;
        /**
         * 设备组id
         */
        private Integer teamId;

        //设备的联动状态，1-联动，2-不联动
        private Integer linkAgeStatus;

        private Integer status = CommonConstant.STATUS_YES;

    }

}
