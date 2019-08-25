package com.chason.springdemo.trans.rollback;

import com.chason.springdemo.trans.entity.Person;
import com.chason.springdemo.trans.service.PersonService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import javax.annotation.Resource;

/**
 * 多个方法事务嵌入测试
 * Author: chason
 * Date: 2019/8/24 23:21
 **/
@Service
@Slf4j
public class TransRollbackTest {

    @Autowired
    private PersonService personService;

    @Resource(name = "transactionManager")
    private DataSourceTransactionManager transactionManager;

        @Transactional
    public void methdA() throws Exception {
        int count = personService.count();
        log.info("Init person num:[{}]", count);
        Person person = personService.getById(1);
        try {

            methdB();
            person.setAddress("11");
            update(person);
        } catch (Exception e) {
            log.info(e.getMessage());
//            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            person.setAddress("dfd");
//            throw e;
//            update(person);

        }
        log.info("OK");
            transactionManager.rollback(TransactionAspectSupport.currentTransactionStatus());
            transactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
        System.out.println(TransactionAspectSupport.currentTransactionStatus().isNewTransaction());
        System.out.println(TransactionAspectSupport.currentTransactionStatus().isRollbackOnly());
//        System.out.println(TransactionAspectSupport.currentTransactionStatus().isCompleted());
//        personService.save(new Person("df2", "dfdf"));

    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void update(Person person) {
                System.out.println(TransactionAspectSupport.currentTransactionStatus().isNewTransaction());
        System.out.println(TransactionAspectSupport.currentTransactionStatus().isRollbackOnly());
        System.out.println(TransactionAspectSupport.currentTransactionStatus().isCompleted());
        personService.saveOrUpdate(person);
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void add() {
//                System.out.println(TransactionAspectSupport.currentTransactionStatus().isNewTransaction());
//        System.out.println(TransactionAspectSupport.currentTransactionStatus().isRollbackOnly());
//        System.out.println(TransactionAspectSupport.currentTransactionStatus().isCompleted());
     personService.save(new    Person("df","dfdf"));
}

    @Transactional
    public void methdB(){
//            throw new RuntimeException("1");
        personService.save(new    Person("df23432444444444444444444444444444444444444444444","df23432444444444444444444444444444444444444444444"));

    }

    @Transactional
    public void methdD() throws Exception {
        methdA();
    }
    public void methdC(){
//        try {
//            methdB();
//        }catch (Exception e){
//           log.error("Methed b exception"+e);
//           log.info("exception person num:[{}]",personService.count());
//        }
        personService.save(new Person("df", "dfdf"));
//        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        transactionManager.commit(TransactionAspectSupport.currentTransactionStatus());
        System.out.println(TransactionAspectSupport.currentTransactionStatus().isNewTransaction());
        System.out.println(TransactionAspectSupport.currentTransactionStatus().isRollbackOnly());
        System.out.println(TransactionAspectSupport.currentTransactionStatus().isCompleted());

        log.info("methdC person num:[{}]", personService.count());
    }
}
