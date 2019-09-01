package com.mybatisplus.plugin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.gitee.starblues.extension.mybatis.annotation.PluginMapper;
import com.mybatisplus.plugin.entity.PluginData;

import java.util.List;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
@PluginMapper
public interface PluginDataMapper extends BaseMapper<PluginData> {

    List<PluginData> getAll();

}
