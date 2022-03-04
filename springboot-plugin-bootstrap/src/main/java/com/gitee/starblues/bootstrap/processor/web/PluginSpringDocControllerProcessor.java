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
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.ReflectionUtils;
import com.gitee.starblues.utils.SpringBeanCustomUtils;
import io.swagger.v3.oas.models.OpenAPI;
import org.springdoc.api.AbstractOpenApiResource;
import org.springdoc.core.OpenAPIService;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * spring doc
 * @author starBlues
 * @version 3.0.0
 */
public class PluginSpringDocControllerProcessor implements SpringPluginProcessor {

    static final String CONTROLLER_API_CLASS = "controller_api_class";

    private OpenAPIService openApiService;
    private List<Class<?>> restControllers;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(ProcessorContext context) throws ProcessorException {

        try {
            MainApplicationContext mainApplicationContext = context.getMainApplicationContext();
            openApiService = SpringBeanCustomUtils.getExistBean(mainApplicationContext, OpenAPIService.class);
            AbstractOpenApiResource openApiResource = SpringBeanCustomUtils.getExistBean(mainApplicationContext,
                    AbstractOpenApiResource.class);
            if(openApiResource == null){
                return;
            }
            restControllers = (List<Class<?>>) ReflectionUtils.getField(null, openApiResource.getClass(),
                    "ADDITIONAL_REST_CONTROLLERS", List.class);
        } catch (Throwable e) {
            restControllers = null;
        }
    }

    @Override
    public void refreshAfter(ProcessorContext context) throws ProcessorException {
        if(restControllers == null){
            return;
        }
        List<PluginControllerProcessor.ControllerWrapper> controllerWrappers = context.getRegistryInfo(
                PluginControllerProcessor.PROCESS_CONTROLLERS);
        if(ObjectUtils.isEmpty(controllerWrappers)){
            return;
        }
        List<Class<?>> apiClass = new ArrayList<>();
        for (PluginControllerProcessor.ControllerWrapper controllerWrapper : controllerWrappers) {
            Class<?> beanClass = controllerWrapper.getBeanClass();
            restControllers.add(beanClass);
            apiClass.add(beanClass);
        }
        context.addRegistryInfo(CONTROLLER_API_CLASS, apiClass);
        refresh();
    }

    @Override
    public void close(ProcessorContext context) throws ProcessorException {
        if(restControllers == null){
            return;
        }
        List<Class<?>> apiClass = context.getRegistryInfo(CONTROLLER_API_CLASS);
        if(ObjectUtils.isEmpty(apiClass)){
            return;
        }
        try {
            for (Class<?> controllerClass : apiClass) {
                restControllers.remove(controllerClass);
            }
            refresh();
        } finally {
            apiClass.clear();
        }
    }

    private void refresh(){
        if(openApiService != null){
            try {
                // 兼容版本: 1.5.x
                Method setCachedOpenApiMethod =
                        ReflectionUtils.findMethod(openApiService.getClass(), "setCachedOpenAPI", OpenAPI.class);
                if(setCachedOpenApiMethod != null){
                    setCachedOpenApiMethod.invoke(openApiService, null);
                }
            } catch (Exception e){
                // 忽略
            }
            openApiService.resetCalculatedOpenAPI();
        }
    }


    @Override
    public ProcessorContext.RunMode runMode() {
        return ProcessorContext.RunMode.PLUGIN;
    }
}

