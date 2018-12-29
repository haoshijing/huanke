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
     * 同意生成任务
     */
    public static final int OPERATE_TYPE_ALLOW= 3;
    /**
     * 不同意生成任务
     */
    public static final int OPERATE_TYPE_REJECT= 4;
    /**
     * 处理完成提交审核
     */
    public static final int OPERATE_TYPE_COMMIT= 5;
    /**
     * 通过
     */
    public static final int OPERATE_TYPE_PASS= 6;
    /**
     * 驳回
     */
    public static final int OPERATE_TYPE_BACK= 7;
    /**
     * 忽略
     */
    public static final int OPERATE_TYPE_IGNORE= 8;

    //任务流程状态标志
    /**
     * 已生成待分配
     */
    public static final int FLOW_STATUS_CREATED= 1;
    /**
     * 已分配待审核
     */
    public static final int FLOW_STATUS_ALLOTTED= 2;
    /**
     * 已审核待处理
     */
    public static final int FLOW_STATUS_PERMIT= 3;
    /**
     * 已处理待归档
     */
    public static final int FLOW_STATUS_SUBMIT= 4;
    /**
     * 已归档完成
     */
    public static final int FLOW_STATUS_FINISH= 5;
    /**
     * 已忽略
     */
    public static final int FLOW_STATUS_IGNORED= 6;


}
