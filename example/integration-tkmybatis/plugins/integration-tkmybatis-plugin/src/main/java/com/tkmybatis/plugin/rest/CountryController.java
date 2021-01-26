package com.tkmybatis.plugin.rest;

import com.github.pagehelper.PageHelper;
import com.tkmybatis.plugin.Service;
import com.tkmybatis.plugin.entity.Country;
import com.tkmybatis.plugin.mapper.CountryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
@RestController
@RequestMapping("tk")
public class CountryController {

    @Autowired
    private Service service;

    @GetMapping("all")
    public List<Country> getAll(){
        return service.getAll();
    }

    @GetMapping("one")
    public Country getOne(){
        return service.getOne();
    }

    @GetMapping("xml-all")
    public List<Country> getXmlAll(){
        return service.getAll();
    }
}
