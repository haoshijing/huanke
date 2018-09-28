package com.huanke.iot.manage.vo.request.device.operate;

import lombok.Data;

import java.util.List;

@Data
public class ExcelData {
    private static final long serialVersionUID = 4444017239100620999L;
    // 表头
    private List<String> titles;

    // 数据
    private List<List<Object>> rows;

    // 页签名称
    private String name;
}
