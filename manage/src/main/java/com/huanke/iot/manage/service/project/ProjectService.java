package com.huanke.iot.manage.service.project;

import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.project.*;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.project.ProjectBaseInfo;
import com.huanke.iot.base.po.project.ProjectExtraDevice;
import com.huanke.iot.base.po.project.ProjectMaterialInfo;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.BaseListRequest;
import com.huanke.iot.base.request.project.ExistProjectNoRequest;
import com.huanke.iot.base.request.project.ProjectQueryRequest;
import com.huanke.iot.base.request.project.ProjectRequest;
import com.huanke.iot.base.resp.project.*;
import com.huanke.iot.base.util.UniNoCreateUtils;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
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
    @Autowired
    private DeviceMapper deviceMapper;


    public ProjectRsp selectList(ProjectQueryRequest request) {
        Integer customerId = customerService.obtainCustomerId(false);
        ProjectRsp projectRsp = new ProjectRsp();
        Integer limit = request.getLimit();
        Integer currentPage = request.getCurrentPage();
        Integer start = (currentPage - 1) * limit;

        ProjectBaseInfo projectInfo = new ProjectBaseInfo();
        projectInfo.setCustomerId(customerId);
        BeanUtils.copyProperties(request, projectInfo);

        Integer count = projectMapper.selectCount(projectInfo);
        projectRsp.setTotalCount(count);
        projectRsp.setCurrentPage(currentPage);
        projectRsp.setCurrentCount(limit);

        List<ProjectRspPo> projectPoList = projectMapper.selectPageList(projectInfo, start, limit);
        //核算项目数量、设备数量
        for (ProjectRspPo projectRspPo : projectPoList) {
            String[] projectArray = projectRspPo.getGroupIds().split(",");
            projectRspPo.setProjectCount(projectArray.length);
        }
        projectRsp.setProjectRspPoList(projectPoList);
        return projectRsp;
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
            projectBaseInfo.setStatus(CommonConstant.STATUS_YES);
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

        } else {
            //修改
            //判断projectNo是否重复
            if(projectMapper.editIfExist(projectBaseInfo.getProjectNo(), projectBaseInfo.getId()) > 0){
                throw new BusinessException("projectNo已存在，请重新输入");
            }

            projectBaseInfo.setUpdateTime(new Date());
            projectBaseInfo.setUpdateUser(user.getId());
            projectMapper.updateById(projectBaseInfo);

            //修改第三方设备信息
            List<ProjectRequest.ExtraDevice> extraDeviceList = request.getExtraDeviceList();
            List<Integer> extraDeviceIddList = extraDeviceList.stream().filter(e -> e.getId() != null).map(e -> e.getId()).collect(Collectors.toList());
            List<Integer> existIdList = extraDeviceMapper.selectExistIds(request.getId());

            extraDeviceList.stream().forEach(e -> {
                Integer eId = e.getId();
                if (eId == null) {//修改过程新添加的设备
                    ProjectExtraDevice projectExtraDevice = new ProjectExtraDevice();
                    BeanUtils.copyProperties(e, projectExtraDevice);
                    projectExtraDevice.setStatus(CommonConstant.STATUS_YES);
                    projectExtraDevice.setCreateTime(new Date());
                    projectExtraDevice.setCreateUser(user.getId());
                    projectExtraDevice.setProjectId(projectBaseInfo.getId());
                    projectExtraDevice.setDeviceNo(UniNoCreateUtils.createNo(DeviceConstant.DEVICE_UNI_NO_EXTRADEVICE));
                    extraDeviceMapper.insert(projectExtraDevice);
                } else {//已存在ID，说明是修改的设备
                    ProjectExtraDevice projectExtraDevice = extraDeviceMapper.selectById(eId);
                    BeanUtils.copyProperties(e, projectExtraDevice);
                    projectExtraDevice.setStatus(CommonConstant.STATUS_YES);
                    projectExtraDevice.setUpdateUser(user.getId());
                    projectExtraDevice.setUpdateTime(new Date());
                    extraDeviceMapper.updateById(projectExtraDevice);
                }
            });
            List<Integer> deleteIds = existIdList.stream().filter(e -> !extraDeviceIddList.contains(e)).collect(Collectors.toList());
            if (deleteIds.size() != 0) {
                extraDeviceMapper.batchDelete(user.getId(), deleteIds);//删除修改中已删除的设备

            }
            //修改材料信息
            List<ProjectRequest.MaterialInfo> materialInfoList = request.getMaterialInfoList();


            List<Integer> materialIdList = materialInfoList.stream().filter(e -> e.getId() != null).map(e -> e.getId()).collect(Collectors.toList());
            List<Integer> existMaterialIdList = materiaMapper.selectExistIds(request.getId());
            materialInfoList.stream().forEach(e -> {
                Integer eId = e.getId();
                if (eId == null) {//修改过程新添加的材料
                    ProjectMaterialInfo projectMaterialInfo = new ProjectMaterialInfo();
                    BeanUtils.copyProperties(e, projectMaterialInfo);
                    projectMaterialInfo.setStatus(CommonConstant.STATUS_YES);
                    projectMaterialInfo.setCreateTime(new Date());
                    projectMaterialInfo.setCreateUser(user.getId());
                    projectMaterialInfo.setProjectId(projectBaseInfo.getId());
                    materiaMapper.insert(projectMaterialInfo);
                } else {//已存在ID，说明是修改的材料
                    ProjectMaterialInfo projectMaterialInfo = materiaMapper.selectById(eId);
                    BeanUtils.copyProperties(e, projectMaterialInfo);
                    projectMaterialInfo.setStatus(CommonConstant.STATUS_YES);
                    projectMaterialInfo.setUpdateUser(user.getId());
                    projectMaterialInfo.setUpdateTime(new Date());
                    materiaMapper.updateById(projectMaterialInfo);
                }
            });
            List<Integer> deleteMaterialIds = existMaterialIdList.stream().filter(e -> !materialIdList.contains(e)).collect(Collectors.toList());
            if (deleteMaterialIds.size() != 0) {
                materiaMapper.batchDelete(user.getId(), deleteMaterialIds);//删除修改中已删除的材料
            }
        }
        return true;
    }

    public Boolean reverseProject(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = projectMapper.batchReverse(userId, valueList);
        return result;
    }

    public Boolean deleteProject(List<Integer> valueList) {
        Integer userId = userService.getCurrentUser().getId();
        Boolean result = projectMapper.batchDelete(userId, valueList);
        return result;
    }


    public ProjectRequest selectById(Integer projectId) {
        ProjectRequest projectRequest = projectMapper.selectByProjectId(projectId);
        //查关联其他设备
        List<ProjectRequest.ExtraDevice> extraDeviceList = new ArrayList<>();
        List<ProjectExtraDevice> projectExtraDeviceList = extraDeviceMapper.selectExtraDeviceByProjectId(projectId);
        for (ProjectExtraDevice projectExtraDevice : projectExtraDeviceList) {
            ProjectRequest.ExtraDevice extraDevice = new ProjectRequest.ExtraDevice();
            BeanUtils.copyProperties(projectExtraDevice, extraDevice);
            extraDeviceList.add(extraDevice);
        }
        projectRequest.setExtraDeviceList(extraDeviceList);
        //查关联材料信息
        List<ProjectRequest.MaterialInfo> materialList = new ArrayList<>();
        List<ProjectMaterialInfo> projectMaterialInfoList = materiaMapper.selectMaterialInfoByProjectId(projectId);
        for (ProjectMaterialInfo projectMaterialInfo : projectMaterialInfoList) {
            ProjectRequest.MaterialInfo materialInfo = new ProjectRequest.MaterialInfo();
            BeanUtils.copyProperties(projectMaterialInfo, materialInfo);
            materialList.add(materialInfo);
        }
        projectRequest.setMaterialInfoList(materialList);

        return projectRequest;
    }

    public List<ProjectDictRsp> selectProjectDict() {

        Integer customerId = customerService.obtainCustomerId(false);
        return projectMapper.selectProjectDict(customerId);
    }

    public Boolean existProjectNo(ExistProjectNoRequest request) {
        Integer projectId = request.getProjectId();
        String projectNo = request.getValue();
        Integer customerId = customerService.obtainCustomerId(false);
        if(projectId != null){
            return projectMapper.existProjectNo(customerId, projectId, projectNo) > 1;
        }
        return projectMapper.existProjectNo(customerId, projectId, projectNo) > 0;
    }

    public List<ProjectGroupsRsp> selectGroups(BaseListRequest<Integer> request) {
        List<Integer> valueList = request.getValueList();
        List<ProjectGroupsRsp> projectGroupsRspList = projectMapper.selectGroups(valueList);
        for (ProjectGroupsRsp projectGroupsRsp : projectGroupsRspList) {
            List<LinkGroupDeviceRspPo> devicePoList = deviceMapper.selectByGroupId(projectGroupsRsp.getId());
            projectGroupsRsp.setDeviceList(devicePoList);
        }
        return projectGroupsRspList;
    }
}
