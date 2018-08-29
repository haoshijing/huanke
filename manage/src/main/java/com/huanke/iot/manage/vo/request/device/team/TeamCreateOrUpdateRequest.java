package com.huanke.iot.manage.vo.request.device.team;

import lombok.Data;

@Data
public class TeamCreateOrUpdateRequest {
    //组名
    private String name;
    //创建人的微信名称
    private String createUserWeChat;
    //组缩略图
    private String icon;
    //安卓场景的介绍封面
    private String videoCover;
    private String videoUrl;

}
