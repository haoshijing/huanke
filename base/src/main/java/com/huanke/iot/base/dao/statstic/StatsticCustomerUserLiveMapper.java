package com.huanke.iot.base.dao.statstic;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.statstic.StatsticCustomerUserLivePo;

import java.util.List;

/**
 * 描述:
 *
 * @author caikun
 * @create 2018-11-17 早上1:54
 */
public interface StatsticCustomerUserLiveMapper extends BaseMapper<StatsticCustomerUserLivePo> {
    List selectLiveUserCountByMonth(StatsticCustomerUserLivePo statsticCustomerUserLivePo );
    List selectLiveUserCountByHour(StatsticCustomerUserLivePo statsticCustomerUserLivePo );
}
