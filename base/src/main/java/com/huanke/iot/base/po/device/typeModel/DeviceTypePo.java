package com.huanke.iot.base.po.device.typeModel;

import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import lombok.Data;

import java.util.List;

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
    private String remark;
    private Long createTime;
    private Long lastUpdateTime;

    private List<DeviceAblityPo> deviceAblityPoList;

}
