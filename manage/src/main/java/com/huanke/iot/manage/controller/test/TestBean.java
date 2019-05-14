package com.huanke.iot.manage.controller.test;

import lombok.Data;

/**
 * 描述:
 * 测试
 *
 * @author onlymark
 * @create 2019-05-13 下午3:41
 */
@Data
public class TestBean {
    private String v1;
    private String v2;
    private String v3;
    private String v4;
    private String v5;

    public TestBean(String v1, String v2, String v3, String v4, String v5) {
        this.v1 = v1;
        this.v2 = v2;
        this.v3 = v3;
        this.v4 = v4;
        this.v5 = v5;
    }
}
