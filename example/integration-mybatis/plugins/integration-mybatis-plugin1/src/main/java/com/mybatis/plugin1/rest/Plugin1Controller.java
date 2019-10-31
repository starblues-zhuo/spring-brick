package com.mybatis.plugin1.rest;

import com.mybatis.plugin1.entity.Plugin1;
import com.mybatis.plugin1.mapper.Plugin1Mapper;
import com.mybatis.plugin1.service.TranServiec;
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
    private TranServiec testTransactional;

    @GetMapping
    public String hello(){
        return "hello, 这是集成mybatis 插件1";
    }

    @GetMapping("/list")
    public List<Plugin1> getList(){
        return pluginMapperl.getList();
    }

    @GetMapping("/{id}")
    public Plugin1 getConfig(@PathVariable("id") String id){
        return pluginMapperl.getById(id);
    }

    @GetMapping("/bean/{id}")
    public Plugin1 getUserByIdBean(@PathVariable("id") String id){
        Plugin1 p = new Plugin1();
        p.setId(id);
        return pluginMapperl.getByIdOfBean(p);
    }

    @GetMapping("/transactional")
    public void tran(){
        testTransactional.transactional();
    }
}
