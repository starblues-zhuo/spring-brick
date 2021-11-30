package com.gitee.starblues.realize;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.springframework.context.ApplicationContext;

import javax.annotation.Resource;
import java.util.List;

/**
 * 插件工具类
 * @author starBlues
 * @version 2.4.0
 */
public class PluginUtils {

    protected final ApplicationContext mainApplicationContext;
    protected ApplicationContext pluginApplicationContext;
    protected PluginDescriptor pluginDescriptor;

    @Deprecated
    public PluginUtils(ApplicationContext mainApplicationContext,
                       ApplicationContext pluginApplicationContext,
                       PluginDescriptor pluginDescriptor) {
        this.mainApplicationContext = mainApplicationContext;
        this.pluginApplicationContext = pluginApplicationContext;
        this.pluginDescriptor = pluginDescriptor;
    }

    public PluginUtils(ApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
        this.pluginApplicationContext = null;
        this.pluginDescriptor = null;
    }


    /**
     * 获取主程序的 ApplicationContext
     * @return ApplicationContext
     */
    public ApplicationContext getMainApplicationContext() {
        return mainApplicationContext;
    }

    /**
     * 获取当前插件的 ApplicationContext
     * @return ApplicationContext
     */
    @Deprecated
    public ApplicationContext getPluginApplicationContext() {
        return pluginApplicationContext;
    }

    /**
     * 获取当前插件的描述信息
     * @return PluginDescriptor
     */
    @Deprecated
    public PluginDescriptor getPluginDescriptor(){
        return pluginDescriptor;
    }


    /**
     * 通过 bean名称得到主程序中的bean
     * @param name bean 名称
     * @param <T> bean 类型
     * @return bean
     */
    public <T> T getMainBean(String name){
        Object bean = mainApplicationContext.getBean(name);
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
        return mainApplicationContext.getBean(aClass);
    }

    /**
     * 通过接口或者抽象类类型得到主程序中的多个实现对象
     * @param aClass bean 类型
     * @param <T> bean 类型
     * @return bean
     */
    public <T> List<T> getMainBeans(Class<T> aClass){
        return SpringBeanUtils.getBeans(mainApplicationContext, aClass);
    }

}
