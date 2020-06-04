package com.mbor.dao;

import com.mbor.configuration.TestConfiguration;
import com.mbor.domain.BusinessRelationManager;
import com.mbor.domain.DemandSheet;
import com.mbor.domain.Project;
import com.mbor.entityFactory.TestObjectFactory;
import com.mbor.spring.ServiceConfiguration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith({SpringExtension.class})
@ContextConfiguration(classes = {ServiceConfiguration.class, TestConfiguration.class})
@ActiveProfiles("test")
@Transactional
class DemandSheetDaoTest  extends IDaoImplTest<DemandSheet> {

    @Autowired
    private IDemandSheetDao demandSheetDao;

    private static Long FIRST_BRM_ID;

    private static Long SECOND_BRM_ID;

    private static Long PROJECT_ID;

    @BeforeAll
    static void init(@Autowired EntityManagerFactory entityManagerFactory, @Autowired TestObjectFactory testObjectsFactory)  {
        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction =  entityManager.getTransaction();
        transaction.begin();
        for (int i = 0; i < IDaoImplTest.CREATED_ENTITIES_NUMBER; i++) {
            DemandSheet demandSheet = testObjectsFactory.prepareDemandSheet();
            entityManager.persist(demandSheet);
            entityIdList.add(demandSheet.getId());
        }
        transaction.commit();
        prepareTestData(entityManagerFactory);
    }

    @AfterAll
    static void clear(@Autowired TableClearer tableClearer){
        tableClearer.clearTables();
        entityIdList.clear();
    }

    @Test
    public void  getAllDemandSheetsOfBRMWithNoProjectThenSuccess(){
        assertEquals(1, demandSheetDao.getAllDemandSheetsOfBRMWithNoProject(FIRST_BRM_ID).size());
    }

    private static void prepareTestData(EntityManagerFactory entityManagerFactory){

        EntityManager entityManager = entityManagerFactory.createEntityManager();
        EntityTransaction transaction =  entityManager.getTransaction();
        transaction.begin();

        BusinessRelationManager firstBRM = new BusinessRelationManager();
        entityManager.persist(firstBRM);
        FIRST_BRM_ID = firstBRM.getId();

        BusinessRelationManager secondBRM = new BusinessRelationManager();
        entityManager.persist(secondBRM);
        SECOND_BRM_ID = secondBRM.getId();

        Project project = new Project();
        entityManager.persist(project);
        PROJECT_ID = project.getId();

        DemandSheet demandSheetWithBRMAndNoProject = entityManager.find(DemandSheet.class, getElementIndex(0));
        demandSheetWithBRMAndNoProject.setBusinessRelationManager(entityManager.find(BusinessRelationManager.class, FIRST_BRM_ID));

        DemandSheet demandSheetWithBRMAndProject = entityManager.find(DemandSheet.class, getElementIndex(1));
        demandSheetWithBRMAndProject.setBusinessRelationManager(entityManager.find(BusinessRelationManager.class, FIRST_BRM_ID));
        entityManager.find(Project.class, PROJECT_ID).setDemandSheet(demandSheetWithBRMAndProject);

        DemandSheet demandSheetWithOtherBRMAndNoProject = entityManager.find(DemandSheet.class, getElementIndex(2));
        demandSheetWithOtherBRMAndNoProject.setBusinessRelationManager(entityManager.find(BusinessRelationManager.class, SECOND_BRM_ID));

        transaction.commit();
    }

    @Override
    protected IDao getDao() {
        return demandSheetDao;
    }

    @Override
    protected DemandSheet createNewEntity() {
        return testObjectsFactory.prepareDemandSheet();
    }

}