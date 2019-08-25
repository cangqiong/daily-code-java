package com.chason.springdemo.trans;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.chason.springdemo.trans.rollback.TransRollbackTest;
import com.chason.springdemo.trans.service.PersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan("com.chason.springdemo.trans.dao")
public class BootstrapApplicationTests {

//	@Bean
//	public PlatformTransactionManager txManager(DataSource dataSource) {
//		DataSourceTransactionManager dataSourceTransactionManager = new DataSourceTransactionManager(dataSource);
//		dataSourceTransactionManager.setGlobalRollbackOnParticipationFailure(false);
//		return dataSourceTransactionManager;
//	}

	@Autowired
	private TransRollbackTest rollbackTest;

	@Autowired
	private PersonService personService;

	@Test
	@Transactional
	public void contextLoads() throws Exception {
		try {
			rollbackTest.methdA();
		} catch (Exception e) {}
		personService.count();
	}

}
