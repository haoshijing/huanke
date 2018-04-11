package com.huanke.iot.api.controller.h5.group;

import lombok.Data;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月08日 13:18
 **/
@Data
public class DeviceGroupRequest {
   /**
    * 组号
    */
   private Integer groupId;
   /**
    * 设备列表
    */
   private List<String> deviceIds;
}
