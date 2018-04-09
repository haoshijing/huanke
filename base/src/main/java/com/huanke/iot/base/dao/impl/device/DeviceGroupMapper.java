package com.huanke.iot.base.dao.impl.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.DeviceGroupItemPo;
import com.huanke.iot.base.po.device.DeviceGroupPo;

import java.util.List;

/**
 * @author haoshijing
 * @version 2018年04月08日 10:20
 **/
public interface DeviceGroupMapper extends BaseMapper<DeviceGroupPo> {

    /**
     *
     * @param groupItemPoList
     * @return
     */
    int insertGroupItem(List<DeviceGroupItemPo> groupItemPoList);

    void bactchInsertGroupItem(List<DeviceGroupItemPo> deviceGroupItemPoList);

    Integer queryItemCount(DeviceGroupItemPo queryItemPo);
}
