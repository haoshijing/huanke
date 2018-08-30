package com.huanke.iot.api.service.device.format;

import com.huanke.iot.api.controller.h5.response.DeviceFormatVo;
import com.huanke.iot.base.dao.device.DeviceFormatItemMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.format.WxFormatItemPo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author onlymark
 * @version 2018年08月29日
 **/
@Repository
@Slf4j
public class DeviceFormatService {

    @Autowired
    private DeviceMapper deviceMapper;
    @Autowired
    private DeviceFormatItemMapper deviceFormatItemMapper;

    public DeviceFormatVo queryFormatByDeviceId(String deviceId) throws Exception {
        DeviceFormatVo deviceFormatVo = new DeviceFormatVo();
        List<DeviceFormatVo.formatItem> formatItems = deviceFormatVo.getFormatItems();
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
        Integer modelId = devicePo.getModelId();
        if (modelId == null) {
            log.error("modelId is null, deviceId={}", deviceId);
            throw new Exception("modelId is null");
        }
        List<Integer> formatItemIds = deviceFormatItemMapper.getItemIdsByModelId(modelId);
        if (formatItemIds.isEmpty()) {
            log.error("此modelId无任何对应版式项,modelId={}", modelId);
            throw new Exception("此modelId无任何对应版式项");
        }
        for (Integer formatItemId : formatItemIds) {
            DeviceFormatVo.formatItem formatItem = new DeviceFormatVo.formatItem();
            WxFormatItemPo wxFormatItemPo = deviceFormatItemMapper.selectById(formatItemId);
            BeanUtils.copyProperties(wxFormatItemPo, formatItem);
            formatItems.add(formatItem);
        }
        return deviceFormatVo;
    }
}
