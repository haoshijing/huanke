package com.huanke.iot.manage.service.customer;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.dao.customer.*;
import com.huanke.iot.base.po.customer.*;
import com.huanke.iot.manage.vo.request.customer.CustomerQueryRequest;
import com.huanke.iot.manage.vo.request.customer.CustomerVo;
import com.huanke.iot.manage.vo.response.device.customer.CustomerUserVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 客户
 */
@Repository
public class CustomerUserService {

    @Autowired
    private CustomerUserMapper customerUserMapper;

    /**
     * 首页面板-统计用户
     *
     * @param
     * @return
     */

    public List<CustomerUserVo> selectCustomerUserCount() {
        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);
        int preYear = nowYear-1;
        List rtnList = new ArrayList();
        //今年的用户数据
        List nowUserConutList = customerUserMapper.selectCustomerUserCount(nowYear);
        //去年的用户数据
        List preYearUserList = customerUserMapper.selectCustomerUserCount(preYear);
        //上个月的用户数量
        Long prevMonthCount = new Long(0);
        //去年的上个月的用户数量
        Long prevYearMonthCount = new Long(0);

        String addPercent = "0.00%";
        for(int i=1;i<=12;i++){
            CustomerUserVo.CustomerUserCountVo customerUserCountVo = new CustomerUserVo.CustomerUserCountVo();
            String nowMonth = i +"月";
            if(i<10){
                nowMonth = "0"+i+"月";
            }
            //今年某月的用户量
            Long userCount = new Long(0);
            //今年某月用户增长量
            Long addCount = new Long(0);
            //去年某月用户量
            Long preYearUserCount =  new Long(0);
            //去年某月用户增长量
            Long preYearAddCount =  new Long(0);

            if(nowUserConutList!=null&&nowUserConutList.size()>0){
                for(int m=0;m<nowUserConutList.size();m++){
                    Map tempMap = (Map)nowUserConutList.get(m);
                    String tempMonth = (String)tempMap.get("userMonth");
                    Long tempUserCount = (Long)tempMap.get("userCount");
                    if(tempMonth.equals(nowMonth)){
                        userCount = tempUserCount;
                        addCount = userCount-prevMonthCount;
                        prevMonthCount = userCount;
                        break;
                    }
                }
            }

            if(preYearUserList!=null&&preYearUserList.size()>0){
                for(int m=0;m<preYearUserList.size();m++){
                    Map preMap = (Map)preYearUserList.get(m);
                    String tempMonth = (String)preMap.get("userMonth");
                    Long tempUserCount = (Long)preMap.get("userCount");
                    if(tempMonth.equals(nowMonth)){
                        preYearUserCount = tempUserCount;
                        preYearAddCount = preYearUserCount-prevYearMonthCount;

                        prevYearMonthCount = preYearUserCount;
                        break;
                    }
                }
            }

            DecimalFormat df = new DecimalFormat();
            df.setMaximumFractionDigits(2);
            df.setMinimumFractionDigits(2);
            //如果除数为0
            if(preYearAddCount==0){
                //被除数为0
                if(addCount==0){
                    addPercent = "--";
                }else{
                    addPercent = df.format(addCount * 100.00 / 1) + "%";
                }
            }else {
                addPercent = df.format((addCount-preYearAddCount) * 100.00 / preYearAddCount) + "%";
            }

            customerUserCountVo.setMonth(nowMonth);
            customerUserCountVo.setAddCount(addCount);
            customerUserCountVo.setUserCount(userCount);
            customerUserCountVo.setAddPercent(addPercent);
            rtnList.add(customerUserCountVo);
        }

        return rtnList;
    }




}
