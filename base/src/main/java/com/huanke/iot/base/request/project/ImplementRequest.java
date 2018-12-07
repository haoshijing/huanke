package com.huanke.iot.base.request.project;

import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 实施request
 *
 * @author onlymark
 * @create 2018-11-20 下午6:13
 */
@Data
public class ImplementRequest {
    private Integer id;
    private Integer projectId;
    private Integer typeId;
    private String description;
    private Date implTime;
    private List<String> imgList;
    private Map<String, List<String>> fileMap;

}
