package com.huanke.iot.api.controller.h5.team;

import lombok.Data;

import java.util.List;

/**
 * @author onlymark
 * @version 2018年08月24日
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
