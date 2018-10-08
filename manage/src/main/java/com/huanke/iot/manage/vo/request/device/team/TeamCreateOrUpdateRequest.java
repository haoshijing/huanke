package com.huanke.iot.manage.vo.request.device.team;

import lombok.Data;

import java.util.List;

@Data
public class TeamCreateOrUpdateRequest {

    private Integer id;
    //组名
    private String name;

    private String teamIcon;

    private String remark;

    //创建人的微信名称
    private String createUserOpenId;

    //所有人的微信openid
    private String masterUserOpenId;

    //组缩略图
    //安卓场景的介绍封面
    private String teamCover;

    private String sceneDescription;

    private List<Images> imagesList;

    private List<Videos> videosList;

    private List<TeamDeviceCreateRequest> teamDeviceCreateRequestList;

    @Data
    public static class Images{
        //图片或视频
        private String image;
    }

    @Data
    public static class Videos{
        private String video;
    }

    @Data
    public static class TeamDeviceCreateRequest {

        private String name;
        private String mac;
        private String manageName;
        //联动状态，1-联动，2-不联动
        private Integer linkAgeStatus;
    }

}
