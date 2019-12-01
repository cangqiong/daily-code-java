package com.chason.base.relearning.batch.process;

import com.chason.base.relearning.entity.People;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;

/**
 * 处理器,对读出来的数据进行处理，校验，过滤，筛选
 * Author: chason
 * Date: 2019/8/18 11:43
 **/
public class PersonItemProcessor implements ItemProcessor<People, People> {

    private static final Logger LOG = LoggerFactory.getLogger(PersonItemProcessor.class);

    @Override
    public People process(People people) throws Exception {
        final String firstName = people.getFirstName().toUpperCase();
        final String lastName = people.getLastName().toUpperCase();

        final People transformPeople = new People(firstName,lastName);
        LOG.info("处理前的数据:[{}],处理后的数据:[{}]", people, transformPeople);
        return transformPeople;
    }
}
