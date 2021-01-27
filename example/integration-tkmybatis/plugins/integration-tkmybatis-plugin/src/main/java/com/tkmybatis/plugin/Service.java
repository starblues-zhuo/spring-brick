package com.tkmybatis.plugin;

import com.github.pagehelper.PageHelper;
import com.tkmybatis.plugin.entity.Country;
import com.tkmybatis.plugin.mapper.CountryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @author starBlues
 * @version 1.0
 */
@org.springframework.stereotype.Service
@Transactional
public class Service {


    @Autowired
    private CountryMapper countryMapper;

    public List<Country> getAll(){
        PageHelper.startPage(1, 1);
        return countryMapper.selectAll();
    }

    @Transactional
    public Country getOne(){
        Country country = new Country();
        country.setId(1);
        return countryMapper.selectOne(country);
    }

    public List<Country> getXmlAll(){
        return countryMapper.getAll();
    }

}
