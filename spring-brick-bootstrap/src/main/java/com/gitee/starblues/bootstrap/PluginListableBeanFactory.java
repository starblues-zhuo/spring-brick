/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.utils.DestroyUtils;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.SpringBeanFactory;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.lang.Nullable;

import java.util.Set;

/**
 * 插件BeanFactory实现
 * @author starBlues
 * @version 3.0.0
 */
public class PluginListableBeanFactory extends DefaultListableBeanFactory {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final MainApplicationContext applicationContext;

    public PluginListableBeanFactory(MainApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object resolveDependency(DependencyDescriptor descriptor,
                                    @Nullable String requestingBeanName,
                                    @Nullable Set<String> autowiredBeanNames,
                                    @Nullable TypeConverter typeConverter) throws BeansException {

        try {
            return super.resolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
        } catch (BeansException e){
            return resolveDependencyFromMain(descriptor);
        }
    }

    private Object resolveDependencyFromMain(DependencyDescriptor descriptor){
        String dependencyName = descriptor.getDependencyName();
        SpringBeanFactory springBeanFactory = applicationContext.getSpringBeanFactory();
        if(!ObjectUtils.isEmpty(dependencyName) && springBeanFactory.containsBean(dependencyName)){
            return springBeanFactory.getBean(dependencyName);
        } else {
            try {
                return springBeanFactory.getBean(descriptor.getDependencyType());
            } catch (Exception e){
                throw new NoSuchBeanDefinitionException(descriptor.getDependencyType());
            }
        }
    }

    @Override
    public void destroySingletons() {
        String[] beanDefinitionNames = getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            destroyBean(beanDefinitionName);
        }
        super.destroySingletons();
        destroyAll();
    }

    private void destroyAll(){
        ReflectionUtils.findField(this.getClass(), field -> {
            field.setAccessible(true);
            try {
                Object o = field.get(this);
                DestroyUtils.destroyAll(o);
            } catch (IllegalAccessException e) {
                // 忽略
            }
            return false;
        });
    }

}
