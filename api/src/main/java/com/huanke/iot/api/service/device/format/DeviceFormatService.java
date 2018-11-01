package com.huanke.iot.api.service.device.format;

import com.huanke.iot.api.controller.h5.response.DeviceModelVo;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityMapper;
import com.huanke.iot.base.dao.device.ability.DeviceAbilityOptionMapper;
import com.huanke.iot.base.dao.device.ability.DeviceTypeAbilitysMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelAbilityOptionMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceModelMapper;
import com.huanke.iot.base.dao.format.DeviceModelFormatItemMapper;
import com.huanke.iot.base.dao.format.DeviceModelFormatMapper;
import com.huanke.iot.base.dao.format.WxFormatItemMapper;
import com.huanke.iot.base.dao.format.WxFormatPageMapper;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityOptionPo;
import com.huanke.iot.base.po.device.ability.DeviceAbilityPo;
import com.huanke.iot.base.po.device.ability.DeviceTypeAbilitysPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityOptionPo;
import com.huanke.iot.base.po.device.typeModel.DeviceModelAbilityPo;
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
    private DeviceAbilityMapper deviceabilityMapper;
    @Autowired
    private DeviceAbilityOptionMapper deviceabilityOptionMapper;
    @Autowired
    private DeviceModelAbilityMapper deviceModelabilityMapper;
    @Autowired
    private DeviceModelAbilityOptionMapper deviceModelabilityOptionMapper;
    @Autowired
    private DeviceTypeAbilitysMapper deviceTypeabilitysMapper;
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
        WxFormatPagePo wxFormatPagePo = wxFormatPageMapper.selectByJoinId(formatId, pageNo);
        DeviceModelFormatPo deviceModelFormatPo = deviceModelFormatMapper.selectByJoinId(modelId, formatId, wxFormatPagePo.getId());
        Integer modelFormatId = deviceModelFormatPo.getId();
        deviceModelVo.setFormatShowName(deviceModelFormatPo.getShowName());
        //查型号版式配置项
        List<DeviceModelVo.FormatItems> formatItemsList = new ArrayList<>();
        deviceModelVo.setPageName(wxFormatPagePo.getName());
        List<WxFormatItemPo> wxFormatItemPos = wxFormatItemMapper.selectByJoinId(formatId, wxFormatPagePo.getId());
        for (WxFormatItemPo wxFormatItemPo : wxFormatItemPos) {
            DeviceModelVo.FormatItems formatItems = new DeviceModelVo.FormatItems();
            DeviceModelFormatItemPo deviceModelFormatItemPo = deviceModelFormatItemMapper.selectByJoinId(modelFormatId, wxFormatItemPo.getId());
            formatItems.setItemId(wxFormatItemPo.getId());
            formatItems.setShowName(deviceModelFormatItemPo.getShowName());
            formatItems.setShowStatus(deviceModelFormatItemPo.getShowStatus());
            formatItems.setAbilityId(deviceModelFormatItemPo.getAbilityId());
            formatItemsList.add(formatItems);
        }
        deviceModelVo.setFormatItemsList(formatItemsList);
        //查型号硬件功能项
        List<DeviceModelVo.Abilitys> abilitysList = new ArrayList<>();
        List<DeviceTypeAbilitysPo> deviceTypeabilitysPos = deviceTypeabilitysMapper.selectByTypeId(typeId);
        for (DeviceTypeAbilitysPo deviceTypeabilitysPo : deviceTypeabilitysPos) {
            Integer abilityId = deviceTypeabilitysPo.getAbilityId();

            DeviceModelVo.Abilitys abilitys = new DeviceModelVo.Abilitys();
            abilitys.setAbilityId(abilityId);
            DeviceAbilityPo deviceabilityPo = deviceabilityMapper.selectById(abilityId);
            abilitys.setAbilityName(deviceabilityPo.getAbilityName());
            DeviceModelAbilityPo deviceModelabilityPo = deviceModelabilityMapper.getByJoinId(modelId, abilityId);
            if(deviceModelabilityPo != null){
                abilitys.setDefinedName(deviceModelabilityPo.getDefinedName());
            }else{
                continue;
            }
            BeanUtils.copyProperties(deviceabilityPo, abilitys);
            List<DeviceModelVo.AbilityOption> abilityOptionList = new ArrayList<>();

            List<DeviceAbilityOptionPo> deviceabilityOptionPos = deviceabilityOptionMapper.selectOptionsByAbilityId(deviceabilityPo.getId());
            //查功能项选项及别名
            for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos) {
                DeviceModelVo.AbilityOption abilityOption = new DeviceModelVo.AbilityOption();
                abilityOption.setOptionName(deviceabilityOptionPo.getOptionName());
                abilityOption.setOptionValue(deviceabilityOptionPo.getOptionValue());
                abilityOption.setMaxVal(deviceabilityOptionPo.getMaxVal());
                abilityOption.setMinVal(deviceabilityOptionPo.getMinVal());
                DeviceModelAbilityOptionPo deviceModelabilityOptionPo = deviceModelabilityOptionMapper.getByJoinId(deviceModelabilityPo.getId(), deviceabilityOptionPo.getId());
                if(deviceModelabilityOptionPo != null){
                    abilityOption.setOptionDefinedName(deviceModelabilityOptionPo.getDefinedName());
                    abilityOption.setDefaultValue(deviceModelabilityOptionPo.getDefaultValue());
                    abilityOption.setMaxVal(deviceModelabilityOptionPo.getMaxVal());
                    abilityOption.setMinVal(deviceModelabilityOptionPo.getMinVal());
                    abilityOption.setStatus(deviceModelabilityOptionPo.getStatus());
                    abilityOptionList.add(abilityOption);
                }
            }
            abilitys.setAbilityOptionList(abilityOptionList);
            abilitysList.add(abilitys);
        }
        deviceModelVo.setAbilitysList(abilitysList);
        return deviceModelVo;
    }
}
