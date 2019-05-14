package com.huanke.iot.base.po.customer;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * 安卓配置
 */
@Data
public class AndroidConfigPo {

    private Integer id;
    private Integer customerId; //客户ID
    private String appInfos;
    private String qrcode;  //二维码图标
    private Integer status = CommonConstant.STATUS_YES;   //
    private Long createTime;
    private Long lastUpdateTime;
    private String deviceChangePassword;    //设备切换时的密码

    @Data
    public static class AppInfo {
        private String appNo;
        private String name;
        private String logo;
        private String appUrl;
        private String version;
    }
}
