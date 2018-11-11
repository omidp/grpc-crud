package com.omid.grpc.boot.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jedlab.framework.spring.dao.AbstractDAO;
import com.jedlab.framework.spring.service.AbstractCrudService;
import com.omid.grpc.boot.dao.PersonDao;
import com.omid.grpc.boot.domain.PersonEntity;

@Service
public class PersonService extends AbstractCrudService<PersonEntity>
{
    
    @Autowired
    PersonDao personDao;

    @Override
    public AbstractDAO<PersonEntity> getDao()
    {
        return personDao;
    }

}