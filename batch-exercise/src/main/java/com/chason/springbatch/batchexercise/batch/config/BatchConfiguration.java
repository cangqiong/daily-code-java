package com.chason.springbatch.batchexercise.batch.config;

import com.chason.springbatch.batchexercise.batch.listener.JobCompletionNotificationListener;
import com.chason.springbatch.batchexercise.batch.process.PersonItemProcessor;
import com.chason.springbatch.batchexercise.batch.writer.PersonItemWriter;
import com.chason.springbatch.batchexercise.entity.People;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;


/**
 * 批处理配置
 * Author: chason
 * Date: 2019/8/18 11:51
 **/
@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

    private String dataPath = "data/sample-data.csv";

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;


    // readers、processors、writers
    @Bean
    public ItemReader<People> personReader() {
        return new FlatFileItemReaderBuilder<People>()
                .name("personItemReader")
                .resource(new ClassPathResource(dataPath))
                .delimited()
                .names(new String[]{"firstName", "lastName"})
                .fieldSetMapper(new BeanWrapperFieldSetMapper<People>() {{
                    setTargetType(People.class);
                }})
                .build();
    }

    @Bean
    public PersonItemProcessor personProcessor() {
        return new PersonItemProcessor();
    }

    @Bean
    public PersonItemWriter personWriter(DataSource dataSource) {
        return new PersonItemWriter(dataSource);
    }

    // steps
    @Bean
    public Step step1(PersonItemWriter personItemWriter) {
        return stepBuilderFactory
                .get("PersonStep")
                .<People, People>chunk(2)
                .reader(personReader())
                .processor(personProcessor())
                .writer(personItemWriter)
                .build();
    }

    // jobs
    @Bean
    public Job importJob(JobCompletionNotificationListener listener, Step step1) {
        return jobBuilderFactory
                .get("importJob")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .flow(step1)
                .end()
                .build();
    }

}
