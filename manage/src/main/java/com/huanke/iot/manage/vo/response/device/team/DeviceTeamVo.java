package com.huanke.iot.manage.vo.response.device.team;

import lombok.Data;

import java.util.List;

@Data
public class DeviceTeamVo {
    private Integer id;
    private String name;
    private String icon;
    private String createUserNickName;
    private String createUserOpenId;
    private Long createTime;
    private String ownerOpenId;
    private String ownerNickName;
    private String cover;
    private List<ImgVideos> imgVideosList;
    private String sceneDescription;
    private Integer status;
    private Integer teamStatus;
    private Integer teamType;
    private String remark;
    private Integer deviceCount;
    @Data
    public static class ImgVideos{
        private String imgvideo;
    }

}
