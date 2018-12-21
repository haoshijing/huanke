package com.huanke.iot.manage.vo.response.device.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 描述:
 * 告警信息返回Vo
 *
 * @author onlymark
 * @create 2018-12-21 下午2:36
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WarnDataVo {
    private String name;
    private Integer num;
}
