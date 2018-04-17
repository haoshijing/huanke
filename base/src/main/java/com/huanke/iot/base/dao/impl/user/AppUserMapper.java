package com.huanke.iot.base.dao.impl.user;

import com.huanke.iot.base.dao.BaseMapper;
import com.huanke.iot.base.po.user.AppUserPo;

/**
 * @author haoshijing
 * @version 2018年04月09日 09:37
 **/
public interface AppUserMapper extends BaseMapper<AppUserPo>{
     AppUserPo selectByOpenId(String openId);

     int updateAndroidMac(AppUserPo appUserPo);

     AppUserPo selectByMac(String androidMac);
}
