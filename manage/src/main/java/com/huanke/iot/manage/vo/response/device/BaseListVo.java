package com.huanke.iot.manage.vo.response.device;

import lombok.Data;

import java.util.List;

/**
 * @author caikun
 * @date 2018/10/25 下午10:29
 **/
@Data
public class BaseListVo {
    private List dataList;
    private int totalCount;
}
