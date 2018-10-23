package com.huanke.iot.manage.vo.request.format;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author caikun
 * @date 2018/8/28 上午1:16
 **/
@Data
public class WxFormatQueryRequest {
//    private Integer id;
    private String name;
//    private String htmlUrl;
//    private String icon;
//    private String previewImg;
//    private String typeIds;
    private String customerIds;
//    private String version;
    private Integer type;
    private Integer owerType;
    private Integer status = CommonConstant.STATUS_YES;

    private Integer page = 1;
    private Integer limit = 20;

    @Data
    public static class WxFormatItemQueryRequest{
        private Integer formatId;
        private Integer pageId;
        private String name;
        private Integer abilityType;
        private Integer status = CommonConstant.STATUS_YES;

    }
}
