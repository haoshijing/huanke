package com.huanke.iot.manage.service.project;

import com.huanke.iot.base.constant.CommonConstant;
import com.huanke.iot.base.constant.DeviceConstant;
import com.huanke.iot.base.constant.ProjectMateriaLogContants;
import com.huanke.iot.base.dao.device.DeviceGroupItemMapper;
import com.huanke.iot.base.dao.device.DeviceMapper;
import com.huanke.iot.base.dao.project.*;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.device.DevicePo;
import com.huanke.iot.base.po.project.*;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.BaseListRequest;
import com.huanke.iot.base.request.project.CopyProjectPlanReq;
import com.huanke.iot.base.request.project.ExistProjectNoRequest;
import com.huanke.iot.base.request.project.ProjectQueryRequest;
import com.huanke.iot.base.request.project.ProjectRequest;
import com.huanke.iot.base.resp.project.*;
import com.huanke.iot.base.util.UniNoCreateUtils;
import com.huanke.iot.manage.service.customer.CustomerService;
import com.huanke.iot.manage.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
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
    @Autowired
    private DeviceGroupItemMapper deviceGroupItemMapper;
    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private MateriaLogMapper materiaLogMapper;


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
        Integer userId = user.getId();
        ProjectBaseInfo projectBaseInfo = new ProjectBaseInfo();
        BeanUtils.copyProperties(request, projectBaseInfo);
        projectBaseInfo.setImgList(String.join(",",request.getImgs()));
        if (projectBaseInfo.getId() == null) {
            //添加
            projectBaseInfo.setCustomerId(customerService.obtainCustomerId(false));
            projectBaseInfo.setCreateTime(new Date());
            projectBaseInfo.setCreateUser(userId);
            projectBaseInfo.setStatus(CommonConstant.STATUS_YES);
            projectMapper.insert(projectBaseInfo);
            //添加第三方设备
            List<ProjectRequest.ExtraDevice> extraDeviceList = request.getExtraDeviceList();
            for (ProjectRequest.ExtraDevice extraDevice : extraDeviceList) {
                ProjectExtraDevice projectExtraDevice = new ProjectExtraDevice();
                BeanUtils.copyProperties(extraDevice, projectExtraDevice);
                projectExtraDevice.setStatus(CommonConstant.STATUS_YES);
                projectExtraDevice.setCreateTime(new Date());
                projectExtraDevice.setCreateUser(userId);
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
                projectMaterialInfo.setCreateUser(userId);
                projectMaterialInfo.setProjectId(projectBaseInfo.getId());
                materiaMapper.insert(projectMaterialInfo);
                //log
                logMateria(projectMaterialInfo, ProjectMateriaLogContants.OPERATE_TYPE_CREATE, userId);
            }

        } else {
            //修改
            //判断projectNo是否重复
            if(projectMapper.editIfExist(projectBaseInfo.getProjectNo(), projectBaseInfo.getId()) > 0){
                throw new BusinessException("projectNo已存在，请重新输入");
            }

            projectBaseInfo.setUpdateTime(new Date());
            projectBaseInfo.setUpdateUser(userId);
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
                    projectExtraDevice.setCreateUser(userId);
                    projectExtraDevice.setProjectId(projectBaseInfo.getId());
                    projectExtraDevice.setDeviceNo(UniNoCreateUtils.createNo(DeviceConstant.DEVICE_UNI_NO_EXTRADEVICE));
                    extraDeviceMapper.insert(projectExtraDevice);
                } else {//已存在ID，说明是修改的设备
                    ProjectExtraDevice projectExtraDevice = extraDeviceMapper.selectById(eId);
                    BeanUtils.copyProperties(e, projectExtraDevice);
                    projectExtraDevice.setStatus(CommonConstant.STATUS_YES);
                    projectExtraDevice.setUpdateUser(userId);
                    projectExtraDevice.setUpdateTime(new Date());
                    extraDeviceMapper.updateById(projectExtraDevice);
                }
            });
            List<Integer> deleteIds = existIdList.stream().filter(e -> !extraDeviceIddList.contains(e)).collect(Collectors.toList());
            if (deleteIds.size() != 0) {
                extraDeviceMapper.batchDelete(userId, deleteIds);//删除修改中已删除的设备

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
                    projectMaterialInfo.setCreateUser(userId);
                    projectMaterialInfo.setProjectId(projectBaseInfo.getId());
                    materiaMapper.insert(projectMaterialInfo);
                    //log
                    logMateria(projectMaterialInfo, ProjectMateriaLogContants.OPERATE_TYPE_CREATE, userId);
                } else {//已存在ID，说明是修改的材料
                    ProjectMaterialInfo projectMaterialInfo = materiaMapper.selectById(eId);
                    BeanUtils.copyProperties(e, projectMaterialInfo);
                    projectMaterialInfo.setStatus(CommonConstant.STATUS_YES);
                    projectMaterialInfo.setUpdateUser(userId);
                    projectMaterialInfo.setUpdateTime(new Date());
                    materiaMapper.updateById(projectMaterialInfo);
                    //log
                    logMateria(projectMaterialInfo, ProjectMateriaLogContants.OPERATE_TYPE_UPDATE, userId);
                }
            });
            List<Integer> deleteMaterialIds = existMaterialIdList.stream().filter(e -> !materialIdList.contains(e)).collect(Collectors.toList());
            if (deleteMaterialIds.size() != 0) {
                materiaMapper.batchDelete(userId, deleteMaterialIds);//删除修改中已删除的材料
            }
        }
        return true;
    }

    private void logMateria(ProjectMaterialInfo projectMaterialInfo, Integer type, Integer userId) {
        ProjectMaterialLog projectMaterialLog = new ProjectMaterialLog();
        projectMaterialLog.setType(type);
        projectMaterialLog.setMaterialId(projectMaterialInfo.getId());
        projectMaterialLog.setHanderNums(projectMaterialInfo.getNums());
        projectMaterialLog.setCurrentNums(projectMaterialInfo.getNums());
        projectMaterialLog.setCreateTime(new Date());
        projectMaterialLog.setUpdateTime(new Date());
        projectMaterialLog.setCreateUser(userId);
        projectMaterialLog.setUpdateUser(userId);
        materiaLogMapper.insert(projectMaterialLog);
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
        ProjectBaseInfo projectBaseInfo = projectMapper.selectByProjectId(projectId);
        ProjectRequest projectRequest = new ProjectRequest();
        BeanUtils.copyProperties(projectBaseInfo,projectRequest);
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
        if(StringUtils.isNotEmpty(projectBaseInfo.getImgList())) {
            projectRequest.setImgs(projectBaseInfo.getImgList().split(","));
        }
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

    public ProjectAnalysisRsp projectAnalysis(Integer projectId) {
        ProjectAnalysisRsp projectAnalysisRsp = new ProjectAnalysisRsp();
        //工程项目数量,工程设备数量
        ProjectBaseInfo projectBaseInfo = projectMapper.selectById(projectId);
        String groupIds = projectBaseInfo.getGroupIds();
        if (groupIds != null && !groupIds.equals("")) {
            List<String> groupIdList = Arrays.asList(groupIds.split(","));
            //工程项目数量
            projectAnalysisRsp.setProjectGroupNum(groupIdList.size());
            List<DevicePo> devicePoList = deviceGroupItemMapper.selectByGroupIds(groupIds);
            Integer totalDeviceCount = devicePoList.size();
            //工程设备数量
            projectAnalysisRsp.setDeviceNum(totalDeviceCount);
            long onlineDeviceCount = devicePoList.stream().filter(e -> e.getOnlineStatus() == 1).count();
            long powerDeviceCount = devicePoList.stream().filter(e -> e.getPowerStatus() != null && e.getPowerStatus() == 1).count();
            DecimalFormat df=new DecimalFormat("0.00");//设置保留位数
            projectAnalysisRsp.setOnlineDeviceProportion(df.format((float)onlineDeviceCount/totalDeviceCount));
            projectAnalysisRsp.setPowerDeviceProportion(df.format((float)powerDeviceCount/totalDeviceCount));
        } else {
            projectAnalysisRsp.setProjectGroupNum(0);
            projectAnalysisRsp.setDeviceNum(0);
            projectAnalysisRsp.setOnlineDeviceProportion("");
            projectAnalysisRsp.setPowerDeviceProportion("");
        }
        //维保次数
        Integer maintenanceCount = jobMapper.selectMaintenanceCountByProjectId(projectId);
        projectAnalysisRsp.setMaintenanceCount(maintenanceCount);
        //设备告警，暂无
        projectAnalysisRsp.setDeviceWarnCount(0);
        return projectAnalysisRsp;
    }


    public List<ProjectPlanCount> getCopyProjectPlan(Integer projectId){
        Integer customerId = customerService.obtainCustomerId(false);
        List<ProjectPlanCount> projectPlanCounts = projectMapper.queryOtherProjectPlanCount(customerId,projectId);
        return projectPlanCounts;
    }

    @Transactional
    public Boolean copyProjectPlan(CopyProjectPlanReq copyProjectPlanReq){
        try {
            Integer customerId = customerService.obtainCustomerId(false);
            User user = userService.getCurrentUser();
            ProjectPlanInfo query = new ProjectPlanInfo();
            query.setCustomerId(customerId);
            query.setLinkType(2);
            query.setLinkProjectId(copyProjectPlanReq.getSourceProjectId());
            query.setStatus(1);
            List<ProjectPlanInfo> projectPlanInfos = planMapper.selectList(query, 10000, 0);
            projectPlanInfos.stream().forEach(temp -> {
                if (temp.getCycleType() != 0) {
                    ProjectPlanInfo copyPlan = new ProjectPlanInfo();
                    BeanUtils.copyProperties(temp, copyPlan);
                    copyPlan.setId(null);
                    copyPlan.setLinkProjectId(copyProjectPlanReq.getCurrentProjectId());
                    copyPlan.setCreateTime(new Date());
                    copyPlan.setCreateUser(user.getId());
                    copyPlan.setUpdateTime(null);
                    copyPlan.setUpdateUser(null);
                    planMapper.insert(copyPlan);
                }
            });
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
