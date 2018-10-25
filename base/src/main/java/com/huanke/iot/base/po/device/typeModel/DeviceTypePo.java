package com.huanke.iot.base.po.device.typeModel;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/16 15:19
 */
@Data
public class DeviceTypePo {

    private Integer id;
    private String name;
    private String typeNo;
    private String icon;
    private String stopWatch;
    private String source;
    private Integer status = CommonConstant.STATUS_YES;

    private String remark;
    private Long createTime;
    private Long lastUpdateTime;
    private Integer createUserId;
    private Integer updateUserId;

}
