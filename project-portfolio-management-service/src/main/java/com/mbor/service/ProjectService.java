package com.mbor.service;

import com.mbor.dao.IProjectDao;
import com.mbor.domain.*;
import com.mbor.domain.employeeinproject.BusinessLeader;
import com.mbor.mapper.ProjectMapper;
import com.mbor.model.ProjectClassDTO;
import com.mbor.model.ProjectDTO;
import com.mbor.model.ProjectStatusDTO;
import com.mbor.model.creation.ProjectCreatedDTO;
import com.mbor.model.creation.ProjectCreationDTO;
import com.mbor.model.search.SearchProjectDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(propagation = Propagation.REQUIRED, isolation = Isolation.READ_COMMITTED)
public class ProjectService extends RawService<Project>  implements IProjectService<Project> {

    private final IProjectDao projectDao;

    private final IEmployeeService employeeService;

    private final IBusinessUnitService businessUnitService;

    private final IProjectRoleService projectRoleService;

    private final ProjectMapper projectMapper;

    @Autowired
    public ProjectService(IProjectDao projectDao, IEmployeeService employeeService, IBusinessUnitService businessUnitService, IProjectRoleService projectRoleService, ProjectMapper projectMapper) {
        this.projectDao = projectDao;
        this.employeeService = employeeService;
        this.businessUnitService = businessUnitService;
        this.projectRoleService = projectRoleService;
        this.projectMapper = projectMapper;
    }

    @Override
    public ProjectCreatedDTO save(ProjectCreationDTO projectCreationDTO) {
        Project project = projectMapper.convertCreationDtoToEntity(projectCreationDTO);
        BusinessLeader businessLeader;
        if(project.getBusinessLeader().getId() == null){
            businessLeader = new BusinessLeader();
            BusinessEmployee businessEmployee = (BusinessEmployee) employeeService.find(project.getBusinessLeader().getEmployee().getId());
            businessLeader.setEmployee(businessEmployee);
            projectRoleService.saveInternal(businessLeader);
        } else {
            businessLeader = (BusinessLeader) projectRoleService.find(project.getBusinessLeader().getId());
        }
        project.setBusinessLeader(businessLeader);
        project.setBusinessRelationManager((BusinessRelationManager) employeeService.find(project.getBusinessRelationManager().getId()));
        project.getBusinessRelationManager().getProjects().forEach(project1 -> System.out.println(project1.getProjectName()));

        Set<BusinessUnit> businessUnitSet = new HashSet<>();
        businessUnitSet.forEach(businessUnit -> project.addBusinessUnit((BusinessUnit) businessUnitService.find(businessUnit.getId())));
        Project savedProject =  super.saveInternal(project);
        return projectMapper.convertEntityToCreatedDto(savedProject);
    }

    @Override
    public List<ProjectDTO> findByMultipleCriteria(SearchProjectDTO searchProjectDTO) {
        String projectName = searchProjectDTO.getProjectName();
        List<ProjectClass> projectClass =  searchProjectDTO.getProjectClassDTOList()
                .stream()
                .map(mapProjectClassDTOToProjectClass())
                .collect(Collectors.toList());
        String businessUnitName = searchProjectDTO.getBusinessUnitName();
        List<ProjectStatus> projectStatusList = searchProjectDTO.getProjectStatusDTOList()
                .stream()
                .map(mapProjectStatusDTOToProjectStatus())
                .collect(Collectors.toList());
        LocalDate projectStartDate = searchProjectDTO.getProjectStartDate();
        List<Project>  result = getDao().findByMultipleCriteria(projectName, projectClass, businessUnitName, projectStatusList, projectStartDate);
        return null;
    }

    private Function<ProjectClassDTO, ProjectClass> mapProjectClassDTOToProjectClass(){
        return projectClassDTO -> Enum.valueOf(ProjectClass.class, projectClassDTO.name());
    }

    private Function<ProjectStatusDTO, ProjectStatus> mapProjectStatusDTOToProjectStatus(){
        return projectStatusDTO  -> Enum.valueOf(ProjectStatus.class, projectStatusDTO.name());
    }

    @Override
    public IProjectDao getDao() {
        return projectDao;
    }
}

