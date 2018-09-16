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
    private String ownerOpenId;
    private Integer ownerUserId;
    private String ownerNickName;
    private String cover;
    private List<ImgVideos> imgVideosList;
    private String sceneDescription;
    private Integer status = CommonConstant.STATUS_YES;
    private Integer teamStatus;
    private Integer teamType;
    private String remark;
    private Integer deviceCount;
    @Data
    public static class ImgVideos{
        private String imgvideo;
    }

}
