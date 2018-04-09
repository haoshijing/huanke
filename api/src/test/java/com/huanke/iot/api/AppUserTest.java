package com.huanke.iot.api;

import com.huanke.iot.base.dao.impl.user.AppUserMapper;
import com.huanke.iot.base.po.user.AppUserPo;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author haoshijing
 * @version 2018年04月09日 10:31
 **/
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ApiApplicationStarter.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EnableAutoConfiguration
public class AppUserTest {
    @Autowired
    AppUserMapper appUserMapper;

    @Test
    public void testInsertAppUser(){
        AppUserPo appUserPo = new AppUserPo();
        appUserPo.setOpenId("jshdjshdjshd");
        appUserPo.setCreateTime(System.currentTimeMillis());

        Assert.assertTrue(appUserMapper.insert(appUserPo) > 0);
    }

    @Test
    public void testQuery(){
        AppUserPo appUserPo = new AppUserPo();
        appUserPo.setOpenId("jshdjshdjshd");

        Assert.assertTrue(appUserMapper.selectByOpenId("jshdjshdjshd") != null);
    }

}
