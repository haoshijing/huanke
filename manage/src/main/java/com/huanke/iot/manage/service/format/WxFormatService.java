package com.huanke.iot.manage.service.format;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.CustomerMapper;
import com.huanke.iot.base.dao.device.typeModel.DeviceTypeMapper;
import com.huanke.iot.base.dao.format.WxFormatItemMapper;
import com.huanke.iot.base.dao.format.WxFormatMapper;
import com.huanke.iot.base.dao.format.WxFormatPageMapper;
import com.huanke.iot.base.po.customer.CustomerPo;
import com.huanke.iot.base.po.device.typeModel.DeviceTypePo;
import com.huanke.iot.base.po.format.WxFormatItemPo;
import com.huanke.iot.base.po.format.WxFormatPagePo;
import com.huanke.iot.base.po.format.WxFormatPo;
import com.huanke.iot.base.util.CommonUtil;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.vo.request.format.WxFormatQueryRequest;
import com.huanke.iot.manage.vo.response.format.WxFormatVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class WxFormatService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private WxFormatMapper wxFormatMapper;

    @Autowired
    private WxFormatItemMapper wxFormatItemMapper;

    @Autowired
    private WxFormatPageMapper wxFormatPageMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private DeviceTypeMapper deviceTypeMapper;

    @Autowired
    private CommonUtil commonUtil;



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
            if (CommonConstant.STATUS_DEL.equals( wxFormatVo.getStatus())) {
                wxFormatPo.setStatus(CommonConstant.STATUS_DEL);
            } else {
                wxFormatPo.setStatus(CommonConstant.STATUS_YES);
            }
            ret = wxFormatMapper.updateById(wxFormatPo) > 0;
        } else {
            wxFormatPo.setCreateTime(System.currentTimeMillis());
            wxFormatPo.setStatus(CommonConstant.STATUS_YES);
            ret = wxFormatMapper.insert(wxFormatPo) > 0;
        }

        //判断 该版式的页面里是否为空，若不为空则进行保存

        if (wxFormatVo.getWxFormatPageVos() != null && wxFormatVo.getWxFormatPageVos().size() > 0) {

            wxFormatVo.getWxFormatPageVos().stream().forEach(wxFormatPageVo -> {
                WxFormatPagePo wxFormatPagePo = new WxFormatPagePo();
                BeanUtils.copyProperties(wxFormatPageVo, wxFormatPagePo);
                //如果是有id，则该配置项为更新，否则为新增保存
                if (wxFormatPageVo.getId() != null && wxFormatPageVo.getId() > 0) {
                    //如果当前 状态 =删除的时候 才会删除，否则 全部为正常状态
                    if (CommonConstant.STATUS_DEL.equals(wxFormatPageVo.getStatus())) {
                        wxFormatPagePo.setStatus(CommonConstant.STATUS_DEL);
                    } else {
                        wxFormatPagePo.setStatus(CommonConstant.STATUS_YES);
                    }
                    wxFormatPagePo.setLastUpdateTime(System.currentTimeMillis());
                    wxFormatPageMapper.updateById(wxFormatPagePo);
                } else {
                    wxFormatPagePo.setFormatId(wxFormatPo.getId());
                    wxFormatPagePo.setCreateTime(System.currentTimeMillis());
                    wxFormatPagePo.setStatus(CommonConstant.STATUS_YES);
                    wxFormatPageMapper.insert(wxFormatPagePo);
                }

                //判断 该版式的页面里的配置项是否为空，若不为空则进行保存
                if (wxFormatPageVo.getWxFormatItemVos() != null && wxFormatPageVo.getWxFormatItemVos().size() > 0) {

                    wxFormatPageVo.getWxFormatItemVos().stream().forEach(wxFormatItemVo -> {
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
                            wxFormatItemPo.setPageId(wxFormatPagePo.getId());
                            wxFormatItemPo.setCreateTime(System.currentTimeMillis());
                            wxFormatItemPo.setStatus(CommonConstant.STATUS_YES);
                            wxFormatItemMapper.insert(wxFormatItemPo);
                        }

                    });

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

        /*查询当前域名的 客户主键*/
        Integer hostCustomerId = customerService.obtainCustomerId(false);
        //如果 查询条件有客户，则查询该客户的版式，反之，则查询该二级域名的客户的版式,版式类型为3（安卓）的没有权限限制
        if(request.getType() ==null||request.getType()!= 3) {
            if (StringUtils.isBlank(request.getCustomerIds())) {
                queryWxFormatPo.setCustomerIds(hostCustomerId == null ? null : hostCustomerId.toString());
            } else {
                queryWxFormatPo.setCustomerIds(request.getCustomerIds());
            }
        }

        if (request != null) {
//            queryWxFormatPo.setId(request.getId());
            queryWxFormatPo.setName(request.getName());
            queryWxFormatPo.setOwerType(request.getOwerType());
            queryWxFormatPo.setType(request.getType());
            if (CommonConstant.STATUS_DEL.equals(request.getStatus())) {
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

            //根据版式主键 查询该版式下的 页面
            List<WxFormatVo.WxFormatPageVo> wxFormatPageVos = getPageList(wxFormatPo.getId());
            wxFormatVo.setWxFormatPageVos(wxFormatPageVos);


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

            //根据版式主键 查询该版式下的 页面
            List<WxFormatVo.WxFormatPageVo> wxFormatPageVos = getPageList(wxFormatPo.getId());
            wxFormatVo.setWxFormatPageVos(wxFormatPageVos);
        }
        return wxFormatVo;
    }

    /**
     * 查询客户可使用的版式列表
     *
     * @param customerId
     * @return
     */
    public ApiResponse<List<WxFormatVo>> selectFormatsByCustomerId(Integer customerId,Integer typeId) {

        WxFormatPo queryWxFormatPo = new WxFormatPo();

        //先查询该客户id是否存在
        CustomerPo customerPo = customerMapper.selectById(customerId);
        DeviceTypePo deviceTypePo = deviceTypeMapper.selectById(typeId);
        if(deviceTypePo==null){
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该类型不存在");
        }
        if(customerPo!=null){
            //如果 该客户的父级客户不为空。则放入其中。
//            String customerIds = customerId.toString();
//            if(customerPo.getParentCustomerId()!=null) {
//                customerIds += "," + customerPo.getParentCustomerId();
//            }
            //查询 版式列表
            queryWxFormatPo.setStatus(CommonConstant.STATUS_YES);
            queryWxFormatPo.setCustomerIds(customerId.toString());
            queryWxFormatPo.setTypeIds(typeId.toString());
            List<WxFormatPo> wxformatPoList = wxFormatMapper.selectFormatsByCustomerId(queryWxFormatPo);
            List<WxFormatVo> wxFormatVoList = wxformatPoList.stream().map(wxFormatPo -> {
                WxFormatVo wxFormatVo = new WxFormatVo();
                BeanUtils.copyProperties(wxFormatPo, wxFormatVo);

                //根据版式主键 查询该版式下的 页面
                List<WxFormatVo.WxFormatPageVo> wxFormatPageVos = getPageList(wxFormatPo.getId());
                wxFormatVo.setWxFormatPageVos(wxFormatPageVos);
                return wxFormatVo;
            }).collect(Collectors.toList());

            return new ApiResponse<>(wxFormatVoList) ;

        } else {
            return new ApiResponse<>(RetCode.PARAM_ERROR, "该客户不存在");

        }
    }

    /**
     * 查询 版式下的页面
     * @param formatId
     * @return
     */
    public List getPageList(Integer formatId){

        List<WxFormatPagePo> wxFormatPagePos = wxFormatPageMapper.selectByFormatId(formatId);

        List<WxFormatVo.WxFormatPageVo> wxFormatPageVos = wxFormatPagePos.stream().map(wxFormatPagePo -> {
            WxFormatVo.WxFormatPageVo wxFormatPageVo = new WxFormatVo.WxFormatPageVo();
            wxFormatPageVo.setId(wxFormatPagePo.getId());
            wxFormatPageVo.setName(wxFormatPagePo.getName());
            wxFormatPageVo.setPageNo(wxFormatPagePo.getPageNo());
            wxFormatPageVo.setShowImg(wxFormatPagePo.getShowImg());
            wxFormatPageVo.setStatus(wxFormatPagePo.getStatus());

            List<WxFormatVo.WxFormatItemVo> wxFormatItemVos = getItemList(formatId,wxFormatPagePo.getId());

            wxFormatPageVo.setWxFormatItemVos(wxFormatItemVos);
            return wxFormatPageVo;
        }).collect(Collectors.toList());
        return  wxFormatPageVos;
    }

    /**
     * 查询 每个页面下的配置项
     * @param formatId
     * @param pageId
     * @return
     */
    public List<WxFormatVo.WxFormatItemVo> getItemList(Integer formatId,Integer pageId){

        //根据版式主键 查询该版式下的 配置项
        List<WxFormatItemPo> wxFormatItemPos = wxFormatItemMapper.selectByPageId(formatId,pageId);

        List<WxFormatVo.WxFormatItemVo> wxFormatItemVos = wxFormatItemPos.stream().map(wxFormatItemPo -> {
            WxFormatVo.WxFormatItemVo wxFormatItemVo = new WxFormatVo.WxFormatItemVo();
            wxFormatItemVo.setName(wxFormatItemPo.getName());
            wxFormatItemVo.setId(wxFormatItemPo.getId());
            wxFormatItemVo.setAbilityType(wxFormatItemPo.getAbilityType());
            wxFormatItemVo.setStatus(wxFormatItemPo.getStatus());
            return wxFormatItemVo;
        }).collect(Collectors.toList());

        return  wxFormatItemVos;
    }
    /**
     * 删除 该版式
     * 并同时删除该版式下 所有的配置项
     *
     * @param formatId
     * @return
     */
    public ApiResponse<Boolean> deleteById(Integer formatId) {

        Boolean ret = true;
        //先删除(逻辑删除) 该 版式
        WxFormatPo wxFormatPo = wxFormatMapper.selectById(formatId);
        if (wxFormatPo != null) {
            wxFormatPo.setStatus(CommonConstant.STATUS_DEL);
            wxFormatPo.setLastUpdateTime(System.currentTimeMillis());
            wxFormatMapper.updateById(wxFormatPo);
            //再删除 版式表中 的配置项
            //todo 页面及配置项 暂不做处理
//            WxFormatItemPo wxFormatItemPo = new WxFormatItemPo();
//            wxFormatItemPo.setFormatId(formatId);
//            wxFormatItemPo.setStatus(CommonConstant.STATUS_DEL);
//            wxFormatItemPo.setLastUpdateTime(System.currentTimeMillis());
//            ret = ret && wxFormatItemMapper.updateStatusByFormId(wxFormatItemPo) > 0;
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
