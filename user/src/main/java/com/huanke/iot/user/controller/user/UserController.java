package com.huanke.iot.user.controller.user;

import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.user.model.user.LoginRsp;
import com.huanke.iot.user.model.user.User;
import com.huanke.iot.user.service.user.UserService;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @ApiOperation("登录，返回用户信息（置空密码）和权限")
    @PostMapping("/login")
    public ApiResponse<LoginRsp> login(@RequestParam("userName") String userName, @RequestParam("pwd") String pwd) {

        return new ApiResponse<>(userService.login(userName, pwd));
    }

    @ApiOperation("登出")
    @RequiresAuthentication
    @DeleteMapping("/logout")
    public ApiResponse<Boolean> logout() {

        return new ApiResponse<>(userService.logout());
    }

    @RequiresAuthentication
    @ApiOperation("获取当前用户信息（置空密码）")
    @GetMapping("/getCurrentUser")
    public ApiResponse<User> getCurrentUser() {

        return new ApiResponse<>(userService.getCurrentUser());
    }

    @RequiresAuthentication
    @ApiOperation("创建用户")
    @PostMapping("/createUser")
    public ApiResponse<Integer> createUser(@RequestBody User user) {

        return new ApiResponse<>(userService.createUser(user));
    }

    @RequiresAuthentication
    @ApiOperation("更新用户")
    @PutMapping("/updateUser")
    public ApiResponse updateCurrentUser(@RequestBody User user) {

        return new ApiResponse<>(userService.updateUser(user));
    }

    @RequiresAuthentication
    @ApiOperation("根据用户ID删除用户")
    @DeleteMapping("/delUser/{id}")
    public ApiResponse<Boolean> delUser(@PathVariable("id") Integer userId) {

        return new ApiResponse<>(userService.delUser(userId));
    }

    @RequiresAuthentication
    @ApiOperation("获取用户列表")
    @GetMapping("/getUserList")
    public ApiResponse<List<User>> getUserList() {

        return new ApiResponse<>(userService.getUserList());
    }

}
