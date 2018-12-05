package com.huanke.iot.base.dao;

import com.huanke.iot.base.po.config.DictPo;
import com.huanke.iot.base.resp.DictRspPo;
import com.huanke.iot.base.resp.QueryDictRsp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-14 下午1:54
 */
public interface DictMapper extends BaseMapper<DictPo> {
    /**
     * 不要用
     * @param type
     * @return
     */
    List<DictPo> selectByType(String type);

    Boolean batchDisable(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    Boolean batchEnable(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    Boolean batchDelete(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    List<DictRspPo> selectPageList(@Param("dictPo") DictPo dictPo, @Param("start") int start, @Param("limit") int limit);

    List<QueryDictRsp> queryDict(@Param("customerId") Integer customerId, @Param("type") String type);

    Integer confirmAdd(DictPo dictPo);

    Integer confirmUpdate(DictPo dictPo);
}
