package com.chason.base.relearning.threadsync;

import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 测试配置
 * Author: chason
 * Date: 2019/8/25 22:51
 **/
@Configuration
public class TestConfig {

    @ConditionalOnClass(value={org.springframework.context.annotation.Configuration.class})
    @Bean
    public void test(){
        System.out.println("111");
    }

//    @ConditionalOnMissingBean(value={org.springframework.context.annotation.Configuration.class})
    @ConditionalOnMissingBean(value={MybatisAutoConfiguration.class})
    @Bean
    public void test2(){
        System.out.println("222");
    }
}
