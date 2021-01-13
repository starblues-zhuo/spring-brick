package com.mybatisplus.plugin.service;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.gitee.starblues.extension.mybatis.mybatisplus.ServiceImplWrapper;
import com.mybatisplus.plugin.entity.PluginData;
import com.mybatisplus.plugin.mapper.PluginDataMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
@Component
public class PluginDataServiceImpl extends ServiceImpl<PluginDataMapper, PluginData>
        implements PluginDataService{
    @Override
    public List<PluginData> getByName(String name){
        Wrapper<PluginData> wrapper = Wrappers.<PluginData>query()
                .eq("name", name);
        return list(wrapper);
    }

    @Override
    public List<PluginData> getByPage(Long size, Long currentPage) {
        Page<PluginData> page = new Page<>();
        page.setSize(size);
        page.setCurrent(currentPage);
        IPage<PluginData> page1 = page(page);
        return page1.getRecords();
    }

    @Override
    public List<PluginData> getTestAll() {
        return baseMapper.getTestAll();
    }

    @Override
    public PluginData getTestOne() {
        return baseMapper.getTestOne();
    }

}
