package com.huanke.iot.manage.vo.response.device.typeModel;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

import java.util.List;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceModelVo {

    private Integer id;
    private String name;
    private String modelNo;
    private Integer typeId; //类型id
    private Integer customerId;
    private String productId;
    private String icon;
    private String version;
    private Integer status = CommonConstant.STATUS_YES;
    private String remark;

    private List<DeviceModelAblityVo> deviceModelAblitys;

}
