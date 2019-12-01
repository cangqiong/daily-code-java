package com.chason.base.relearning;

import com.chason.springdemo.trans.rollback.TransRollbackTest;
import com.chason.springdemo.trans.service.PersonService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringRunner.class)
@SpringBootTest
@MapperScan("com.chason.springdemo.starter.mapper")
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
