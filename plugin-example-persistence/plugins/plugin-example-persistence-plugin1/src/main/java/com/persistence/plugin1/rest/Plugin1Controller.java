package com.persistence.plugin1.rest;

import com.google.gson.Gson;
import com.persistence.plugin1.config.Plugin1Config;
import com.persistence.plugin1.entity.Plugin1;
import com.persistence.plugin1.mapper.Plugin1Mapper;
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
    private Plugin1Config pluginConfig;

    @Autowired
    private Gson gson;

    @GetMapping
    public String hello(){
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


}
