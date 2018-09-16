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

    //创建人的微信名称
    private String masterUserOpenId;

    //组缩略图
    //安卓场景的介绍封面
    private String teamCover;

    private String sceneDescription;

    private List<imgOrVideo> imgOrVideoList;

    private List<TeamDeviceCreateRequest> teamDeviceCreateRequestList;

    @Data
    public static class imgOrVideo{
        //图片或视频
        private String imgVideo;
    }

}
