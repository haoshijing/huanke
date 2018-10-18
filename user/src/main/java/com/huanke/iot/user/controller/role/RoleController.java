package com.huanke.iot.user.controller.role;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.po.role.Permission;
import com.huanke.iot.base.po.role.Role;
import com.huanke.iot.base.po.role.Role2PermissionReq;
import com.huanke.iot.base.po.role.Role2PermissionRsp;
import com.huanke.iot.user.service.role.RoleService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/role")
public class RoleController {

    @Resource
    private RoleService roleService;

    @RequiresAuthentication
    @ApiOperation("根据角色ID查询角色详情")
    @GetMapping("/getRoleDetail/{id}")
    public ApiResponse<Role2PermissionRsp> getRoleDetail(@PathVariable("id") Integer roleId) {

        return new ApiResponse<>(roleService.getRoleDetail(roleId));
    }

    @RequiresAuthentication
    @ApiOperation("获取角色列表")
    @GetMapping("/getRoleList")
    public ApiResponse<List<Role>> getRoleList() {

        return new ApiResponse<>(roleService.getRoleList());
    }

    @RequiresAuthentication
    @ApiOperation("创建角色")
    @PostMapping("/createRole")
    public ApiResponse<Integer> createRole(@RequestBody Role2PermissionReq req) {

        return new ApiResponse<>(roleService.createRole(req));
    }

    @RequiresAuthentication
    @ApiOperation("修改角色")
    @PutMapping("/updateRole")
    public ApiResponse<Boolean> updateRole(@RequestBody Role2PermissionReq req) {

        return new ApiResponse<>(roleService.updateRole(req));
    }

    @RequiresAuthentication
    @ApiOperation("根据角色ID删除角色")
    @DeleteMapping("/delRole/{id}")
    public ApiResponse<Boolean> delRole(@PathVariable("id") Integer roleId) {

        return new ApiResponse<>(roleService.delRole(roleId));
    }

    @RequiresAuthentication
    @ApiOperation("获取当前用户的权限列表")
    @GetMapping("/getPermissions")
    public ApiResponse<List<Permission>> getPermissions() {

        return new ApiResponse<>(roleService.getPermissions());
    }

}
