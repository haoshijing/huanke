package com.huanke.iot.manage.service.format;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.RetCode;
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
                    wxFormatItemPo.setFormatId(wxFormatPo.getId());
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
            if (request.getStatus().equals(CommonConstant.STATUS_DEL)) {
                queryWxFormatPo.setStatus(CommonConstant.STATUS_DEL);
            } else {
                queryWxFormatPo.setStatus(CommonConstant.STATUS_YES);

            }
        }
        Integer offset = (request.getPage() - 1) * request.getLimit();
        Integer limit = request.getLimit();

        //查询 版式列表
        List<WxFormatPo> wxformatPoList = wxFormatMapper.selectList(queryWxFormatPo, limit, offset);
        List<WxFormatVo> wxFormatVoList = wxformatPoList.stream().map(wxFormatPo -> {
            WxFormatVo wxFormatVo = new WxFormatVo();
            BeanUtils.copyProperties(wxFormatPo, wxFormatVo);

            //根据版式主键 查询该版式下的 配置项
            List<WxFormatItemPo> wxFormatItemPos = wxFormatItemMapper.selectByFormatId(wxFormatPo.getId());

            List<WxFormatVo.WxFormatItemVo> wxFormatItemVos = wxFormatItemPos.stream().map(wxFormatItemPo -> {
                WxFormatVo.WxFormatItemVo wxFormatItemVo = new WxFormatVo.WxFormatItemVo();
                wxFormatItemVo.setName(wxFormatItemPo.getName());
                wxFormatItemVo.setId(wxFormatItemPo.getId());
                wxFormatItemVo.setStatus(wxFormatItemPo.getStatus());
                return wxFormatItemVo;
            }).collect(Collectors.toList());

            wxFormatVo.setWxFormatItemVos(wxFormatItemVos);

            return wxFormatVo;
        }).collect(Collectors.toList());

        return wxFormatVoList;
    }


    /**
     * 根据主键查询 版式
     *
     * @param formatId
     * @return
     */
    public WxFormatVo selectById(Integer formatId) {

        WxFormatPo wxFormatPo = wxFormatMapper.selectById(formatId);

        WxFormatVo wxFormatVo = new WxFormatVo();
        if (wxFormatPo != null) {
            wxFormatVo.setId(wxFormatPo.getId());
            wxFormatVo.setName(wxFormatPo.getName());
            wxFormatVo.setHtmlUrl(wxFormatPo.getHtmlUrl());
            wxFormatVo.setIcon(wxFormatPo.getIcon());
            wxFormatVo.setOwerType(wxFormatPo.getOwerType());
            wxFormatVo.setPreviewImg(wxFormatPo.getPreviewImg());
            wxFormatVo.setTypeIds(wxFormatPo.getTypeIds());
            wxFormatVo.setCustomerIds(wxFormatPo.getCustomerIds());
            wxFormatVo.setType(wxFormatPo.getType());
            wxFormatVo.setVersion(wxFormatPo.getVersion());
            wxFormatVo.setStatus(wxFormatPo.getStatus());

            //根据版式主键 查询该版式下的 配置项
            List<WxFormatItemPo> wxFormatItemPos = wxFormatItemMapper.selectByFormatId(wxFormatPo.getId());

            List<WxFormatVo.WxFormatItemVo> wxFormatItemVos = wxFormatItemPos.stream().map(wxFormatItemPo -> {
                WxFormatVo.WxFormatItemVo wxFormatItemVo = new WxFormatVo.WxFormatItemVo();
                wxFormatItemVo.setName(wxFormatItemPo.getName());
                wxFormatItemVo.setId(wxFormatItemPo.getId());
                wxFormatItemVo.setStatus(wxFormatItemPo.getStatus());
                return wxFormatItemVo;
            }).collect(Collectors.toList());

            wxFormatVo.setWxFormatItemVos(wxFormatItemVos);
        }
        return wxFormatVo;
    }

//    /**
//     * 查询客户可使用的版式列表
//     *
//     * @param customerId
//     * @return
//     */
//    public List<WxFormatVo> selectFormatsByCustomerId(Integer customerId) {
//
//        WxFormatPo queryWxFormatPo = new WxFormatPo();
//
//        if (request.getStatus().equals(CommonConstant.STATUS_DEL)) {
//            queryWxFormatPo.setStatus(CommonConstant.STATUS_DEL);
//        } else {
//            queryWxFormatPo.setStatus(CommonConstant.STATUS_YES);
//
//        }
//
//        //查询 版式列表
//        List<WxFormatPo> wxformatPoList = wxFormatMapper.selectList(queryWxFormatPo, limit, offset);
//        List<WxFormatVo> wxFormatVoList = wxformatPoList.stream().map(wxFormatPo -> {
//            WxFormatVo wxFormatVo = new WxFormatVo();
//            BeanUtils.copyProperties(wxFormatPo, wxFormatVo);
//
//            //根据版式主键 查询该版式下的 配置项
//            List<WxFormatItemPo> wxFormatItemPos = wxFormatItemMapper.selectByFormatId(wxFormatPo.getId());
//
//            List<WxFormatVo.WxFormatItemVo> wxFormatItemVos = wxFormatItemPos.stream().map(wxFormatItemPo -> {
//                WxFormatVo.WxFormatItemVo wxFormatItemVo = new WxFormatVo.WxFormatItemVo();
//                wxFormatItemVo.setName(wxFormatItemPo.getName());
//                wxFormatItemVo.setId(wxFormatItemPo.getId());
//                wxFormatItemVo.setStatus(wxFormatItemPo.getStatus());
//                return wxFormatItemVo;
//            }).collect(Collectors.toList());
//
//            wxFormatVo.setWxFormatItemVos(wxFormatItemVos);
//
//            return wxFormatVo;
//        }).collect(Collectors.toList());
//
//        return wxFormatVoList;
//    }

    /**
     * 删除 该版式
     * 并同时删除该版式下 所有的配置项
     *
     * @param formatId
     * @return
     */
    public ApiResponse<Boolean> delteById(Integer formatId) {

        Boolean ret = true;
        //先删除(逻辑删除) 该 版式
        WxFormatPo wxFormatPo = wxFormatMapper.selectById(formatId);
        if (wxFormatPo != null) {
            wxFormatPo.setStatus(CommonConstant.STATUS_DEL);
            wxFormatPo.setLastUpdateTime(System.currentTimeMillis());
            wxFormatMapper.updateById(wxFormatPo);
            //再删除 版式表中 的配置项

            WxFormatItemPo wxFormatItemPo = new WxFormatItemPo();
            wxFormatItemPo.setFormatId(formatId);
            wxFormatItemPo.setStatus(CommonConstant.STATUS_DEL);
            wxFormatItemPo.setLastUpdateTime(System.currentTimeMillis());
            ret = ret && wxFormatItemMapper.updateStatusByFormId(wxFormatItemPo) > 0;
        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该版式不存在");
        }
        return new ApiResponse<>(ret);
    }


//    public Integer selectCount(DeviceTypeQueryRequest queryRequest) {
//        DeviceTypePo queryTypePo = new DeviceTypePo();
//        queryTypePo.setName(queryRequest.getName());
//        return deviceTypeMapper.selectCount(queryTypePo);
//    }
}
