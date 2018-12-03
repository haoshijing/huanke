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
    private Integer type;   //1-创建；2-修改；3-追加；4-减少；5-维保使用
    private Integer handerNums;
}
