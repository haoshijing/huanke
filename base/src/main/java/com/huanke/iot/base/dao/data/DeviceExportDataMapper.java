package com.huanke.iot.base.dao.data;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.dto.DeviceIdMacDto;
import com.huanke.iot.base.po.data.DeviceExportDataPo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author liuxiaoyu
 * @version 2019年05月14日 10:20
 **/
public interface DeviceExportDataMapper extends BaseMapper<DeviceExportDataPo> {
    List<DeviceIdMacDto> queryExportDataDevice();

    void addMac(@Param("macList") List<String> macList);

    List<DeviceExportDataPo> selectPageList(@Param("deviceExportDataPo") DeviceExportDataPo deviceExportDataPo, @Param("start") int start, @Param("limit") int limit);

    Boolean batchDelete(@Param("valueList") List<Integer> valueList);
}
