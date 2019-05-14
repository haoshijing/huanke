package com.huanke.iot.manage.service.device.exportData;

import com.huanke.iot.base.dao.data.DeviceExportDataMapper;
import com.huanke.iot.base.po.data.DeviceExportDataPo;
import com.huanke.iot.base.request.exportData.DeviceExportDataRequest;
import com.huanke.iot.base.resp.exportData.DeviceExportDataRsp;
import com.huanke.iot.manage.vo.request.BaseListRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 描述:
 * 导出数据service
 *
 * @author onlymark
 * @create 2018-11-14 上午10:25
 */
@Slf4j
@Repository
public class DeviceExportDataService {
    @Autowired
    private DeviceExportDataMapper deviceExportDataMapper;

    /**
     * 删除dict
     *
     * @param valueList
     * @return
     */
    public Boolean delete(List<Integer> valueList) {
        Boolean result = deviceExportDataMapper.batchDelete(valueList);
        return result;
    }


    public DeviceExportDataRsp selectList(DeviceExportDataRequest request) {
        DeviceExportDataRsp deviceExportDataRsp = new DeviceExportDataRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;

        DeviceExportDataPo deviceExportDataPo = new DeviceExportDataPo();
        Integer count = deviceExportDataMapper.selectCount(deviceExportDataPo);
        deviceExportDataRsp.setTotalCount(count);
        deviceExportDataRsp.setCurrentPage(currentPage);
        deviceExportDataRsp.setCurrentCount(limit);

        List<DeviceExportDataPo> deviceExportDataPoList = deviceExportDataMapper.selectPageList(deviceExportDataPo, start, limit);
        deviceExportDataRsp.setMacList(deviceExportDataPoList);
        return deviceExportDataRsp;
    }

    public Boolean add(BaseListRequest<String> request) {
        List<String> macList = request.getValueList();
        deviceExportDataMapper.addMac(macList);
        return true;
    }
}
