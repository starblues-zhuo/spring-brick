package com.plugin.development.context.refresh;

import com.plugin.development.context.PluginSpringBeanListener;
import com.plugin.development.integration.PluginApplication;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * 抽象的SpringBean刷新类监听类.
 * 继承该类。在插件动态的注册卸载时, refresh方法被触发, 可以获取到当前环境所有T实现的所有beans(包括主程序中的beans)
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractSpringBeanRefresh<T> implements PluginSpringBeanListener {

    private List<T> beans;

    protected final Class<T> typeClass;
    protected final PluginApplication pluginApplication;


    public AbstractSpringBeanRefresh(PluginApplication pluginApplication) {
        this.pluginApplication = pluginApplication;
        pluginApplication.addListener(this);
        this.typeClass  = (Class<T>)((ParameterizedType)getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    @Override
    public void registryEvent(String pluginId) throws Exception {
        this.beans = refresh();
        registryEvent(beans);
    }

    @Override
    public void unRegistryEvent(String pluginId) throws Exception{
        this.beans = refresh();
        unRegistryEvent(beans);
    }

    /**
     * 注册事件
     * @param beans 当前所有实现的bean
     */
    protected void registryEvent(List<T> beans){

    }

    /**
     * 卸载事件
     * @param beans 当前卸载后所有的beans
     */
    protected void unRegistryEvent(List<T> beans){

    }

    /**
     * 刷新bean
     */
    protected List<T> refresh(){
        return pluginApplication
                .getPluginUser()
                .getPluginSpringDefineBeansOfType(typeClass);
    }


    /**
     * 得到beans
     * @return beansMap
     */
    public List<T> getBeans() {
        return beans;
    }
}
