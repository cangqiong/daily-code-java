package com.chason.base.relearning.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * <p>
 *      Person 实体类
 * </p>
 *
 * @author chason
 * @since 2019-08-25
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
public class Person  {

    private Integer id;

    private String lastName;

    private String firstName;

    private String address;

    private String city;

    private Integer age;

    public Person(String lastName, String firstName) {
        this.lastName = lastName;
        this.firstName = firstName;
    }

}
