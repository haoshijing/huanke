package com.huanke.iot.api.service.device.format;

import com.huanke.iot.api.controller.h5.response.DeviceModelVo;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityOptionMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceTypeAblitysMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAblityMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAblityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.format.*;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.alibity.DeviceAblityOptionPo;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.base.po.device.alibity.DeviceTypeAblitysPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAblityOptionPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAblityPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelPo;
import com.huanke.iot.base.po.format.DeviceModelFormatItemPo;
import com.huanke.iot.base.po.format.DeviceModelFormatPo;
import com.huanke.iot.base.po.format.WxFormatItemPo;
import com.huanke.iot.base.po.format.WxFormatPagePo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
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
    private WxFormatItemMapper wxFormatItemMapper;
    @Autowired
    private DeviceModelMapper deviceModelMapper;
    @Autowired
    private DeviceModelFormatMapper deviceModelFormatMapper;
    @Autowired
    private DeviceModelFormatItemMapper deviceModelFormatItemMapper;
    @Autowired
    private DeviceAblityMapper deviceAblityMapper;
    @Autowired
    private DeviceAblityOptionMapper deviceAblityOptionMapper;
    @Autowired
    private DeviceModelAblityMapper deviceModelAblityMapper;
    @Autowired
    private DeviceModelAblityOptionMapper deviceModelAblityOptionMapper;
    @Autowired
    private DeviceTypeAblitysMapper deviceTypeAblitysMapper;
    @Autowired
    private WxFormatPageMapper wxFormatPageMapper;


    public DeviceModelVo getModelVo(Integer deviceId, Integer pageNo) {
        DeviceModelVo deviceModelVo = new DeviceModelVo();
        DevicePo devicePo = deviceMapper.selectById(deviceId);
        Integer modelId = devicePo.getModelId();
        DeviceModelPo deviceModelPo = deviceModelMapper.selectById(modelId);
        Integer typeId = deviceModelPo.getTypeId();
        Integer formatId = deviceModelMapper.getFormatIdById(modelId);
        deviceModelVo.setFormatId(formatId);
        deviceModelVo.setModelId(modelId);
        DeviceModelFormatPo deviceModelFormatPo = deviceModelFormatMapper.selectByJoinId(modelId, formatId, pageNo);
        Integer modelFormatId = deviceModelFormatPo.getId();
        deviceModelVo.setFormatShowName(deviceModelFormatPo.getShowName());
        //查型号版式配置项
        List<DeviceModelVo.FormatItems> formatItemsList = new ArrayList<>();
        WxFormatPagePo wxFormatPagePo = wxFormatPageMapper.selectByJoinId(formatId, pageNo);
        deviceModelVo.setPageName(wxFormatPagePo.getName());
        List<WxFormatItemPo> wxFormatItemPos = wxFormatItemMapper.selectByJoinId(formatId, wxFormatPagePo.getId());
        for (WxFormatItemPo wxFormatItemPo : wxFormatItemPos) {
            DeviceModelVo.FormatItems formatItems = new DeviceModelVo.FormatItems();
            DeviceModelFormatItemPo deviceModelFormatItemPo = deviceModelFormatItemMapper.selectByJoinId(modelFormatId, wxFormatItemPo.getId());
            formatItems.setItemId(wxFormatItemPo.getId());
            formatItems.setShowName(deviceModelFormatItemPo.getShowName());
            formatItems.setShowStatus(deviceModelFormatItemPo.getShowStatus());
            formatItems.setAbilityId(deviceModelFormatItemPo.getAblityId());
            formatItemsList.add(formatItems);
        }
        deviceModelVo.setFormatItemsList(formatItemsList);
        //查型号硬件功能项
        List<DeviceModelVo.Abilitys> abilitysList = new ArrayList<>();
        List<DeviceTypeAblitysPo> deviceTypeAblitysPos = deviceTypeAblitysMapper.selectByTypeId(typeId);
        for (DeviceTypeAblitysPo deviceTypeAblitysPo : deviceTypeAblitysPos) {
            Integer abilityId = deviceTypeAblitysPo.getAblityId();

            DeviceModelVo.Abilitys abilitys = new DeviceModelVo.Abilitys();
            abilitys.setAbilityId(abilityId);
            DeviceAblityPo deviceAblityPo = deviceAblityMapper.selectById(abilityId);
            abilitys.setAbilityName(deviceAblityPo.getAblityName());
            DeviceModelAblityPo deviceModelAblityPo = deviceModelAblityMapper.getByJoinId(modelId, abilityId);
            if(deviceModelAblityPo != null){
                abilitys.setDefinedName(deviceModelAblityPo.getDefinedName());
            }
            BeanUtils.copyProperties(deviceAblityPo, abilitys);
            List<DeviceModelVo.AbilityOption> abilityOptionList = new ArrayList<>();

            List<DeviceAblityOptionPo> deviceAblityOptionPos = deviceAblityOptionMapper.selectOptionsByAblityId(deviceAblityPo.getId());
            //查功能项选项及别名
            for (DeviceAblityOptionPo deviceAblityOptionPo : deviceAblityOptionPos) {
                DeviceModelVo.AbilityOption abilityOption = new DeviceModelVo.AbilityOption();
                abilityOption.setOptionName(deviceAblityOptionPo.getOptionName());
                abilityOption.setOptionValue(deviceAblityOptionPo.getOptionValue());
                abilityOption.setMaxVal(deviceAblityOptionPo.getMaxVal());
                abilityOption.setMinVal(deviceAblityOptionPo.getMinVal());
                DeviceModelAblityOptionPo deviceModelAblityOptionPo = deviceModelAblityOptionMapper.getByJoinId(deviceModelAblityPo.getId(), deviceAblityOptionPo.getId());
                if(deviceModelAblityOptionPo != null){
                    abilityOption.setOptionDefinedName(deviceModelAblityOptionPo.getDefinedName());
                    abilityOption.setMaxVal(deviceModelAblityOptionPo.getMaxVal());
                    abilityOption.setMinVal(deviceModelAblityOptionPo.getMinVal());
                }
                abilityOptionList.add(abilityOption);
            }
            abilitys.setAbilityOptionList(abilityOptionList);
            abilitysList.add(abilitys);
        }
        deviceModelVo.setAbilitysList(abilitysList);
        return deviceModelVo;
    }
}
