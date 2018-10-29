package com.huanke.iot.base.po.device;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author onlymark
 * @version 2018年08月21日
 **/
@Data
public class DeviceCustomerUserRelationPo {
    private Integer id;
    private Integer customerId;
    private String openId;
    private String nickname;
    private String parentOpenId;
    private Integer deviceId;
    private String defineName;
    private Integer status = CommonConstant.STATUS_YES;
    private Long createTime;
    private Long lastUpdateTime;
}
