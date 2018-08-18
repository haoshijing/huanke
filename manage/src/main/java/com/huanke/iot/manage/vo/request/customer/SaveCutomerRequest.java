package com.huanke.iot.manage.vo.request.customer;

import com.huanke.iot.manage.vo.message.OtaDeviceVo;
import lombok.Data;

import java.util.List;

/**
 * 保存客户信息
 */
@Data
public class SaveCutomerRequest {

    private Integer id;
    private String name;
    private String publicName;
    private String appid;
    private String appsecret;
    private String userType;
    private String loginName;
    private List<Integer> deviceModelIdList;



    @Data
    public static class H5Config {
        private String type;
        private OtaDeviceVo.VersionPo version;
        private String url;
        private Integer size;
        private String md5;
    }



}
