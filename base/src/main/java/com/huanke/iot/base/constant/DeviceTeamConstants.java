package com.huanke.iot.base.constant;

/**
 * 描述:
 * 设备组
 *
 * @author onlymark
 * @create 2018-09-06 下午3:58
 */
public interface DeviceTeamConstants {
    /**
     * 设备组类型
     */
    String DEVICE_TEAM_TYPE = "device_team_type";

    /**
     * 设备组类型：用户组
     */
    int DEVICE_TEAM_TYPE_USER = 1;
    /**
     * 设备组类型：联动组
     */
    int DEVICE_TEAM_TYPE_LINK = 2;
    /**
     * 设备组类型：自成组
     */
    int DEVICE_TEAM_TYPE_SELF = 3;
}
