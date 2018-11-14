package com.huanke.iot.base.request;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 基本列表请求类
 *
 * @author onlymark
 * @create 2018-11-14 下午12:33
 */
@Data
public class BaseListRequest<T> {
    private List<T> valueList;
}
