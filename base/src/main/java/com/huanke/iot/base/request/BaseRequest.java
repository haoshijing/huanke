package com.huanke.iot.base.request;

import lombok.Data;

/**
 * 描述:
 * 基本请求类
 *
 * @author onlymark
 * @create 2018-11-14 下午12:32
 */
@Data
public class BaseRequest<T> {
    private T value;
}
