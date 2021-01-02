package com.tkmybatis.plugin.mapper;

import com.tkmybatis.plugin.entity.Country;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

/**
 * @author starBlues
 * @version 1.0
 * @since 2020-12-18
 */
@org.apache.ibatis.annotations.Mapper
public interface CountryMapper extends Mapper<Country> {

    List<Country> getAll();

}
