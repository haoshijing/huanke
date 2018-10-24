package com.huanke.iot.api.controller.app.response;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.util.List;

@Data
public class AppSceneVo {
    private Integer id;
    private Integer customerId; //客户ID
    private String name;    //场景名称，如：白天，晚上等。
    private String imgsCover;   //图册封面
    private String description;    //描述
    private Integer status = CommonConstant.STATUS_YES;    //状态
    private Long createTime;
    private Long lastUpdateTime;    //最后更新时间
    private List<AndroidSceneImgVo> androidSceneImgs;

    @Data
    public static class AndroidSceneImgVo {
        private Integer id;
        private Integer androidSceneId; //安卓场景ID
        private Integer customerId; //客户主键
        private String name;    //图片名称
        private String imgVideo;    //图片或视频
        private String description;    //描述介绍
        private Integer status = CommonConstant.STATUS_YES;    //状态
        private Long createTime;
        private Long lastUpdateTime;
    }
}