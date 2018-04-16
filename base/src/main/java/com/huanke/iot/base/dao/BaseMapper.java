package com.huanke.iot.base.dao;

import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BaseMapper<T> {
    int insert(T bean);

    T selectById(Integer id);

    int updateById(T bean);

    List<T> selectList(@Param("param") T queryBean,@Param("limit") int limit ,@Param("offset") int offset);

    Integer selectCount(@Param("param") T queryBean);
}
