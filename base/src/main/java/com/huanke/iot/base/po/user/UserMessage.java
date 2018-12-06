package com.huanke.iot.base.po.user;

import lombok.Data;

import java.util.Date;

/**
 * 描述:发送消息
 *
 * @author onlymark
 * @create 2018-12-06 上午10:57
 */
@Data
public class UserMessage {
    private Integer id;
    private String topic;
    private String description;
    private Integer status;
    private Date createTime;
    private Date updateTime;
    private Integer createUser;
    private Integer updateUser;
}