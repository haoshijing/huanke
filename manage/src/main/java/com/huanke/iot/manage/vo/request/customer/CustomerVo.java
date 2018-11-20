package com.huanke.iot.manage.vo.request.customer;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.util.List;

/**
 * 保存客户信息
 */
@Data
public class CustomerVo {

    private Integer id;
    private String name;
    private String publicName;
    private String publicId;
    private String appid;
    private String appsecret;
    private Integer userType;
    private String loginName;
    private String SLD;
    private String remark;  //备注
    private String typeIds;  //设备类型id 集合
    private String modelIds;  //设备型号id 集合
    private String busDirection;//业务场景
    private H5Config h5Config;  //H5配置
    private AndroidConfig androidConfig;    //安卓配置
    private BackendConfig backendConfig;    //管理后台配置

    private Long createTime;
    private Integer createUser;
    private String createUserName;
    private Long lastUpdateTime;
    private Integer lastUpdateUser;
    private String lastUpdateUserName;


    /**
     * 后台配置
     */
    @Data
    public static class BackendConfig {

        //        private Integer id;
        private Integer enableStatus;
        private String logo;
        private String name;
        private Integer type;
        private Integer status = CommonConstant.STATUS_YES;
    }

    /**
     * H5配置
     */
    @Data
    public static class H5Config {
        //        private  Integer id;
        private String password;
        private String serviceUser;
        private String defaultTeamName;
        private String backgroundImg;
        private String htmlTypeIds;
        private String themeName;
        private String logo;
        private String version;
        private Integer status = CommonConstant.STATUS_YES;
        private List<H5BgImg> h5BgImgList;  //背景列表
    }

    /**
     * h5端 背景
     */
    @Data
    public static class H5BgImg {
        private Integer id;
        private String name;    //图片名称
        private String bgImg;    //图片或视频
        private String description;    //描述介绍
        private Integer status = CommonConstant.STATUS_YES;
    }

    /**
     * 安卓配置
     */
    @Data
    public static class AndroidConfig {
        //        private Integer id;
        private String qrcode;   //二维码
        private String name;
        private String logo;
        private String appUrl;
        private String version;
        private String deviceChangePassword;    //设备切换密码
        private Integer status = CommonConstant.STATUS_YES;
        private AndroidScene androidScene;  //场景
    }


    /**
     * 安卓场景
     */
    @Data
    public static class AndroidScene {
        private Integer id;
        private String name;
        private String imgsCover;    //图册封面
        private String description;
        private Integer status = CommonConstant.STATUS_YES;
        private List<AndroidSceneImg> androidSceneImgList;  //图片列表
        private List<AndroidSceneImg> androidSceneVideoList;  //图片列表
    }

    /**
     * 安卓场景中图片
     */
    @Data
    public static class AndroidSceneImg {
        private Integer id;
        private String name;
        private String imgVideo;    //图片或视频
        private String description;
        private Integer status = CommonConstant.STATUS_YES;
    }

    /**
     * 二级域名的名称和logo
     */
    @Data
    public static class BackendLogo {
        private String logo;
        private String name;
    }
}
