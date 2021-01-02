package com.mybatisplus.plugin.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.mybatisplus.plugin.entity.PluginData;

import java.util.List;

/**
 * description
 *
 * @author starBlues
 * @version 1.0
 */
public interface PluginDataService extends IService<PluginData> {

    /**
     * 通过名称查询
     * @param name
     * @return
     */
    List<PluginData> getByName(String name);

    /**
     * 分页查询
     * @param size
     * @param currentPage
     * @return
     */
    List<PluginData> getByPage(Long size, Long currentPage);

    List<PluginData> getTestAll();
    PluginData getTestOne();
}
