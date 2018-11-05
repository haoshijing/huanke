package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;
import lombok.NonNull;

import java.util.List;

@Data
public class DeviceUpdateRequest {

    @NonNull
    private Integer id;
    /*设备名称*/
    private String name;

    private String manageName;

    /*设备mac地址*/
    private String mac;

    //设备 类型、型号
    private Integer typeId;
    private Integer modelId;
    private String productId;

    //设备位置
    private String location;

    /**
     * 机器所在ip
     */
    private String ip;

}