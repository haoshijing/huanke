package com.huanke.iot.base.dao.device;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.device.team.DeviceTeamItemPo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;

import java.util.List;

/**
 * onlymark
 *
 * 2018/8/20
 */
public interface DeviceTeamMapper  extends BaseMapper<DeviceTeamPo> {

    List<DeviceTeamItemPo> queryTeamItems(DeviceTeamItemPo queryDeviceTeamItem);

}
