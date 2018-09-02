package com.huanke.iot.manage.vo.request.device.ablity;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author Caik
 * @date 2018/8/14 19:19
 */
@Data
public class DeviceAblitySetQueryRequest {

    private Integer id;
    private String name;
    private Integer status = CommonConstant.STATUS_YES;
    private String remark;
    private Integer page = 1;
    private Integer limit = 20;
}
