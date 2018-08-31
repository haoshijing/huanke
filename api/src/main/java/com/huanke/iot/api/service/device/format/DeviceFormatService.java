package com.huanke.iot.api.service.device.format;

import com.huanke.iot.api.controller.h5.response.ItemAbilitysVo;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAblityMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAblityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.format.DeviceModelFormatConfigMapper;
import com.huanke.iot.base.dao.format.WxFormatItemMapper;
import com.huanke.iot.base.dao.format.WxFormatMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.alibity.DeviceAblityOptionPo;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAblityOptionPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAblityPo;
import com.huanke.iot.base.po.format.WxFormatItemPo;
import com.huanke.iot.base.po.format.WxFormatPo;
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
    private WxFormatMapper wxFormatMapper;
    @Autowired
    private WxFormatItemMapper wxFormatItemMapper;
    @Autowired
    private DeviceModelMapper deviceModelMapper;
    @Autowired
    private DeviceModelFormatConfigMapper deviceModelFormatConfigMapper;
    @Autowired
    private DeviceAblityMapper deviceAblityMapper;
    @Autowired
    private DeviceAblityOptionMapper deviceAblityOptionMapper;
    @Autowired
    private DeviceModelAblityMapper deviceModelAblityMapper;
    @Autowired
    private DeviceModelAblityOptionMapper deviceModelAblityOptionMapper;

    public ItemAbilitysVo getItemAbilitys(String deviceId, Integer pageId) {
        ItemAbilitysVo itemAbilitysVo = new ItemAbilitysVo();
        DevicePo devicePo = deviceMapper.selectByDeviceId(deviceId);
        Integer modelId = devicePo.getModelId();
        Integer formatId = deviceModelMapper.getFormatIdById(modelId);
        itemAbilitysVo.setFormatId(formatId);

        WxFormatItemPo wxFormatItemPo = wxFormatItemMapper.selectByJoinId(formatId, pageId);
        WxFormatPo wxFormatPo = wxFormatMapper.selectById(formatId);
        itemAbilitysVo.setItemName(wxFormatPo.getName());
        Integer itemId = wxFormatItemPo.getId();
        log.info("查功能项Ids, modelId={},formatId={},pageId={},itemId={}", modelId, formatId, pageId, itemId);
        List<Integer> abilityIds = deviceModelFormatConfigMapper.obtainAbilityIdsByJoinId(modelId, formatId, pageId, itemId);
        //遍历查功能项
        for (Integer abilityId : abilityIds) {
            ItemAbilitysVo.Abilitys abilitys = new ItemAbilitysVo.Abilitys();
            DeviceAblityPo deviceAblityPo = deviceAblityMapper.selectById(abilityId);
            DeviceModelAblityPo deviceModelAblityPo = deviceModelAblityMapper.getByJoinId(modelId, abilityId);
            abilitys.setDefinedName(deviceModelAblityPo.getDefinedName());
            BeanUtils.copyProperties(deviceAblityPo, abilitys);
            List<ItemAbilitysVo.AbilityOption> abilityOptionList = abilitys.getAbilityOptionList();
            List<DeviceAblityOptionPo> deviceAblityOptionPos = deviceAblityOptionMapper.selectOptionsByAblityId(deviceAblityPo.getId());
            //查功能项选项及别名
            for (DeviceAblityOptionPo deviceAblityOptionPo : deviceAblityOptionPos) {
                ItemAbilitysVo.AbilityOption abilityOption = new ItemAbilitysVo.AbilityOption();
                abilityOption.setOptionName(deviceAblityOptionPo.getOptionName());
                abilityOption.setOptionValue(deviceAblityOptionPo.getOptionValue());
                DeviceModelAblityOptionPo deviceModelAblityOptionPo = deviceModelAblityOptionMapper.getByJoinId(deviceModelAblityPo.getId(), deviceAblityOptionPo.getId());
                abilityOption.setOptionDefinedName(deviceModelAblityOptionPo.getDefinedName());
                abilityOptionList.add(abilityOption);
            }
            itemAbilitysVo.getAbilitysList().add(abilitys);
        }
        return itemAbilitysVo;
    }

}
