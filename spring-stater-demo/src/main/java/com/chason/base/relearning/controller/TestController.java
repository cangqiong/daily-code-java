package com.chason.base.relearning.controller;

import com.chason.base.relearning.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller
 * Author: chason
 * Date: 2019/8/25 22:29
 **/
@RestController
public class TestController {

    @Autowired
    private PersonService personService;

    @RequestMapping(path = "/user/{id}")
    public Object getById(@PathVariable("id") Integer id) {
        return personService.getPerson(id);
    }
}
