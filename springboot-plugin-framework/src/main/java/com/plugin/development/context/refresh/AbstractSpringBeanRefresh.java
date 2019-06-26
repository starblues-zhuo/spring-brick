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

    private final Class<T> typeClass;

    private List<T> beans;

    private final PluginApplication pluginApplication;


    public AbstractSpringBeanRefresh(PluginApplication pluginApplication) {
        this.pluginApplication = pluginApplication;
        pluginApplication.addListener(this);
        this.typeClass  = (Class<T>)((ParameterizedType)getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
    }

    @Override
    public void registryEvent(String pluginId) throws Exception {
        refresh();
    }

    @Override
    public void unRegistryEvent(String pluginId) throws Exception {
        refresh();
    }


    private void refresh(){
        List<T> beans = pluginApplication
                .getPluginUser()
                .getSpringDefineBeansOfType(typeClass);
        if(beans != null){
            this.beans = refresh(beans);
        } else {
            this.beans = beans;
        }
    }

    /**
     * 刷新beans的操作
     * @param beans 当前 对 T 的实现类的所有beans(包括主程序中的beans)
     * @return 操作后的beans
     */
    protected abstract List<T> refresh(List<T> beans);

    /**
     * 得到beans
     * @return beansMap
     */
    public List<T> getBeans() {
        return beans;
    }
}
