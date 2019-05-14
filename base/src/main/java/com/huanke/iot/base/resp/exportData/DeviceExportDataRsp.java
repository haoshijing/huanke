package com.huanke.iot.base.resp.exportData;

import com.huanke.iot.base.po.data.DeviceExportDataPo;
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
public class DeviceExportDataRsp {
    List<DeviceExportDataPo> macList;
    private Integer currentPage;
    private Integer currentCount;
    private Integer totalCount;

}
