package com.huanke.iot.base.dao;

public interface BaseMapper<T> {
    int insert(T bean);

    T selectById(Integer id);
}
