package com.huanke.iot.base.dao;

import java.util.List;

public interface BaseMapper<T> {
    int insert(T bean);

    T selectById(Integer id);

    List<T> selectList(T queryBean, int limit , int offset);

    Integer selectCount(T queryBean);
}
