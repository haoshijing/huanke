package com.huanke.iot.base.dao.impl.publicnumber;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.publicnumber.PublicNumberPo;

public interface PublicNumberMapper extends BaseMapper<PublicNumberPo> {
    PublicNumberPo selectByHost(String host);
}
