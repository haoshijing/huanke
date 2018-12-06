package com.huanke.iot.base.resp.user;

import com.huanke.iot.base.po.user.UserMessage;
import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 用户消息rsp
 *
 * @author onlymark
 * @create 2018-12-06 下午1:50
 */
@Data
public class UserMessageRsp {
    List<UserMessage> userMessageList;
    private Integer currentPage;
    private Integer currentCount;
    private Integer totalCount;
}
