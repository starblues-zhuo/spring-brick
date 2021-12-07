package com.gitee.starblues.integration.user;

import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author starBlues
 * @version 1.0
 */
public class EmptyPluginUser implements PluginUser{
    @Override
    public <T> T getBean(String name) {
        return null;
    }

    @Override
    public <T> T getBean(Class<T> aClass) {
        return null;
    }

    @Override
    public <T> T getPluginBean(String name) {
        return null;
    }

    @Override
    public <T> List<T> getBeans(Class<T> aClass) {
        return null;
    }

    @Override
    public <T> List<T> getMainBeans(Class<T> aClass) {
        return null;
    }

    @Override
    public <T> List<T> getPluginBeans(Class<T> aClass) {
        return null;
    }

    @Override
    public <T> List<T> getPluginBeans(String pluginId, Class<T> aClass) {
        return null;
    }

    @Override
    public List<Object> getPluginBeansWithAnnotation(Class<? extends Annotation> annotationType) {
        return null;
    }

    @Override
    public List<Object> getPluginBeansWithAnnotation(String pluginId, Class<? extends Annotation> annotationType) {
        return null;
    }

    @Override
    public <T> T generateNewInstance(T object) {
        return null;
    }
}
