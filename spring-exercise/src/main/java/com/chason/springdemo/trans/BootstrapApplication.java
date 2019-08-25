package com.chason.springdemo.trans;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.GlobalConfig;
import com.chason.springdemo.trans.rollback.TransRollbackTest;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("com.chason.springdemo.trans.dao")
public class BootstrapApplication {

	@Autowired
	private static TransRollbackTest rollbackTest;

	public static void main(String[] args) {

		SpringApplication.run(BootstrapApplication.class, args);
		rollbackTest.methdC();
	}

}
