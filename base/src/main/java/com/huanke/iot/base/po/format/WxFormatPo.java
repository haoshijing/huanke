package com.huanke.iot.base.po.format;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/27 13:13
 */
@Data
public class WxFormatPo {
    private Integer id;
    private String name;
    private String htmlUrl;
    private String icon;
    private String previewImg;
    private String typeIds;
    private String customerIds;
    private String version;
    private Integer type;
    private Integer owerType;
    private Integer status = CommonConstant.STATUS_YES;
    private String description;
    private Long createTime;
    private Long lastUpdateTime;
    private Integer createUserId;
    private Integer updateUserId;
}
