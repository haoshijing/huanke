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
import java.util.Map;
import java.util.stream.Collectors;

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
        deviceModelVo.setManageName(devicePo.getManageName());
        WxFormatPagePo wxFormatPagePo = wxFormatPageMapper.selectByJoinId(formatId, pageNo);
        DeviceModelFormatPo deviceModelFormatPo = deviceModelFormatMapper.selectByJoinId(modelId, formatId, wxFormatPagePo.getId());
        Integer modelFormatId = deviceModelFormatPo.getId();
        deviceModelVo.setFormatShowName(deviceModelFormatPo.getShowName());
        //查型号版式配置项
        List<DeviceModelVo.FormatItems> formatItemsList = new ArrayList<>();
        deviceModelVo.setPageName(wxFormatPagePo.getName());
        List<WxFormatItemPo> wxFormatItemPos = wxFormatItemMapper.selectByJoinId(formatId, wxFormatPagePo.getId());
        //缓存
        List<DeviceModelFormatItemPo> deviceModelFormatItemPoCaches = deviceModelFormatItemMapper.obtainModelFormatItems(modelFormatId);
        Map<Integer, DeviceModelFormatItemPo> deviceModelFormatItemPoMap = deviceModelFormatItemPoCaches.stream().collect(Collectors.toMap(DeviceModelFormatItemPo::getItemId, a -> a, (k1, k2) -> k1));
        for (WxFormatItemPo wxFormatItemPo : wxFormatItemPos) {
            DeviceModelVo.FormatItems formatItems = new DeviceModelVo.FormatItems();
            DeviceModelFormatItemPo deviceModelFormatItemPo = deviceModelFormatItemPoMap.get(wxFormatItemPo.getId());
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
        //缓存功能项集
        List<DeviceAbilityPo> deviceAbilityPoCaches = deviceabilityMapper.selectList(new DeviceAbilityPo(), 10000, 0);
        Map<Integer,DeviceAbilityPo> deviceAbilityPoMap = deviceAbilityPoCaches.stream().collect(Collectors.toMap(DeviceAbilityPo::getId, a -> a,(k1, k2)->k1));
        List<DeviceModelAbilityPo> deviceModelAbilityPoCaches = deviceModelabilityMapper.selectByModelId(modelId);
        Map<Integer, DeviceModelAbilityPo> deviceModelAbilityPoMap = deviceModelAbilityPoCaches.stream().collect(Collectors.toMap(DeviceModelAbilityPo::getAbilityId, a -> a, (k1, k2) -> k1));
        for (DeviceTypeAbilitysPo deviceTypeabilitysPo : deviceTypeabilitysPos) {
            Integer abilityId = deviceTypeabilitysPo.getAbilityId();
            DeviceModelVo.Abilitys abilitys = new DeviceModelVo.Abilitys();
            abilitys.setAbilityId(abilityId);
            DeviceAbilityPo deviceabilityPo = deviceAbilityPoMap.get(abilityId);
            abilitys.setAbilityName(deviceabilityPo.getAbilityName());
            DeviceModelAbilityPo deviceModelabilityPo = deviceModelAbilityPoMap.get(abilityId);
            if(deviceModelabilityPo != null){
                abilitys.setDefinedName(deviceModelabilityPo.getDefinedName());
            }else{
                continue;
            }
            BeanUtils.copyProperties(deviceabilityPo, abilitys);
            List<DeviceModelVo.AbilityOption> abilityOptionList = new ArrayList<>();

            List<DeviceAbilityOptionPo> deviceabilityOptionPos = deviceabilityOptionMapper.selectOptionsByAbilityId(deviceabilityPo.getId());
            //查功能项选项及别名
            //缓存
            List<DeviceModelAbilityOptionPo> deviceModelAbilityOptionPoCaches = deviceModelabilityOptionMapper.getOptionsByModelAbilityId(deviceModelabilityPo.getId());
            Map<Integer, DeviceModelAbilityOptionPo> deviceModelAbilityOptionPoMap = deviceModelAbilityOptionPoCaches.stream().collect(Collectors.toMap(DeviceModelAbilityOptionPo::getAbilityOptionId, a -> a, (k1, k2) -> k1));
            for (DeviceAbilityOptionPo deviceabilityOptionPo : deviceabilityOptionPos) {
                DeviceModelAbilityOptionPo deviceModelabilityOptionPo = deviceModelAbilityOptionPoMap.get(deviceabilityOptionPo.getId());
                if(deviceModelabilityOptionPo != null){
                    DeviceModelVo.AbilityOption abilityOption = new DeviceModelVo.AbilityOption();
                    abilityOption.setOptionName(deviceabilityOptionPo.getOptionName());
                    abilityOption.setOptionValue(deviceabilityOptionPo.getOptionValue());
                    abilityOption.setMaxVal(deviceabilityOptionPo.getMaxVal());
                    abilityOption.setMinVal(deviceabilityOptionPo.getMinVal());
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
