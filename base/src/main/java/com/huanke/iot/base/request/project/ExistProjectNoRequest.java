package com.huanke.iot.base.request.project;

import com.huanke.iot.base.request.BaseRequest;
import lombok.Data;

/**
 * 描述:
 * 检查是否存在projectNo的工程项
 *
 * @author onlymark
 * @create 2018-11-29 下午2:29
 */
@Data
public class ExistProjectNoRequest extends BaseRequest<String> {
    private Integer projectId;
}
