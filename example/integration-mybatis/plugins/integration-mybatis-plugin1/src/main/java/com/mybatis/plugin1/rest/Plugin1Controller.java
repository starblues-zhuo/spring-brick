package com.mybatis.plugin1.rest;

import com.google.gson.Gson;
import com.mybatis.plugin1.entity.Plugin1;
import com.mybatis.plugin1.mapper.Plugin1Mapper;
import com.mybatis.plugin1.service.TestService;
import com.mybatis.plugin1.service.TestTransactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@RestController
@RequestMapping("/plugin1")
public class Plugin1Controller {

    @Autowired
    private Plugin1Mapper pluginMapperl;

    @Autowired
    private Gson gson;

    @Autowired
    private TestService testService;

    @Autowired
    private TestTransactional testTransactional;

    @GetMapping
    public String hello(){
        testService.gson();
        return gson.toJson(pluginMapperl.getList());
    }

    @GetMapping("/list")
    public List<Plugin1> getList(){
        return pluginMapperl.getList();
    }

    @GetMapping("/{id}")
    public Plugin1 getConfig(@PathVariable("id") String id){
        return pluginMapperl.getById(id);
    }

    @GetMapping("/transactional")
    public void tran(){
        testTransactional.transactional();
    }
}
