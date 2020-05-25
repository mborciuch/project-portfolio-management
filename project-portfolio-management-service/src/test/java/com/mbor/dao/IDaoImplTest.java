package com.mbor.dao;

import org.junit.jupiter.api.Test;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
public abstract class IDaoImplTest<T> implements IDaoTest {

    protected static int createdEntitiesNumber = 3;

    protected static Random random = new Random();

    @Override
    @Test
    public void find_ThenSuccess() {
        assertTrue(getDao().find(1L).isPresent());
    }

    @Override
    @Test
    public void findAll_ThenSuccess() {
        List<T> lists = getDao().findAll();
        assertEquals(createdEntitiesNumber, lists.size());
    }

    @Override
    @Test
    public void delete_ThenSuccess() {
        getDao().delete(1L);
        assertEquals(createdEntitiesNumber - 1, getDao().findAll().size());
    }

    @Override
    @Test
    public void save_ThenSuccess() {
        getDao().findAll().size();
        T t = createNewEntity();
        assertTrue(getDao().save(t).isPresent());
    }

    protected abstract T createNewEntity();

    protected abstract IDao getDao();
}
