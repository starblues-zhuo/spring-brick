package com.gitee.starblues.realize;

import com.gitee.starblues.integration.operator.module.PluginInfo;
import com.gitee.starblues.utils.PluginBeanUtils;
import org.pf4j.PluginDescriptor;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * 插件工具类
 * @author starBlues
 * @version 1.0
 */
public class PluginUtils {

    protected final ApplicationContext parentApplicationContext;
    protected final ApplicationContext pluginApplicationContext;
    protected final PluginDescriptor pluginDescriptor;

    public PluginUtils(ApplicationContext parentApplicationContext,
                       ApplicationContext pluginApplicationContext,
                       PluginDescriptor pluginDescriptor) {
        this.parentApplicationContext = parentApplicationContext;
        this.pluginApplicationContext = pluginApplicationContext;
        this.pluginDescriptor = pluginDescriptor;
    }

    /**
     * 获取主程序的 ApplicationContext
     * @return ApplicationContext
     */
    public ApplicationContext getMainApplicationContext() {
        return parentApplicationContext;
    }

    /**
     * 获取当前插件的 ApplicationContext
     * @return ApplicationContext
     */
    public ApplicationContext getPluginApplicationContext() {
        return pluginApplicationContext;
    }

    /**
     * 获取当前插件的描述信息
     * @return PluginDescriptor
     */
    public PluginDescriptor getPluginDescriptor(){
        return pluginDescriptor;
    }


    /**
     * 获取bean名称得到主程序中的bean
     * @param name bean 名称
     * @param <T> bean 类型
     * @return bean
     */
    public <T> T getMainBean(String name){
        Object bean = parentApplicationContext.getBean(name);
        if(bean == null){
            return null;
        }
        return (T) bean;
    }

    /**
     * 通过bean类型得到主程序中的bean
     * @param aClass bean 类型
     * @param <T> bean 类型
     * @return bean
     */
    public <T> T getMainBean(Class<T> aClass) {
        return parentApplicationContext.getBean(aClass);
    }

    /**
     * 通过接口或者抽象类型得到主程序中的多个实现类型
     * @param aClass bean 类型
     * @param <T> bean 类型
     * @return bean
     */
    public <T> List<T> getMainBeans(Class<T> aClass){
        return PluginBeanUtils.getPluginBeans(parentApplicationContext, aClass);
    }

}
