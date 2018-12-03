package com.huanke.iot.base.request.project;

import lombok.Data;

import java.util.Date;

/**
 * 描述:
 * 字典请求类
 *
 * @author onlymark
 * @create 2018-11-14 上午10:16
 */
@Data
public class ProjectQueryRequest {
    private String projectNo;
    private String name;
    private String buildAddress;
    private Date createTime;
    private Integer currentPage = 1;
    private Integer limit = 10;

}
