package com.huanke.iot.manage.vo.request.customer;

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
    private String appid;
    private String appsecret;
    private Integer userType;
    private String loginName;
    private String SLD;
    private String remark;  //备注
    private List<DeviceModel> deviceModelList;  //设备型号列表
    private H5Config h5Config;  //H5配置
    private AndroidConfig androidConfig;    //安卓配置
    private BackendConfig backendConfig;    //管理后台配置

    /**
     * 后台配置
     */
    @Data
    public static class BackendConfig {
        private Boolean enableStatus;
        private String logoKey;
        private String name;
        private Integer type;
    }

    /**
     * H5配置
     */
    @Data
    public static class H5Config {
        private String password;
        private String defaultTeamName;
        private String backgroundImgKey;
        private Integer htmlTypeId;
        private String themeName;
        private String logoKey;
        private String version;
    }

    /**
     * 设备型号
     */
    @Data
    public static class DeviceModel {
        private String name;
        private Integer typeId;
        private Integer productId;
        private String iconKey;
        private List<DeviceModelAbility> deviceModelAbilityList;    //功能列表
        private String remark;  //备注
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
        private String qrcodeKey;   //二维码
        private String name;
        private String logoKey;
        private String version;
        private String deviceChangePassword;    //设备切换密码
        private List<AndroidScene> androidSceneList;  //场景列表
    }

    /**
     * 安卓场景
     */
    @Data
    public static class AndroidScene {
        private String name;
        private String imgsCoverKey;    //图册封面
        private String describe;
        private List<AndroidSceneImg> androidSceneImgList;  //图片列表
    }

    /**
     * 安卓场景中图片
     */
    @Data
    public static class AndroidSceneImg {
        private String name;
        private String imgVideoKey;    //图片或视频
        private String describe;
    }
}
