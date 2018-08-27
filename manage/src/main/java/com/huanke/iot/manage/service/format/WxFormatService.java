package com.huanke.iot.manage.service.format;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityMapper;
import com.huanke.iot.base.dao.device.ablity.DeviceAblityOptionMapper;
import com.huanke.iot.base.dao.format.WxFormatItemMapper;
import com.huanke.iot.base.dao.format.WxFormatMapper;
import com.huanke.iot.base.po.device.alibity.DeviceAblityOptionPo;
import com.huanke.iot.base.po.device.alibity.DeviceAblityPo;
import com.huanke.iot.base.po.format.WxFormatItemPo;
import com.huanke.iot.base.po.format.WxFormatPo;
import com.huanke.iot.manage.vo.request.WxFormatQueryRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityOptionCreateOrUpdateRequest;
import com.huanke.iot.manage.vo.request.device.ablity.DeviceAblityQueryRequest;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityOptionVo;
import com.huanke.iot.manage.vo.response.device.ablity.DeviceAblityVo;
import com.huanke.iot.manage.vo.response.format.WxFormatVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class WxFormatService {

    @Autowired
    private WxFormatMapper wxFormatMapper;

    @Autowired
    private WxFormatItemMapper wxFormatItemMapper;


    /**
     * 新增 版式以及版式的 配置项
     *
     * @param wxFormatVo
     * @return
     */
    public ApiResponse<Integer> createOrUpdate(WxFormatVo wxFormatVo) {

        int effectCount = 0;
        Boolean ret = false;
        WxFormatPo wxFormatPo = new WxFormatPo();
        BeanUtils.copyProperties(wxFormatVo, wxFormatPo);
        //如果有id则为更新 否则为新增
        if (wxFormatVo.getId() != null && wxFormatVo.getId() > 0) {
            wxFormatPo.setLastUpdateTime(System.currentTimeMillis());
            ret = wxFormatMapper.updateById(wxFormatPo) > 0;
        } else {
            wxFormatPo.setCreateTime(System.currentTimeMillis());
            ret = wxFormatMapper.insert(wxFormatPo) > 0;
        }
        //判断 该版式里的配置项是否为空，若不为空则进行保存
        if (wxFormatVo.getWxFormatItemVos() != null && wxFormatVo.getWxFormatItemVos().size() > 0) {

            wxFormatVo.getWxFormatItemVos().stream().forEach(wxFormatItemVo -> {
                WxFormatItemPo wxFormatItemPo = new WxFormatItemPo();
                BeanUtils.copyProperties(wxFormatItemVo, wxFormatItemPo);
                //如果是有id，则该配置项为更新，否则为新增保存
                if (wxFormatItemVo.getId() != null && wxFormatItemVo.getId() > 0) {
                    //如果当前 状态 =删除的时候 才会删除，否则 全部为正常状态
                    if (CommonConstant.STATUS_DEL.equals(wxFormatItemVo.getStatus())) {
                        wxFormatItemPo.setStatus(CommonConstant.STATUS_DEL);
                    } else {
                        wxFormatItemPo.setStatus(CommonConstant.STATUS_YES);
                    }
                    wxFormatItemPo.setLastUpdateTime(System.currentTimeMillis());
                    wxFormatItemMapper.updateById(wxFormatItemPo);
                } else {
                    wxFormatItemPo.setCreateTime(System.currentTimeMillis());
                    wxFormatItemPo.setStatus(CommonConstant.STATUS_YES);
                    wxFormatItemMapper.insert(wxFormatItemPo);
                }

            });

        }

        return new ApiResponse<>(wxFormatPo.getId());
    }

    /**
     * 查询版式列表
     *
     * @param request
     * @return
     */
    public List<WxFormatVo> selectList(WxFormatQueryRequest request) {

        WxFormatPo queryWxFormatPo = new WxFormatPo();

        if (request != null) {
            queryWxFormatPo.setId(request.getId());
            queryWxFormatPo.setName(request.getName());
            queryWxFormatPo.setOwerType(request.getOwerType());
            queryWxFormatPo.setType(request.getType());
            queryWxFormatPo.setStatus(request.getStatus());
        }
        Integer offset = (request.getPage() - 1) * request.getLimit();
        Integer limit = request.getLimit();

        //查询 版式列表
        List<WxFormatPo> wxformatPoList = wxFormatMapper.selectList(queryWxFormatPo, limit, offset);
        List<WxFormatVo> wxFormatVoList = wxformatPoList.stream().map(wxFormatPo -> {
            WxFormatVo wxFormatVo = new WxFormatVo();
            BeanUtils.copyProperties(wxFormatPo, wxFormatVo);



            return wxFormatVo;
        }).collect(Collectors.toList());

        return wxFormatVoList;
    }

//
//    /**
//     * 根据主键查询 功能
//     *
//     * @param typeId
//     * @return
//     */
//    public DeviceAblityVo selectById(Integer typeId) {
//
//        DeviceAblityPo deviceAblityPo = deviceAblityMapper.selectById(typeId);
//
//        DeviceAblityVo deviceAblityVo = new DeviceAblityVo();
//        if (deviceAblityPo != null) {
//            deviceAblityVo.setAblityName(deviceAblityPo.getAblityName());
//            deviceAblityVo.setDirValue(deviceAblityPo.getDirValue());
//            deviceAblityVo.setWriteStatus(deviceAblityPo.getWriteStatus());
//            deviceAblityVo.setReadStatus(deviceAblityPo.getReadStatus());
//            deviceAblityVo.setRunStatus(deviceAblityPo.getRunStatus());
//            deviceAblityVo.setConfigType(deviceAblityPo.getConfigType());
//            deviceAblityVo.setAblityType(deviceAblityPo.getAblityType());
//            deviceAblityVo.setRemark(deviceAblityPo.getRemark());
//            deviceAblityVo.setId(deviceAblityPo.getId());
//
//            //根据功能主键 查询该功能下的 选项列表
//            List<DeviceAblityOptionPo> deviceAblityOptionpos = deviceAblityOptionMapper.selectOptionsByAblityId(deviceAblityPo.getId());
//            List<DeviceAblityOptionVo> deviceAblityOptionVos = deviceAblityOptionpos.stream().map(deviceAblityOptionPo -> {
//                DeviceAblityOptionVo deviceAblityOptionVo = new DeviceAblityOptionVo();
//                deviceAblityOptionVo.setOptionValue(deviceAblityOptionPo.getOptionValue());
//                deviceAblityOptionVo.setOptionName(deviceAblityOptionPo.getOptionName());
//                deviceAblityOptionVo.setId(deviceAblityOptionPo.getId());
//                deviceAblityOptionVo.setStatus(deviceAblityOptionPo.getStatus());
//                return deviceAblityOptionVo;
//            }).collect(Collectors.toList());
//
//            deviceAblityVo.setDeviceAblityOptions(deviceAblityOptionVos);
//        }
//        return deviceAblityVo;
//    }
//
//    /**
//     * 删除 该功能
//     * 并同时删除该功能下 所有的选项
//     *
//     * @param ablityId
//     * @return
//     */
//    public Boolean deleteAblity(Integer ablityId) {
//
//        Boolean ret = false;
//        //先删除 该 功能
//        ret = deviceAblityMapper.deleteById(ablityId) > 0;
//        //再删除 选项表中 的选项
//        ret = ret && deviceAblityMapper.deleteOptionByAblityId(ablityId) > 0;
//        return ret;
//    }


//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
