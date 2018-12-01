package com.huanke.iot.base.request.project;

import lombok.Data;

/**
 * 描述:
 * 材料修改类
 *
 * @author onlymark
 * @create 2018-11-14 上午10:16
 */
@Data
public class MateriaUpdateRequest {
    private Integer id;
    private Integer type;
    private Integer handerNums;
}
