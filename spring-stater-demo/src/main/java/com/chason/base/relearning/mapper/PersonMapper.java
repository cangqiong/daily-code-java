package com.chason.base.relearning.mapper;

import com.chason.base.relearning.entity.Person;

/**
 * <p>
 * Mapper 接口
 * </p>
 *
 * @author chason
 * @since 2019-08-25
 */
public interface PersonMapper {

    Person getPersonById(int id);
}
