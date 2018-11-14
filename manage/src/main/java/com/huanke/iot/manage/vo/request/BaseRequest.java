package com.huanke.iot.manage.vo.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 描述:
 * 基本请求类
 *
 * @author onlymark
 * @create 2018-11-14 下午12:32
 */
@Data
public class BaseRequest<T> {
    @NotNull
    private T value;
}
