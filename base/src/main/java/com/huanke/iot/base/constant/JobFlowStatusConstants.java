package com.huanke.iot.base.constant;

/**
 * 描述:
 * 任务状态流转常量
 *
 * @author onlymark
 * @create 2018-11-19 上午9:08
 */
public class JobFlowStatusConstants {
    //具体操作流程
    /**
     * 生成
     */
    public static final int OPERATE_TYPE_CREATE= 1;
    /**
     * 分配
     */
    public static final int OPERATE_TYPE_ALLOT= 2;
    /**
     * 处理完成提交审核
     */
    public static final int OPERATE_TYPE_COMMIT= 3;
    /**
     * 通过
     */
    public static final int OPERATE_TYPE_PASS= 4;
    /**
     * 驳回
     */
    public static final int OPERATE_TYPE_BACK= 5;
    /**
     * 忽略
     */
    public static final int OPERATE_TYPE_IGNORE= 6;

    //任务流程状态标志
    /**
     * 已生成待分配
     */
    public static final int FLOW_STATUS_CREATED= 1;
    /**
     * 已分配待处理
     */
    public static final int FLOW_STATUS_DEALING= 2;
    /**
     * 已处理待审核
     */
    public static final int FLOW_STATUS_COMMITED= 3;
    /**
     * 审核通过已完成
     */
    public static final int FLOW_STATUS_FINISH= 4;
    /**
     * 已忽略
     */
    public static final int FLOW_STATUS_IGNORED= 5;



}
