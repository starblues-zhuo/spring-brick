package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.exception.PluginBeanFactoryException;
import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.factory.NoticePluginFactory;
import com.gitee.starblues.factory.bean.register.PluginBeanRegister;
import com.gitee.starblues.loader.PluginResourceLoader;
import com.gitee.starblues.realize.BasePlugin;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * spring boot mybatis 扩展
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class SpringBootMybatisExtension extends AbstractExtension {

    public static final String KEY = "SpringBootMybatisExtension";


    @Override
    public String key() {
        return KEY;
    }

    @Override
    public List<PluginResourceLoader> getPluginResourceLoader() {
        final List<PluginResourceLoader> pluginResourceLoaders = new ArrayList<>();
        pluginResourceLoaders.add(new PluginMybatisXmlLoader());
        return pluginResourceLoaders;
    }

    @Override
    public List<NoticePluginFactory> getNoticePluginFactory(ApplicationContext mainApplicationContext) {
        final List<NoticePluginFactory> pluginFactoryList = new ArrayList<>();
        pluginFactoryList.add(new NoticePluginFactory(new PluginMybatisXmlFactory(mainApplicationContext)));
        return pluginFactoryList;
    }

    @Override
    public List<PluginBeanRegister> getPluginBeanRegister(ApplicationContext mainApplicationContext) {
        try {
            final List<PluginBeanRegister> pluginBeanRegisters = new ArrayList<>();
            pluginBeanRegisters.add(new PluginMybatisMapperRegister(mainApplicationContext));
            return pluginBeanRegisters;
        } catch (PluginBeanFactoryException e) {
            e.printStackTrace();
            return super.getPluginBeanRegister(mainApplicationContext);
        }
    }
}
