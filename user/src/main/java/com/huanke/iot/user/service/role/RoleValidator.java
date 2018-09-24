package com.huanke.iot.user.service.role;

public class RoleValidator {

    private RoleValidator() {
    }

    private static class Holder {
        private static RoleValidator instance = new RoleValidator();
    }

    public static RoleValidator getInstance() {
        return Holder.instance;
    }

}