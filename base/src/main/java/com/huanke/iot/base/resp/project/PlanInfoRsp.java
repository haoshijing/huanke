package com.huanke.iot.base.resp.project;

import com.huanke.iot.base.po.project.ProjectPlanInfo;
import lombok.Data;

/**
 * 描述:
 * 计划向西返回类
 *
 * @author onlymark
 * @create 2018-11-23 下午6:03
 */
@Data
public class PlanInfoRsp extends ProjectPlanInfo{
    private String linkDeviceName;
    private Integer linkDeviceModelId;
    private String linkDeviceModelName;
}
