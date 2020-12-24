package com.mybatisplus.plugin.rest;

import com.mybatisplus.plugin.entity.App;
import com.mybatisplus.plugin.entity.PluginData;
import com.mybatisplus.plugin.mapper.AppMapper;
import com.mybatisplus.plugin.service.PluginDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-12-19
 */
@RestController
@RequestMapping("app")
public class AppController {

    @Autowired
    private AppMapper appMapper;


    @GetMapping
    public List<App> getAll(){
        return appMapper.selectList(null);
    }


    @GetMapping("{version}")
    public List<App> getAll(@PathVariable("version") Integer version){
        return appMapper.getAppVersion(version);
    }


}
