package com.huanke.iot.api.controller.h5.team;

import lombok.Data;

import java.util.List;

/**
 * @author onlymark
 * @version 2018年08月22日
 **/
@Data
public class DeviceTeamRequest {
   /**
    * 组号
    */
   private Integer teamId;
   /**
    * 设备列表
    */
   private List<String> deviceIds;
}
