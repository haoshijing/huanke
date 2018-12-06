package com.huanke.iot.base.dao.user;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.user.UserMessage;
import com.huanke.iot.base.po.user.UserMessageLog;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 描述:
 *
 * @author onlymark
 * @create 2018-09-14 下午1:54
 */
public interface UserMessageMapper extends BaseMapper<UserMessage> {

    Boolean batchDelete(@Param("userId") Integer userId, @Param("valueList") List<Integer> valueList);

    List<UserMessage> selectPageList(@Param("userMessage") UserMessage userMessage, @Param("start") int start, @Param("limit") int limit);

    Integer selectLogCount(@Param("userMessageLog") UserMessageLog userMessageLog);

    List<UserMessageLog> selectLogPageList(@Param("userMessageLog") UserMessageLog userMessageLog, @Param("start") int start, @Param("limit") int limit);
}
