package com.huanke.iot.base.resp.project;

import com.huanke.iot.base.po.project.ProjectJobInfo;
import lombok.Data;

import java.util.List;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-12-05 上午7:19
 */
@Data
public class WarnJobRsp {
    List<JobRspPo> projectJobInfoList;
    private Integer currentPage;
    private Integer currentCount;
    private Integer totalCount;
}
