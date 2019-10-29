package com.gitee.starblues.integration.refresh;

import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.listener.PluginListener;

import java.lang.reflect.ParameterizedType;
import java.util.List;

/**
 * 抽象的SpringBean刷新类监听类.
 * 继承该类。在插件动态的注册卸载时, refresh方法被触发, 可以获取到当前环境所有T实现的所有beans(包括主程序中的beans)
 *
 * @author zhangzhuo
 * @version 2.0.2
 */
public abstract class AbstractSpringBeanRefresh<T> implements PluginListener {

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
    public void registry(String pluginId) {
        this.beans = refresh();
        registryEvent(beans);
    }

    @Override
    public void unRegistry(String pluginId)  {
        this.beans = refresh();
        unRegistryEvent(beans);
    }

    @Override
    public void failure(String pluginId, Throwable throwable) {

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
     * @return 返回刷新后的Bean集合
     */
    protected List<T> refresh(){
        return pluginApplication
                .getPluginUser()
                .getBeans(typeClass);
    }


    /**
     * 得到beans
     * @return beansMap
     */
    public List<T> getBeans() {
        return beans;
    }
}
