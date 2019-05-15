//package com.huanke.iot.job.statstic;
//
//import com.huanke.iot.base.dao.customer.CustomerMapper;
//import com.huanke.iot.base.dao.customer.CustomerUserMapper;
//import com.huanke.iot.base.dao.statstic.StatsticCustomerUserLiveMapper;
//import com.huanke.iot.base.po.customer.CustomerPo;
//import com.huanke.iot.base.po.customer.CustomerUserPo;
//import com.huanke.iot.base.po.statstic.StatsticCustomerUserLivePo;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Repository;
//
//import java.util.Calendar;
//import java.util.Date;
//import java.util.List;
//
///**
// * 描述:
// * 统计活跃用户数
// *
// * @author 蔡坤
// * @create 2018-11-17 上午0:41
// */
//@Repository
//@Slf4j
//public class StatsticLiveCustomerUser {
//
//    @Autowired
//    private StatsticCustomerUserLiveMapper statsticCustomerUserLiveMapper;
//
//    @Autowired
//    private CustomerMapper customerMapper;
//
//    @Autowired
//    private CustomerUserMapper customerUserMapper;
//
//    @Scheduled(cron = "0 1/10 * * * ? ")
//    public void statsticLiveCustomerUser() {
//        Calendar c1 = Calendar.getInstance();//默认是当前日期
//        // 获得年份
//        int year = c1.get(Calendar.YEAR);
//        // 获得月份
//        int month = c1.get(Calendar.MONTH) + 1;
//        // 获得日期
//        int date = c1.get(Calendar.DATE);
//        // 获得小时
//        int hour = c1.get(Calendar.HOUR_OF_DAY);
//        // 获得分钟
//        int minute = c1.get(Calendar.MINUTE);
//        // 获得秒
//        int second = c1.get(Calendar.SECOND);
//
//        Long endTime = c1.getTimeInMillis();
//
//        //获取前10分钟的时间戳
//        c1.add(Calendar.MINUTE, -10);
//        Long startTime = c1.getTimeInMillis();
//        /*查询所有客户*/
//        List<CustomerPo> customerPos = customerMapper.selectAllCustomers(null);
//        if (customerPos != null && customerPos.size() > 0) {
//            customerPos.stream().forEach(customerPo -> {
//                //查询当前客户 近10分钟内访问用户数量
//                int curLiveCount = customerUserMapper.selectLiveUserCountByTime(startTime, endTime, customerPo.getId());
//
//                StatsticCustomerUserLivePo insertPo = new StatsticCustomerUserLivePo();
//                insertPo.setCustomerId(customerPo.getId());
//                insertPo.setUserLiveCount(curLiveCount);
//                insertPo.setStatisticYear(year);
//                insertPo.setStatisticMonth(month);
//                insertPo.setStatisticDay(date);
//                insertPo.setStatisticHour(hour);
//                insertPo.setStatisticMin(minute);
//                insertPo.setStatsticSec(second);
//
//                statsticCustomerUserLiveMapper.insert(insertPo);
//
//            });
//        }
//
//
//    }
//}
