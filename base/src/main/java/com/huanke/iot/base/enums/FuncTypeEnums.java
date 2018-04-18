package com.huanke.iot.base.enums;

import lombok.Getter;
import lombok.Setter;

/**
 * @author haoshijing
 * @version 2018年04月18日 09:43
 **/
public enum  FuncTypeEnums {

    MODE(210,"mode","0,1");
    private FuncTypeEnums(int code,String mark,String range){
        this.code = code;
        this.mark = mark;
        this.range = range;
    }
    @Getter
    @Setter
    private int code;
    @Getter @Setter
    private String mark;

    private int funcType;

    @Getter @Setter
    private String range;
}
