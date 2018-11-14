package com.huanke.iot.base.dao;

import com.huanke.iot.base.po.config.DictPo;
import com.huanke.iot.base.resp.DictRspPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-14 下午1:54
 */
public interface DictMapper extends BaseMapper<DictPo> {
    List<DictPo> selectByType(String type);

    Boolean batchDelete(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    List<DictRspPo> selectPageList(@Param("dictPo") DictPo dictPo, @Param("start") int start, @Param("limit") int limit);
}
