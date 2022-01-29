/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.integration.user;

import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.core.launcher.plugin.involved.PluginApplicationContextGetter;
import com.gitee.starblues.spring.ApplicationContext;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.SpringBeanUtils;
import com.gitee.starblues.utils.SpringBeanCustomUtils;
import org.springframework.context.support.GenericApplicationContext;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * 默认插件使用者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginUser implements PluginUser{

    protected final GenericApplicationContext parentApplicationContext;

    public DefaultPluginUser(GenericApplicationContext parentApplicationContext) {
        Objects.requireNonNull(parentApplicationContext, "ApplicationContext can't be null");
        this.parentApplicationContext = parentApplicationContext;
    }

    @Override
    public BeanWrapper<Set<String>> getBeanName(boolean includeMainBeans) {
        Set<String> mainBeanSet = new HashSet<>();
        if(includeMainBeans){
            mainBeanSet = SpringBeanUtils.getBeanName(parentApplicationContext);
        }
        Map<String, ApplicationContext> applicationContexts = PluginApplicationContextGetter.get();
        Map<String, Set<String>> pluginBeanNames = new HashMap<>(applicationContexts.size());
        applicationContexts.forEach((k,v)->{
            pluginBeanNames.put(k, SpringBeanCustomUtils.getBeanName(v));
        });
        return new BeanWrapper<>(mainBeanSet, pluginBeanNames);
    }

    @Override
    public Set<String> getBeanName(String pluginId) {
        return null;
    }

    @Override
    public BeanWrapper<Object> getBean(String name, boolean includeMainBeans){
        Object mainBean = null;
        if(includeMainBeans){
            mainBean = SpringBeanUtils.getExistBean(parentApplicationContext, name);
        }
        Map<String, ApplicationContext> applicationContexts = PluginApplicationContextGetter.get();
        Map<String, Object> pluginBeans = new HashMap<>(applicationContexts.size());
        applicationContexts.forEach((k,v)->{
            Object existBean = SpringBeanCustomUtils.getExistBean(v, name);
            if(existBean != null){
                pluginBeans.put(k, v);
            }
        });
        return new BeanWrapper<>(mainBean, pluginBeans);
    }

    @Override
    public Object getBean(String pluginId, String name) {
        ApplicationContext applicationContext = PluginApplicationContextGetter.get(pluginId);
        if(applicationContext == null){
            return null;
        }
        return SpringBeanCustomUtils.getExistBean(applicationContext, name);
    }

    @Override
    public <T> BeanWrapper<List<T>> getBeanByInterface(Class<T> interfaceClass, boolean includeMainBeans) {
        checkInterface(interfaceClass);
        List<T> mainBeans = new ArrayList<>();
        if(includeMainBeans){
            mainBeans = SpringBeanUtils.getBeans(parentApplicationContext, interfaceClass);
        }
        Map<String, ApplicationContext> applicationContexts = PluginApplicationContextGetter.get();
        Map<String, List<T>> pluginBeans = new HashMap<>(applicationContexts.size());
        applicationContexts.forEach((k,v)->{
            List<T> beans = SpringBeanCustomUtils.getBeans(v, interfaceClass);
            if(!ObjectUtils.isEmpty(beans)){
                pluginBeans.put(k, beans);
            }
        });
        return new BeanWrapper<>(mainBeans, pluginBeans);
    }

    @Override
    public <T> List<T> getBeanByInterface(String pluginId, Class<T> interfaceClass) {
        checkInterface(interfaceClass);
        List<T> result = new ArrayList<>();
        ApplicationContext applicationContext = PluginApplicationContextGetter.get(pluginId);
        if(applicationContext != null){
            result.addAll(SpringBeanCustomUtils.getBeans(applicationContext, interfaceClass));
        }
        return result;
    }

    @Override
    public BeanWrapper<List<Object>> getBeansWithAnnotation(Class<? extends Annotation> annotationType,
                                                            boolean includeMainBeans) {
        List<Object> mainBeans = new ArrayList<>();
        if(includeMainBeans){
            mainBeans = SpringBeanUtils.getBeansWithAnnotation(parentApplicationContext, annotationType);
        }
        Map<String, ApplicationContext> applicationContexts = PluginApplicationContextGetter.get();
        Map<String, List<Object>> pluginBeans = new HashMap<>(applicationContexts.size());
        applicationContexts.forEach((k,v)->{
            List<Object> beans = SpringBeanCustomUtils.getBeansWithAnnotation(v, annotationType);
            if(!ObjectUtils.isEmpty(beans)){
                pluginBeans.put(k, beans);
            }
        });
        return new BeanWrapper<>(mainBeans, pluginBeans);
    }


    @Override
    public List<Object> getBeansWithAnnotation(String pluginId, Class<? extends Annotation> annotationType) {
        ApplicationContext applicationContext = PluginApplicationContextGetter.get(pluginId);
        if(applicationContext != null){
            return SpringBeanCustomUtils.getBeansWithAnnotation(applicationContext, annotationType);
        }
        return new ArrayList<>(0);
    }

    /**
     * 判断clazz是否是接口
     * @param clazz clazz
     */
    private void checkInterface(Class<?> clazz) {
        if (clazz.isInterface()) {
            return;
        }
        throw new PluginException("[" + clazz.getName() + "]不是一个接口");
    }

}
