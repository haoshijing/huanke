package com.huanke.iot.api.requestcontext;

public final class UserRequestContextHolder {

    private static ThreadLocal<UserRequestContext> contextThreadLocal = new ThreadLocal<>();

    public static void clear(){
        contextThreadLocal.remove();
    }

    public static UserRequestContext get(){
        UserRequestContext context = contextThreadLocal.get();

        if(context== null){
            context = new UserRequestContext();
            contextThreadLocal.set(context);
        }
        return context;
    }

}
