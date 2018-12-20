package com.huanke.iot.base.resp.project;

import lombok.Data;

@Data
public class ProjectPlanCount {
    private Integer id;
    private String name;
    private String projectNo;
    private Integer count;//周期维保个数
}
