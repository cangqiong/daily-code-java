package com.chason.base.relearning.optional;

import java.util.Optional;

/**
 * Optional功能
 * Author: chason
 * Date: 2020/3/21 19:36
 **/
public class OptionalTest {
    class Company{
        Empoyee empoyee;

        public Empoyee getEmpoyee() {
            return empoyee;
        }
    }
    class Empoyee{
        String name;

        public  String getName() {
            return name;
        }
    }

    public static void main(String[] args) {
        Company company =null;
        Optional<Company> optional = Optional.ofNullable(company);


        String name = optional.map(Company::getEmpoyee)
                .map(Empoyee::getName)
                .orElse("UNKNOWN");
        System.out.println(name);

    }
}
