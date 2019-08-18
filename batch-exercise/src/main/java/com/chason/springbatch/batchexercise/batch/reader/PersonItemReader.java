package com.chason.springbatch.batchexercise.batch.reader;

import com.chason.springbatch.batchexercise.entity.People;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;

/**
 * 读
 * Author: chason
 * Date: 2019/8/18 11:49
 **/
public class PersonItemReader implements ItemReader<People> {

    @Autowired
    private FlatFileItemReader<People> cvsFileItemReader;

    private String dataPath = "sample-data.csv";

    @Override
    public People read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {

        // 设置资源
        cvsFileItemReader.setResource(new ClassPathResource(dataPath));
        // 设置行Mapper
        DefaultLineMapper defaultLineMapper = new DefaultLineMapper();
        // 行分割符
        defaultLineMapper.setLineTokenizer(new DelimitedLineTokenizer(","));
        // 字段映射
        defaultLineMapper.setFieldSetMapper(new BeanWrapperFieldSetMapper<People>() {{
            setTargetType(People.class);
        }});
        cvsFileItemReader.setLineMapper(defaultLineMapper);
        return cvsFileItemReader.read();
    }
}
