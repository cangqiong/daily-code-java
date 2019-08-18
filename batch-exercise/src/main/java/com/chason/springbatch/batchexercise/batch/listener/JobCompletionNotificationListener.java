package com.chason.springbatch.batchexercise.batch.listener;

import com.chason.springbatch.batchexercise.entity.People;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * Job执行监听器
 * Author: chason
 * Date: 2019/8/18 12:32
 **/
@Component
@Slf4j
public class JobCompletionNotificationListener extends JobExecutionListenerSupport {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JobCompletionNotificationListener(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
            log.info("!!!Job FINISHED! Time to verify the result");
            jdbcTemplate.query("SELECT first_name, last_name from people",
                    (rs, row) ->
                            new People(rs.getString(1), rs.getString(2))
            ).forEach(person -> log.info("Found <{}>in the database.", person));
        }
    }
}
