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

package com.gitee.starblues.bootstrap.processor.web;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.ProcessorException;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.bootstrap.processor.interceptor.PluginInterceptorRegister;
import com.gitee.starblues.bootstrap.processor.interceptor.PluginInterceptorRegistry;
import com.gitee.starblues.bootstrap.utils.SpringBeanUtils;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.utils.ClassUtils;
import com.gitee.starblues.utils.OrderUtils;
import com.gitee.starblues.utils.PluginConfigUtils;
import com.gitee.starblues.utils.SpringBeanCustomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件拦截器处理者
 * @author starBlues
 * @version 3.0.0
 */
public class PluginInterceptorsProcessor implements SpringPluginProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String INTERCEPTORS = "pluginHandlerInterceptors";

    private AbstractHandlerMapping handlerMapping;


    @Override
    public void initialize(ProcessorContext context) throws ProcessorException {
        MainApplicationContext applicationContext = context.getMainApplicationContext();
        handlerMapping = SpringBeanCustomUtils.getExistBean(applicationContext,
                AbstractHandlerMapping.class);
        if(handlerMapping == null){
            logger.warn("Not found AbstractHandlerMapping, Plugin interceptor can't use");
        }
    }

    @Override
    public void refreshAfter(ProcessorContext context) throws ProcessorException {
        if(handlerMapping == null){
            return;
        }
        List<PluginInterceptorRegister> interceptorRegisters = SpringBeanUtils.getBeans(
                context.getApplicationContext(),
                PluginInterceptorRegister.class);
        List<HandlerInterceptor> interceptorsObjects = new ArrayList<>();
        List<HandlerInterceptor> adaptedInterceptors = getAdaptedInterceptors();
        if(adaptedInterceptors == null){
            return;
        }
        IntegrationConfiguration configuration = context.getConfiguration();
        String pluginId = context.getPluginDescriptor().getPluginId();
        String pluginRestPrefix = PluginConfigUtils.getPluginRestPrefix(configuration, pluginId);

        for (PluginInterceptorRegister interceptorRegister : interceptorRegisters) {
            PluginInterceptorRegistry interceptorRegistry = new PluginInterceptorRegistry(pluginRestPrefix);
            interceptorRegister.registry(interceptorRegistry);
            List<Object> interceptors = interceptorRegistry.getInterceptors();
            if(interceptors == null || interceptors.isEmpty()){
                continue;
            }
            for (Object interceptor : interceptors) {
                HandlerInterceptor handlerInterceptor = adaptInterceptor(interceptor);
                adaptedInterceptors.add(handlerInterceptor);
                interceptorsObjects.add(handlerInterceptor);
            }
        }
        context.addRegistryInfo(INTERCEPTORS, interceptorsObjects);
    }

    @Override
    public void close(ProcessorContext context) throws ProcessorException {
        if(handlerMapping == null){
            return;
        }
        List<HandlerInterceptor> interceptorsObjects = context.getRegistryInfo(INTERCEPTORS);
        if(interceptorsObjects == null || interceptorsObjects.isEmpty()){
            return;
        }
        List<HandlerInterceptor> adaptedInterceptors = getAdaptedInterceptors();
        if(adaptedInterceptors == null){
            return;
        }
        for (HandlerInterceptor interceptor : interceptorsObjects) {
            adaptedInterceptors.remove(interceptor);
        }
    }

    @Override
    public ProcessorContext.RunMode runMode() {
        return ProcessorContext.RunMode.PLUGIN;
    }

    /**
     * 得到拦截器存储者
     * @return List<HandlerInterceptor>
     */
    private List<HandlerInterceptor> getAdaptedInterceptors(){
        try {
            return ClassUtils.getReflectionField(handlerMapping, "adaptedInterceptors", List.class);
        } catch (IllegalAccessException e) {
            logger.error("Can't get 'adaptedInterceptors' from AbstractHandlerMapping, so " +
                    "You can't use HandlerInterceptor. {} ", e.getMessage());
            return null;
        }
    }

    /**
     * 转换拦截器
     * @param interceptor interceptor
     * @return HandlerInterceptor
     */
    private HandlerInterceptor adaptInterceptor(Object interceptor) {
        if (interceptor instanceof HandlerInterceptor) {
            return (HandlerInterceptor) interceptor;
        } else if (interceptor instanceof WebRequestInterceptor) {
            return new WebRequestHandlerInterceptorAdapter((WebRequestInterceptor) interceptor);
        } else {
            throw new IllegalArgumentException("Interceptor type not supported: " + interceptor.getClass().getName());
        }
    }
}
