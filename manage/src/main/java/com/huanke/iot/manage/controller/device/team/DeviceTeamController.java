package com.huanke.iot.manage.controller.device.team;


import com.huanke.iot.base.api.ApiResponse;
import com.huanke.iot.base.constant.RetCode;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.customer.CustomerUserPo;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.device.team.DeviceTeamPo;
import com.huanke.iot.manage.service.device.team.DeviceTeamService;
import com.huanke.iot.manage.vo.request.device.operate.QueryInfoByCustomerRequest;
import com.huanke.iot.manage.vo.request.device.team.*;
import com.huanke.iot.manage.vo.response.device.team.DeviceTeamVo;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/team")
@Slf4j
public class DeviceTeamController {
    @Autowired
    private DeviceTeamService deviceTeamService;


    @ApiOperation("创建新的组或更新已有组，并向其中添加或更新设备")
    @RequestMapping(value = "/createNewTeam",method = RequestMethod.POST)
    public ApiResponse<Integer> createNewTeam(@RequestBody TeamCreateOrUpdateRequest teamCreateOrUpdateRequest){
        //组名不可为空
        if(!StringUtils.isNotEmpty(teamCreateOrUpdateRequest.getName())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"组名不可为空");
        }
        //用户openid不可为空
        if(null == teamCreateOrUpdateRequest.getCreateUserOpenId()||StringUtils.isEmpty(teamCreateOrUpdateRequest.getCreateUserOpenId())){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"用户openid不可为空");
        }
        //查询用户是否存在
        CustomerUserPo customerUserPo = this.deviceTeamService.queryCustomerUser(teamCreateOrUpdateRequest.getCreateUserOpenId());
        if (customerUserPo==null){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"当前用户 "+teamCreateOrUpdateRequest.getCreateUserOpenId()+" 不存在");
        }
        //设备列表不为空时进行mac校验
        if(null != teamCreateOrUpdateRequest.getTeamDeviceCreateRequestList() && 0 < teamCreateOrUpdateRequest.getTeamDeviceCreateRequestList().size()){
            //查询设备的MAC是否存在
            TeamCreateOrUpdateRequest.TeamDeviceCreateRequest queryMacPo = this.deviceTeamService.queryDeviceByMac(teamCreateOrUpdateRequest);
            if(null != queryMacPo){
                return new ApiResponse<>(RetCode.OK,"所选设备中mac地址 "+queryMacPo.getMac()+" 不存在或已被删除");
            }
            //查询当前设备是否属于当前登录的客户
            TeamCreateOrUpdateRequest.TeamDeviceCreateRequest queryCustomerPo = this.deviceTeamService.queryDeviceCustomer(teamCreateOrUpdateRequest);
            if(null != queryCustomerPo){
                return new ApiResponse<>(RetCode.PARAM_ERROR,"设备列表中 "+queryCustomerPo.getMac()+" 尚未被分配或您无权绑定");
            }
            //查询设备列表中是否已有设备在其他组中
            TeamCreateOrUpdateRequest.TeamDeviceCreateRequest queryTeamPo=this.deviceTeamService.isDeviceHasTeam(teamCreateOrUpdateRequest);
            if(null != queryTeamPo){
                return new ApiResponse<>(RetCode.PARAM_ERROR,"设备列表中 "+queryTeamPo.getMac()+" 地址已存在其他组中");
            }
        }
        try {
            return this.deviceTeamService.createNewOrUpdateTeam(teamCreateOrUpdateRequest);
        } catch (Exception e) {
            log.error("创建/更新组失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "创建/更新组失败");
        }
    }

    @ApiOperation("查询组列表")
    @RequestMapping(value = "/queryTeamList",method = RequestMethod.POST)
    public ApiResponse<List<DeviceTeamVo>> queryTeamList(@RequestBody TeamListQueryRequest teamListQueryRequest){
        try{
            return this.deviceTeamService.queryTeamList(teamListQueryRequest);
        }catch (Exception e){
            log.error("查询组异常 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"查询组失败");
        }
    }


    /**
     * 根据主键查询 组信息
     * @param teamId
     * @return 成功返回true，失败返回false
     * @throws Exception
     */
    @ApiOperation("根据主键查询 组信息")
    @GetMapping(value = "/queryTeamById/{id}")
    public ApiResponse<DeviceTeamVo> queryTeamById(@PathVariable("id") Integer teamId) throws Exception{
        try {
            return  deviceTeamService.queryTeamById(teamId);
        } catch (Exception e) {
            return  new ApiResponse<>(RetCode.ERROR,"查询组信息失败");
        }
    }

    @ApiOperation("查询组的数量")
    @RequestMapping(value = "/queryTeamCount/{status}",method = RequestMethod.GET)
    public ApiResponse<Integer> queryTeamCount(@PathVariable("status") Integer status){
        return new ApiResponse<>(this.deviceTeamService.selectTeamCount(status));
    }


    @ApiOperation("通过指定用户openId托管组给另一用户")
    @RequestMapping(value = "/trusteeTeam",method = RequestMethod.POST)
    public ApiResponse<Integer> trusteeTeam(@RequestBody TeamTrusteeRequest teamTrusteeRequest){
        //首先查询要托管的用户是否存在
        try {
            CustomerUserPo queryCustomerUserPo = this.deviceTeamService.queryCustomerUser(teamTrusteeRequest.getOpenId());
            if (queryCustomerUserPo==null){
                return new ApiResponse<>(RetCode.PARAM_ERROR,"当前用户 "+teamTrusteeRequest.getOpenId()+" 不存在");
            }
            CustomerUserPo customerUserPo=this.deviceTeamService.trusteeTeam(teamTrusteeRequest);
            if(null != customerUserPo){
                return new ApiResponse<>(RetCode.OK,"托管成功",customerUserPo.getId());
            }
            else {
                return new ApiResponse<>(RetCode.ERROR,"托管失败");
            }
        } catch (Exception e) {
            log.error("托管组失败={}", e);
            return new ApiResponse<>(RetCode.ERROR, "托管组失败");
        }
    }

    @ApiOperation("删除选中的组")
    @RequestMapping(value = "/deleteOneTeam",method = RequestMethod.POST)
    public ApiResponse<Boolean> deleteOneTeam(@RequestBody  TeamDeleteRequest teamDeleteRequest){
        if(null == teamDeleteRequest || 1 > teamDeleteRequest.getTeamId()){
            return new ApiResponse<>(RetCode.PARAM_ERROR,"请先选择一个组");
        }
        try {
            return this.deviceTeamService.deleteOneTeam(teamDeleteRequest);
        }catch (Exception e){
            log.error("组删除异常 = {}",e);
            return new ApiResponse<>(RetCode.ERROR,"组删除异常");
        }
    }

    @ApiOperation("生成托管二维码")
    @RequestMapping(value = "/createTrusteeQrCode",method = RequestMethod.POST)
    public ApiResponse<String> createTrusteeQrCode(@RequestBody TrusteeQrCodeRequest trusteeQrCodeRequest){
        try {
            return new ApiResponse<>(this.deviceTeamService.createQrCode(trusteeQrCodeRequest.getTeamId()));
        }
        catch (Exception e) {
            if (e instanceof BusinessException){
                return new ApiResponse<>(RetCode.ERROR, e.getMessage());
            }else {
                return new ApiResponse<>(RetCode.ERROR, "二维码生成错误");
            }
        }
    }

    @ApiOperation("查询当前客户下的可绑定设备信息")
    @RequestMapping(value = "/queryDeviceInfo",method = RequestMethod.POST)
    public ApiResponse<List<DevicePo>> queryDeviceInfo(@RequestBody  QueryInfoByCustomerRequest queryInfoByCustomerRequest){
        List<DevicePo> devicePoList=this.deviceTeamService.queryDevicesByCustomer(queryInfoByCustomerRequest);
        if(0 == devicePoList.size()){
            return new ApiResponse<>(RetCode.OK,"当前客户下无可用设备",null);
        }
        else {
            return new ApiResponse<>(RetCode.OK,"查询成功",devicePoList);
        }
    }
}
