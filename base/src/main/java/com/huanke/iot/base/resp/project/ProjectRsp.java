package com.huanke.iot.base.resp.project;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-11-16 上午9:41
 */
@Data
public class ProjectRsp {
    List<ProjectRspPo> projectRspPoList;
    private Integer currentPage;
    private Integer currentCount;
    private Integer totalCount;
}
