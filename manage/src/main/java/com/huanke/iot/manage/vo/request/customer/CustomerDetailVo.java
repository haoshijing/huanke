package com.huanke.iot.manage.vo.request.customer;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 客户类型，设备总数详细信息
 *
 * @author onlymark
 * @create 2018-11-08 下午1:53
 */
@Data
public class CustomerDetailVo {
    private List<String> typeNameList;
    private Integer deviceCount;
}
