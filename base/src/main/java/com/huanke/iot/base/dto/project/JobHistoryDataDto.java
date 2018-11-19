package com.huanke.iot.base.dto.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 任务操作流程dto
 *
 * @author onlymark
 * @create 2018-11-19 下午2:04
 */
@Data
public class JobHistoryDataDto {
    private Date createTime;
    private String userName;
    private Integer operateType;
    private String description;
    private String imgListStr;
}
