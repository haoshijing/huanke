package com.huanke.iot.base.request.project;

import lombok.Data;

@Data
public class CopyProjectPlanReq {
    private Integer sourceProjectId;
    private Integer currentProjectId;
}
