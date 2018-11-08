package com.huanke.iot.base.po.format;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/27 13:13
 */
@Data
public class DeviceModelFormatItemPo {
    private Integer id;
    private Integer modelFormatId;
    private Integer itemId;
    private String abilityIds;
    private Integer showStatus;
    private String showName;
    private Integer status = CommonConstant.STATUS_YES;
    private Long createTime;
    private Long lastUpdateTime;
}
