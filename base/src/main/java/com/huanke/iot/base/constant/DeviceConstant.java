package com.huanke.iot.base.constant;

/**
 * @author Caik
 * @date 2018/8/14 18:07
 */
public class DeviceConstant {

    /*分配状态*/
    public static final Integer ASSIGN_STATUS_YES = 1; //已分配
    public static final Integer ASSIGN_STATUS_NO = 0;//未分配

    /*绑定状态*/
    public static final Integer BIND_STATUS_YES = 1; //已绑定
    public static final Integer BIND_STATUS_NO = 0;//未绑定

    /*在线状态*/
    public static final Integer ONLINE_STATUS_YES = 1; //在线
    public static final Integer ONLINE_STATUS_NO = 0;//离线

    /*工作状态*/
    public static final Integer WORKING_STATUS_YES = 1; // 开机/租赁中
    public static final Integer WORKING_STATUS_NO = 0;// 关机/空闲

    /*启用状态*/
    public static final Integer ENABLE_STATUS_YES = 1; // 启用
    public static final Integer ENABLE_STATUS_NO = 0;// 禁用


    /*能力类型*/
    public static final Integer ABILITY_TYPE_TEXT = 1; //文本类
    public static final Integer ABILITY_TYPE_RADIO = 2; //单选类
    public static final Integer ABILITY_TYPE_SELECT = 3; //多选类
    public static final Integer ABILITY_TYPE_NUMBER = 4; //阈值类
    public static final Integer ABILITY_TYPE_SELECT_NUMBER = 5; //阈值选项类

    public static  final Integer WXDEVICEID_STATUS_YES = 1; // 已占用
    public static  final Integer WXDEVICEID_STATUS_NO = 0; // 未占用

    public static  final Integer DEFAULT_TEAM_ID = -1; // 默认组ID

    public static  final Integer WXDEVICEID_DEF_COUNT = 2; // 设备id池 默认增加配额。
    public static  final Integer WXDEVICEID_MAX_COUNT = 200; // 设备id池 增加配额最大限制。

    public static  final Integer HAS_TEAM_NO = -1; // -1 该用户不存在设设备组


    /*型号的版式配置项是否显示*/
    public static final Integer DEVICE_MODEL_FORMAT_ITEM_SHOW_YES = 1; // 是
    public static final Integer DEVICE_MODEL_FORMAT_ITEM_SHOW_NO = 0; //否

    public static final Integer DEVICE_MODEL_ABILITY_UPDATE_MINUS = -1;//减去
    public static final Integer DEVICE_MODEL_ABILITY_UPDATE_NORMAL = 0;//正常
    public static final Integer DEVICE_MODEL_ABILITY_UPDATE_ADD = 1;//增加
    public static final Integer DEVICE_MODEL_ABILITY_UPDATE_DISABIE = 3;//禁用

    public static final int DEVICE_UNI_NO_TYPE = 1;//type
    public static final int DEVICE_UNI_NO_MODEl = 2;//model
    public static final int DEVICE_UNI_NO_DEVICE = 3;//device

    public static final Integer DEVICE_OPERATE_SYS_H5 = 1;//h5端
    public static final Integer DEVICE_OPERATE_SYS_ANDROID = 2;//安卓端
    public static final Integer DEVICE_OPERATE_SYS_BACKEND = 3;//后台端

}
