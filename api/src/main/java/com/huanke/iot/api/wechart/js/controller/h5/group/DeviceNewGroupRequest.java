package com.huanke.iot.api.wechart.js.controller.h5.group;

import lombok.Data;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月08日 13:18
 **/
@Data
public class DeviceNewGroupRequest {
   private String groupName;
   private List<String> deviceIds;
   private String openId;
}
