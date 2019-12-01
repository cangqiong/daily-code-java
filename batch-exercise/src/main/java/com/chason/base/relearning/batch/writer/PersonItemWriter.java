package com.chason.base.relearning.batch.writer;

import com.chason.base.relearning.entity.People;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.ItemPreparedStatementSetter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

/**
 * 写数据
 * Author: chason
 * Date: 2019/8/18 11:49
 **/
@Slf4j
public class PersonItemWriter implements ItemWriter<People> {

    private ItemWriter writer;

    private DataSource dataSource;

    public PersonItemWriter(DataSource dataSource) {
        this.dataSource = dataSource;
        writer = new JdbcBatchItemWriterBuilder<People>()
                .itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
                .sql("INSERT INTO people (first_name, last_name) VALUES (?,?)")
                .itemPreparedStatementSetter(new ItemPreparedStatementSetter<People>() {
                    @Override
                    public void setValues(People item, PreparedStatement ps) throws SQLException {
                        ps.setString(1, item.getFirstName());
                        ps.setString(2, item.getLastName());
                    }
                })
                .dataSource(dataSource)
                .build();
    }

    @Override
    public void write(List<? extends People> items) throws Exception {
        log.info("Once write person size :{}",items.size());
        writer.write(items);
    }
}
