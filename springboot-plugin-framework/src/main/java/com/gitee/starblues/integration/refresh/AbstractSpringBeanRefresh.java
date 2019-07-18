package com.gitee.starblues.integration.refresh;

import com.gitee.starblues.factory.FactoryType;
import com.gitee.starblues.factory.MainFactoryType;
import com.gitee.starblues.factory.PluginListener;
import com.gitee.starblues.integration.PluginApplication;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Objects;

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
    public void registryEvent(FactoryType factoryType, String pluginId) throws Exception {
        if(Objects.equals(MainFactoryType.OVERALL.getKey(), factoryType.getKey())){
            this.beans = refresh();
            registryEvent(beans);
        }
    }

    @Override
    public void unRegistryEvent(FactoryType factoryType, String pluginId) throws Exception {
        if(Objects.equals(MainFactoryType.OVERALL.getKey(), factoryType.getKey())){
            this.beans = refresh();
            unRegistryEvent(beans);
        }
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
