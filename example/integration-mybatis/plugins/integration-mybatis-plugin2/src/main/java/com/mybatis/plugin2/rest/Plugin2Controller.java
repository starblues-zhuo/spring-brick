package com.mybatis.plugin2.rest;

import com.mybatis.plugin2.entity.Plugin2;
import com.mybatis.plugin2.mapper.Plugin2Mapper;
import com.mybatis.plugin2.service.TestTransactional2;
import com.mybatis.plugin2.config.Plugin2Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
@RestController
@RequestMapping("/plugin2")
public class Plugin2Controller {

    @Autowired
    private Plugin2Mapper plugin2Mapper;

    @Autowired
    private Plugin2Config plugin2Config;

    @Autowired
    private TestTransactional2 testTransactional;


    @GetMapping
    public String hello(){
        return "my is plugin2";
    }

    @GetMapping("/list")
    public List<Plugin2> getList(){
        return plugin2Mapper.getList();
    }

    @GetMapping("/{id}")
    public Plugin2 getConfig(@PathVariable("id") String id){
        return plugin2Mapper.getById(id);
    }


    @GetMapping("/transactional")
    public void tran(){
        testTransactional.transactional();
    }

}
