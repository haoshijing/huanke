package com.huanke.iot.manage.vo.request.device.operate;

import com.huanke.iot.base.constant.CommonConstant;
import lombok.Data;

/**
 * @author sixiaojun
 * @version 2018-08-15
 * 查询设备列表返回类
 **/
@Data
public class DeviceListQueryRequest {


    private Integer customerId;
    /**
     * 设备id
     */
    private String name;
    private String manageName;

    /**
     * 设备mac地址
     */
    private String mac;
    private String saNo;

    //设备 类型、型号
    private Integer typeId;
    private Integer modelId;


    //分配状态
    private Integer assignStatus;

    //绑定状态
    private Integer bindStatus;
    //在线状态
    private Integer onlineStatus;
    //启用状态
    private Integer enableStatus;
    //工作状态 或 租赁状态
    private Integer workStatus;

    private Integer powerStatus;

    private Integer status;

    private Integer page = 1;
    private Integer limit = 20;
}
