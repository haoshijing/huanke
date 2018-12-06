package com.huanke.iot.base.request.user;

import lombok.Data;

/**
 * 描述:用户消息req
 *
 * @author onlymark
 * @create 2018-12-06 下午12:54
 */
@Data
public class UserMessageRequest {
    private Integer id;
    private String topic;
    private String description;
}
