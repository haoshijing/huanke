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

    /**
     * 终端组
     */
    int DEVICE_TEAM_STATUS_TERMINAL = 1;

    /**
     * 自称组
     */
    int DEVICE_TEAM_STATUS_TRUSTEE = 2;

    /**
     * 存在联动设备
     */
    int DEVICE_TEAM_LINKAGE_YES = 1;
    /**
     * 不存在联动设备
     */
    int DEVICE_TEAM_LINKAGE_NO = 2;
}
