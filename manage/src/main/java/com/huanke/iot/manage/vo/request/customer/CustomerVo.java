package com.huanke.iot.manage.vo.request.customer;

import com.huanke.iot.manage.vo.response.device.typeModel.DeviceTypeVo;
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
    private Integer publicId;
    private String appid;
    private String appsecret;
    private Integer userType;
    private String loginName;
    private String SLD;
    private String remark;  //备注
    private String  typeIds;  //设备类型id 集合
    private String  modelIds;  //设备型号id 集合
    private H5Config h5Config;  //H5配置
    private AndroidConfig androidConfig;    //安卓配置
    private BackendConfig backendConfig;    //管理后台配置

    /**
     * 设备类型主键
     */
    @Data
    public static class DeviceType{
        private Integer typeId;
    }
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
        private Integer status;
    }

    /**
     * H5配置
     */
    @Data
    public static class H5Config {
//        private  Integer id;
        private String password;
        private String defaultTeamName;
        private String backgroundImg;
        private Integer htmlTypeId;
        private String themeName;
        private String logo;
        private String version;
        private Integer status;
    }

    @Data
    public static class DeviceModelAbility {
        private Integer ablityId;
        private String definedName;
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
        private String version;
        private String deviceChangePassword;    //设备切换密码
        private Integer status;
//        private List<AndroidBgImg> androidBgImgList;  //背景列表
        private List<AndroidScene> androidSceneList;  //场景列表
    }

    /**
     * 安卓背景
     */
    @Data
    public static class AndroidBgImg{
        private Integer id;
        private String name;    //图片名称
        private String bgImg;    //图片或视频
        private String description;    //描述介绍
        private Integer status;
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
        private Integer status;
        private List<AndroidSceneImg> androidSceneImgList;  //图片列表
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
        private Integer status;
    }
}
