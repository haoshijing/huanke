package com.huanke.iot.manage.vo.response.device.customer;

import com.huanke.iot.manage.vo.request.customer.CustomerVo;
import lombok.Data;

import java.util.List;

/**
 * 保存客户信息
 */
@Data
public class CustomerListVo {

    private List<CustomerVo> customerVos;
    private int totalCount;
}
