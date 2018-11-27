package com.huanke.iot.base.resp.project;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 描述:
 * 工程实施rsp
 *
 * @author onlymark
 * @create 2018-11-21 上午7:48
 */
@Data
public class ImplementRsp {
    private Integer projectId;
    private String typeName;
    private String description;
    private String implTime;
    private String imgListStr;
    private List<String> imgList;
    private String fileList;
    private Map<Integer, List<String>> fileMap;
}
