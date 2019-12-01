package com.chason.base.relearning.service.impl;

import com.chason.base.relearning.entity.Person;
import com.chason.base.relearning.mapper.PersonMapper;
import com.chason.base.relearning.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author chason
 * @since 2019-08-25
 */
@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonMapper personMapper;

    @Override
    public Person getPerson(int id) {
        return personMapper.getPersonById(id);
    }
}
