package com.huanke.iot.manage.vo.request.device.team;

import lombok.Data;

import java.util.List;

@Data
public class TeamCreateOrUpdateRequest {
    //组名
    private String name;
    //创建人的微信名称
    private String createUserOpenId;
    //组缩略图
    private String teamIcon;
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
