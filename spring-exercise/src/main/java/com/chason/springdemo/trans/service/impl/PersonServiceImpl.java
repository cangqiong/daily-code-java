package com.chason.springdemo.trans.service.impl;

import com.chason.springdemo.trans.entity.Person;
import com.chason.springdemo.trans.dao.PersonMapper;
import com.chason.springdemo.trans.service.PersonService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chason
 * @since 2019-08-25
 */
@Service
public class PersonServiceImpl extends ServiceImpl<PersonMapper, Person> implements PersonService {

}
