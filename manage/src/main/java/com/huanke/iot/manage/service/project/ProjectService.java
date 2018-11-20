package com.huanke.iot.manage.service.project;

import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.dao.project.*;
import com.huanke.iot.base.po.project.ProjectBaseInfo;
import com.huanke.iot.base.po.project.ProjectExtraDevice;
import com.huanke.iot.base.po.project.ProjectMaterialInfo;
import com.huanke.iot.base.po.project.ProjectPlanInfo;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.project.PlanQueryRequest;
import com.huanke.iot.base.request.project.ProjectRequest;
import com.huanke.iot.base.resp.project.PlanRsp;
import com.huanke.iot.base.resp.project.PlanRspPo;
import com.huanke.iot.base.util.UniNoCreateUtils;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 描述:
 * 计划service
 *
 * @author onlymark
 * @create 2018-11-16 上午9:36
 */
@Slf4j
@Repository
public class ProjectService {
    @Autowired
    private UserService userService;
    @Autowired
    private PlanMapper planMapper;
    @Autowired
    private ProjectMapper projectMapper;
    @Autowired
    private RuleMapper ruleMapper;
    @Autowired
    private ExtraDeviceMapper extraDeviceMapper;
    @Autowired
    private MateriaMapper materiaMapper;
    @Autowired
    private CustomerService customerService;


    public PlanRsp selectList(PlanQueryRequest request) {
        Integer customerId = customerService.obtainCustomerId(false);
        PlanRsp planRsp = new PlanRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;

        ProjectPlanInfo projectPlan = new ProjectPlanInfo();
        projectPlan.setCustomerId(customerId);
        BeanUtils.copyProperties(request, projectPlan);
        Integer count = planMapper.selectCount(projectPlan);
        planRsp.setTotalCount(count);
        planRsp.setCurrentPage(currentPage);
        planRsp.setCurrentCount(limit);

        List<PlanRspPo> planPoList = planMapper.selectPageList(projectPlan, start, limit);
        for (PlanRspPo planRspPo : planPoList) {
            if (planRspPo.getIsRule() == 1) {
                planRspPo.setWarnLevel(ruleMapper.selectById(planRspPo.getRuleId()).getWarnLevel());
            }
        }
        planRsp.setPlanRspPoList(planPoList);
        return planRsp;
    }

    /**
     * 添加工程项目
     *
     * @param request
     * @return
     */
    @Transactional
    public Boolean addOrUpdate(ProjectRequest request) {
        User user = userService.getCurrentUser();
        ProjectBaseInfo projectBaseInfo = new ProjectBaseInfo();
        BeanUtils.copyProperties(request, projectBaseInfo);

        if (projectBaseInfo.getId() == null) {
            //添加
            projectBaseInfo.setCustomerId(customerService.obtainCustomerId(false));
            projectBaseInfo.setCreateTime(new Date());
            projectBaseInfo.setCreateUser(user.getId());
            projectMapper.insert(projectBaseInfo);
            //添加第三方设备
            List<ProjectRequest.ExtraDevice> extraDeviceList = request.getExtraDeviceList();
            for (ProjectRequest.ExtraDevice extraDevice : extraDeviceList) {
                ProjectExtraDevice projectExtraDevice = new ProjectExtraDevice();
                BeanUtils.copyProperties(extraDevice, projectExtraDevice);
                projectExtraDevice.setStatus(CommonConstant.STATUS_YES);
                projectExtraDevice.setCreateTime(new Date());
                projectExtraDevice.setCreateUser(user.getId());
                projectExtraDevice.setProjectId(projectBaseInfo.getId());
                projectExtraDevice.setDeviceNo(UniNoCreateUtils.createNo(DeviceConstant.DEVICE_UNI_NO_EXTRADEVICE));
                extraDeviceMapper.insert(projectExtraDevice);
            }
            //添加材料信息
            List<ProjectRequest.MaterialInfo> materialInfoList = request.getMaterialInfoList();
            for (ProjectRequest.MaterialInfo materialInfo : materialInfoList) {
                ProjectMaterialInfo projectMaterialInfo = new ProjectMaterialInfo();
                BeanUtils.copyProperties(materialInfo, projectMaterialInfo);
                projectMaterialInfo.setStatus(CommonConstant.STATUS_YES);
                projectMaterialInfo.setCreateTime(new Date());
                projectMaterialInfo.setCreateUser(user.getId());
                projectMaterialInfo.setProjectId(projectBaseInfo.getId());
                materiaMapper.insert(projectMaterialInfo);
            }

        }else {
            //修改
            projectBaseInfo.setUpdateTime(new Date());
            projectBaseInfo.setUpdateUser(user.getId());
            projectMapper.updateById(projectBaseInfo);

            //修改第三方设备信息
            List<ProjectRequest.ExtraDevice> extraDeviceList = request.getExtraDeviceList();
            List<Integer> idList = extraDeviceList.stream().map(e -> e.getId()).collect(Collectors.toList());
            

            //修改材料信息
        }
        //TODO
        return true;
    }

    public Boolean deletePlan(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = planMapper.batchDelete(userId, valueList);
        return result;
    }

    public Boolean forbitPlan(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = planMapper.batchForbidden(userId, valueList);
        return result;
    }

    public ProjectPlanInfo selectById(Integer planId) {
        ProjectPlanInfo projectPlanInfo = planMapper.selectById(planId);
        return projectPlanInfo;
    }
}
