package com.huanke.iot.base.constant;

/**
 * @author Caik
 * @date 2018/8/14 18:07
 */
public class DeviceConstant {

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
    public static final Integer ABLITY_TYPE_TEXT = 1; //文本类
    public static final Integer ABLITY_TYPE_RADIO = 2; //单选类
    public static final Integer ABLITY_TYPE_SELECT = 3; //多选类
    public static final Integer ABLITY_TYPE_NUMBER = 4; //阈值类
    public static final Integer ABLITY_TYPE_SELECT_NUMBER = 5; //阈值选项类

    public static  final Integer WXDEVICEID_STATUS_YES = 1; // 已占用
    public static  final Integer WXDEVICEID_STATUS_NO = 0; // 未占用

    public static  final Integer DEFAULT_TEAM_ID = -1; // 已占用

    public static  final Integer WXDEVICEID_DEF_COUNT = 200; // 设备id池 默认增加配额。
    public static  final Integer WXDEVICEID_MAX_COUNT = 2000; // 设备id池 增加配额最大限制。


}
