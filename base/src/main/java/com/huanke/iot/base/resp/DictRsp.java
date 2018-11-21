package com.huanke.iot.base.resp;

import lombok.Data;

import java.util.List;

/**
 * 描述:
 * 字典返回类
 *
 * @author onlymark
 * @create 2018-11-14 下午1:46
 */
@Data
public class DictRsp {
    List<DictRspPo> dictRspPoList;
    private Integer currentPage;
    private Integer currentCount;
    private Integer totalCount;

}
