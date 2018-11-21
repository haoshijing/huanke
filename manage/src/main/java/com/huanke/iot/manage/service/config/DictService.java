package com.huanke.iot.manage.service.config;

import com.huanke.iot.base.dao.DictMapper;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.config.DictPo;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.config.DictQueryRequest;
import com.huanke.iot.base.request.config.DictRequest;
import com.huanke.iot.base.resp.DictRsp;
import com.huanke.iot.base.resp.DictRspPo;
import com.huanke.iot.base.resp.QueryDictRsp;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
import com.huanke.iot.manage.vo.request.BaseRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * 描述:
 * 字典service
 *
 * @author onlymark
 * @create 2018-11-14 上午10:25
 */
@Slf4j
@Repository
public class DictService {
    @Autowired
    private UserService userService;
    @Autowired
    private DictMapper dictMapper;
    @Autowired
    private CustomerService customerService;

    /**
     * 添加或修改
     *
     * @param request
     * @return
     */
    public Boolean addOrUpdateDict(DictRequest request) {
        User user = userService.getCurrentUser();
        DictPo dictPo = new DictPo();
        BeanUtils.copyProperties(request, dictPo);
        dictPo.setCustomerId(customerService.obtainCustomerId(false));
        if (dictPo.getId() == null) {
            //添加
            //判断不能重复
            Integer count = dictMapper.confirmAdd(dictPo);
            if(count > 0){
                throw new BusinessException("已存在当前类型的该值");
            }
            dictPo.setCreateTime(new Date());
            dictPo.setCreateUserId(user.getId());
            return dictMapper.insert(dictPo) > 0;
        } else {
            //修改
            Integer count = dictMapper.confirmUpdate(dictPo);
            if(count > 1){
                throw new BusinessException("已存在当前类型的该值");
            }
            dictPo.setUpdateTime(new Date());
            dictPo.setUpdateUserId(user.getId());
            return dictMapper.updateById(dictPo) > 0;
        }
    }

    /**
     * 删除dict
     *
     * @param valueList
     * @return
     */
    public Boolean deleteDict(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = dictMapper.batchDelete(userId, valueList);
        return result;
    }


    public DictRsp selectList(DictQueryRequest request) {
        Integer customerId = customerService.obtainCustomerId(false);
        DictRsp dictRsp = new DictRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;

        DictPo dictPo = new DictPo();
        dictPo.setCustomerId(customerId);
        BeanUtils.copyProperties(request, dictPo);
        Integer count = dictMapper.selectCount(dictPo);
        dictRsp.setTotalCount(count);
        dictRsp.setCurrentPage(currentPage);
        dictRsp.setCurrentCount(limit);

        List<DictRspPo> dictPoList = dictMapper.selectPageList(dictPo, start, limit);
        dictRsp.setDictRspPoList(dictPoList);
        return dictRsp;
    }

    public List<QueryDictRsp> queryDict(BaseRequest<String> request) {
        Integer customerId = customerService.obtainCustomerId(false);
        String type = request.getValue();
        List<QueryDictRsp> queryDictRspList = dictMapper.queryDict(customerId, type);
        return queryDictRspList;
    }
}
