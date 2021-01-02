package com.mybatisplus.plugin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mybatisplus.plugin.entity.PluginData;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
@Mapper
public interface PluginDataMapper extends BaseMapper<PluginData> {

    List<PluginData> getAll();

    List<PluginData> getTestAll();

    PluginData getTestOne();
}
