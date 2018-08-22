package com.huanke.iot.manage.vo.response.device.typeModel;

import com.huanke.iot.base.po.customer.WxConfigPo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceTypeAblitysVo;
import lombok.Data;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceTypeWxConfigVo {

    private Integer id;
    private String name;
    private String typeNo;
    private String icon;
    private String stopWatch;
    private String remark;
    private String source;

    private List<DeviceTypeAblitysVo> DeviceTypeAblitys;
    private WxConfigPo wxConfigPo;

}
