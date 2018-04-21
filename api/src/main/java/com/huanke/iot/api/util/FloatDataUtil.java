package com.huanke.iot.api.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;

import java.nio.ByteOrder;

public class FloatDataUtil {
    public static String getFloat(int a){
        ByteBuf byteBuf = Unpooled.buffer(4);
        byteBuf.writeInt(a);
        System.out.println("a = [" + a + "]");

        return ""+0+"."+1;
    }
    public static void main(String[] args) {
        int a = 800;

        System.out.println("args = [" + getFloat(800) + "]");
    }
}
