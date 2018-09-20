package com.huanke.iot.base.po.format;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/27 13:13
 */
@Data
public class WxFormatItemPo {
    private Integer id;
    private Integer formatId;
    private Integer pageId;
    private String name;
    private Integer abilityType;
    private Integer status = CommonConstant.STATUS_YES;
    private Long createTime;
    private Long lastUpdateTime;
}
