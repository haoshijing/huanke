package com.huanke.iot.manage.service.project;

import com.huanke.iot.base.constant.ProjectMateriaLogContants;
import com.huanke.iot.base.dao.project.JobMapper;
import com.huanke.iot.base.dao.project.MateriaLogMapper;
import com.huanke.iot.base.dao.project.MateriaMapper;
import com.huanke.iot.base.exception.BusinessException;
import com.huanke.iot.base.po.project.ProjectMaterialInfo;
import com.huanke.iot.base.po.project.ProjectMaterialLog;
import com.huanke.iot.base.po.user.User;
import com.huanke.iot.base.request.project.MateriaUpdateRequest;
import com.huanke.iot.base.resp.project.JobMateria;
import com.huanke.iot.base.resp.project.ProjectJobMateriaRsp;
import com.huanke.iot.manage.service.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 描述:
 * Materiaservice
 *
 * @author onlymark
 * @create 2018-11-16 上午9:36
 */
@Slf4j
@Repository
public class MateriaService {
    @Autowired
    private MateriaLogMapper materiaLogMapper;
    @Autowired
    private MateriaMapper materiaMapper;
    @Autowired
    private UserService userService;
    @Autowired
    private JobMapper jobMapper;

    @Transactional
    public Boolean updateMateria(MateriaUpdateRequest request) {
        User user = userService.getCurrentUser();
        //修改
        ProjectMaterialInfo projectMaterialInfo = materiaMapper.selectById(request.getId());
        Integer type = request.getType();
        int num = projectMaterialInfo.getNums();
        if (type == ProjectMateriaLogContants.OPERATE_TYPE_ADD) {
            num = num + request.getHanderNums();
        } else if (type == ProjectMateriaLogContants.OPERATE_TYPE_DEL || type == ProjectMateriaLogContants.OPERATE_TYPE_USE) {
            num = num - request.getHanderNums();
            if (num < 0) {
                throw new BusinessException("可用数量不足");
            }
        }
        projectMaterialInfo.setNums(num);
        projectMaterialInfo.setUpdateTime(new Date());
        projectMaterialInfo.setUpdateUser(user.getId());
        materiaMapper.updateById(projectMaterialInfo);

        //记录日志
        ProjectMaterialLog projectMaterialLog = new ProjectMaterialLog();
        projectMaterialLog.setType(request.getType());
        projectMaterialLog.setHanderNums(request.getHanderNums());
        projectMaterialLog.setMaterialId(projectMaterialInfo.getId());
        projectMaterialLog.setCurrentNums(projectMaterialInfo.getNums());
        projectMaterialLog.setCreateTime(new Date());
        projectMaterialLog.setCreateUser(user.getId());
        return materiaLogMapper.insert(projectMaterialLog) > 0;

    }

    public ProjectJobMateriaRsp ifExistMateria(Integer jobId) {
        ProjectJobMateriaRsp projectJobMateriaRsp = new ProjectJobMateriaRsp();
        Map<Object, Object> resultMap = jobMapper.ifExistMateria(jobId);
        if (resultMap == null) {
            projectJobMateriaRsp.setIfExist(false);
            return projectJobMateriaRsp;
        }
        List<ProjectMaterialInfo> projectMaterialInfoList = materiaMapper.selectMaterialInfoByProjectId((Integer) resultMap.get("projectId"));
        if (projectMaterialInfoList.isEmpty()) {
            projectJobMateriaRsp.setIfExist(false);
            return projectJobMateriaRsp;
        }
        projectJobMateriaRsp.setIfExist(true);
        List<JobMateria> jobMateriaList = new ArrayList<>();
        for (ProjectMaterialInfo projectMaterialInfo : projectMaterialInfoList) {
            JobMateria jobMateria = new JobMateria();
            BeanUtils.copyProperties(projectMaterialInfo, jobMateria);
            jobMateriaList.add(jobMateria);
        }
        projectJobMateriaRsp.setJobMateriaList(jobMateriaList);
        return  projectJobMateriaRsp;
    }
}
