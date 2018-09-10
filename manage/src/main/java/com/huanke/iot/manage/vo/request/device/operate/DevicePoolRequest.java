package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

/**
 * @author caikun
 * @date 2018/9/7 下午5:05
 **/
@Data
public class DevicePoolRequest {
    private Integer customerId;
    private String productId;
    private  Integer addCount;
}
