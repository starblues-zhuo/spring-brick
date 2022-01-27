package com.gitee.starblues.integration.user;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.Set;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class EmptyPluginUser implements PluginUser{


    @Override
    public BeanWrapper<Set<String>> getBeanName(boolean includeMainBeans) {
        return null;
    }

    @Override
    public Set<String> getBeanName(String pluginId) {
        return null;
    }

    @Override
    public BeanWrapper<Object> getBean(String name, boolean includeMainBeans) {
        return null;
    }

    @Override
    public Object getBean(String pluginId, String name) {
        return null;
    }

    @Override
    public <T> BeanWrapper<List<T>> getBeanByInterface(Class<T> interfaceClass, boolean includeMainBeans) {
        return null;
    }

    @Override
    public <T> List<T> getBeanByInterface(String pluginId, Class<T> interfaceClass) {
        return null;
    }

    @Override
    public BeanWrapper<List<Object>> getBeansWithAnnotation(Class<? extends Annotation> annotationType, boolean includeMainBeans) {
        return null;
    }

    @Override
    public List<Object> getBeansWithAnnotation(String pluginId, Class<? extends Annotation> annotationType) {
        return null;
    }
}
