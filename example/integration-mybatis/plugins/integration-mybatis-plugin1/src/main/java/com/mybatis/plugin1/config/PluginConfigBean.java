package com.mybatis.plugin1.config;

import com.gitee.starblues.realize.ConfigBean;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginConfigBean implements ConfigBean {

    private final Plugin1Config plugin1Config;

    public PluginConfigBean(Plugin1Config plugin1Config) {
        this.plugin1Config = plugin1Config;
    }

    @Override
    public void initialize() throws Exception {
        System.out.println("初始化Bean");
        System.out.println(plugin1Config);
    }

    @Override
    public void destroy() throws Exception {
        System.out.println("销毁Bean");
        System.out.println(plugin1Config);
    }
}
