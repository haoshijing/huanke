package com.huanke.iot.base.request.project;

import com.huanke.iot.base.request.BaseListRequest;
import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 任务修改状态req
 *
 * @author onlymark
 * @create 2018-11-19 上午8:06
 */
@Data
public class JobFlowStatusRequest extends BaseListRequest<Integer> {
    private Integer jobId;
    private String description;
    private List<String> imgList;//图册
    private Integer operateType;//1-生成；2-处理；3-提交审核；4-通过；5-驳回；6-忽略
    private List<Integer> targetUsers;
    /**
     * 使用材料信息
     */
    private List<MateriaUpdateRequest> materiaUpdateRequestList;
}
