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
import com.gitee.starblues.bootstrap.utils.AnnotationUtils;
import com.gitee.starblues.bootstrap.utils.DestroyUtils;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.SpringBeanFactory;
import com.gitee.starblues.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 插件Controller处理者
 * @author starBlues
 * @version 3.0.0
 */
public class PluginControllerProcessor implements SpringPluginProcessor {

    private final static Logger LOG = LoggerFactory.getLogger(PluginControllerProcessor.class);

    static final String PROCESS_CONTROLLERS = "PROCESS_SUCCESS";


    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    private Method getMappingForMethod;
    private RequestMappingHandlerAdapter handlerAdapter;

    private final AtomicBoolean canRegistered = new AtomicBoolean(false);


    @Override
    public void initialize(ProcessorContext processorContext) throws ProcessorException {
        SpringBeanFactory mainBeanFactory = processorContext.getMainBeanFactory();
        this.requestMappingHandlerMapping = mainBeanFactory.getBean(RequestMappingHandlerMapping.class);
        this.handlerAdapter = SpringBeanCustomUtils.getExistBean(processorContext.getMainApplicationContext(),
                RequestMappingHandlerAdapter.class);
        this.getMappingForMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class,
                "getMappingForMethod", Method.class, Class.class);
        if(getMappingForMethod == null){
            LOG.warn("RequestMappingHandlerMapping 类中没有发现 <getMappingForMethod> 方法, 无法注册插件接口. " +
                    "请检查当前环境是否为 web 环境");
        }
        this.getMappingForMethod.setAccessible(true);
        canRegistered.set(true);
    }


    @Override
    public void refreshBefore(ProcessorContext processorContext) throws ProcessorException {
        if(!canRegistered.get()){
            return;
        }
        GenericApplicationContext applicationContext = processorContext.getApplicationContext();
        applicationContext.registerBean("changeRestPathPostProcessor",
                ChangeRestPathPostProcessor.class, ()-> new ChangeRestPathPostProcessor(processorContext));
    }

    @Override
    public void refreshAfter(ProcessorContext processorContext) throws ProcessorException {
        if(!canRegistered.get()){
            return;
        }
        IntegrationConfiguration configuration = processorContext.getConfiguration();
        if(ObjectUtils.isEmpty(configuration.pluginRestPathPrefix())
                && !configuration.enablePluginIdRestPathPrefix()){
            // 如果 pluginRestPathPrefix 为空, 并且没有启用插件id作为插件前缀, 则不进行修改插件controller地址前缀
            return;
        }
        String pluginId = processorContext.getPluginDescriptor().getPluginId();
        List<ControllerWrapper> controllerWrappers = processorContext.getRegistryInfo(PROCESS_CONTROLLERS);
        if(ObjectUtils.isEmpty(controllerWrappers)){
            return;
        }
        GenericApplicationContext applicationContext = processorContext.getApplicationContext();

        Iterator<ControllerWrapper> iterator = controllerWrappers.iterator();
        while (iterator.hasNext()){
            ControllerWrapper controllerWrapper = iterator.next();
            if(!applicationContext.containsBean(controllerWrapper.getBeanName())){
                iterator.remove();
            }
            Object controllerBean = applicationContext.getBean(controllerWrapper.getBeanName());
            Set<RequestMappingInfo> requestMappingInfos = registry(applicationContext, controllerBean.getClass());
            if(requestMappingInfos.isEmpty()){
                iterator.remove();
            } else {
                for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
                    LOG.info("插件[{}]注册接口: {}", pluginId, requestMappingInfo.toString());
                }
                controllerWrapper.setRequestMappingInfos(requestMappingInfos);
            }
        }
    }

    @Override
    public void close(ProcessorContext context) throws ProcessorException {
        List<ControllerWrapper> controllerWrappers = context.getRegistryInfo(PROCESS_CONTROLLERS);
        if(ObjectUtils.isEmpty(controllerWrappers)){
            return;
        }
        for (ControllerWrapper controllerWrapper : controllerWrappers) {
            unregister(controllerWrapper);
        }
        controllerWrappers.clear();
    }

    @Override
    public ProcessorContext.RunMode runMode() {
        return ProcessorContext.RunMode.PLUGIN;
    }


    private Set<RequestMappingInfo> registry(GenericApplicationContext pluginApplicationContext, Class<?> aClass)
            throws ProcessorException {
        Object object = pluginApplicationContext.getBean(aClass);

        Method[] methods = aClass.getDeclaredMethods();
        Set<RequestMappingInfo> requestMappingInfos = new HashSet<>();
        for (Method method : methods) {
            if (isHaveRequestMapping(method)) {
                try {
                    RequestMappingInfo requestMappingInfo = (RequestMappingInfo)
                            getMappingForMethod.invoke(requestMappingHandlerMapping, method, aClass);
                    requestMappingHandlerMapping.registerMapping(requestMappingInfo, object, method);
                    requestMappingInfos.add(requestMappingInfo);
                } catch (Exception e){
                    throw new ProcessorException(e.getMessage());
                }
            }
        }
        return requestMappingInfos;
    }

    /**
     * 卸载具体的Controller操作
     * @param controllerBeanWrapper controllerBean包装
     */
    private void unregister(ControllerWrapper controllerBeanWrapper) {
        Set<RequestMappingInfo> requestMappingInfos = controllerBeanWrapper.getRequestMappingInfos();
        if(requestMappingInfos != null && !requestMappingInfos.isEmpty()){
            for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
                requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
            }
        }
        if(handlerAdapter != null){
            Class<?> beanClass = controllerBeanWrapper.getBeanClass();
            DestroyUtils.destroyValue(handlerAdapter, "sessionAttributesHandlerCache", beanClass);
            DestroyUtils.destroyValue(handlerAdapter, "initBinderCache", beanClass);
            DestroyUtils.destroyValue(handlerAdapter, "modelAttributeCache", beanClass);
        }
    }

    /**
     * 方法上是否存在 @RequestMapping 注解
     * @param method method
     * @return boolean
     */
    private boolean isHaveRequestMapping(Method method){
        return AnnotationUtils.findAnnotation(method, RequestMapping.class) != null;
    }

    private static class ChangeRestPathPostProcessor implements BeanPostProcessor {

        private final static Logger LOG = LoggerFactory.getLogger(ChangeRestPathPostProcessor.class);
        private final static String COMMON_ERROR = "无法统一处理该 Controller 统一请求路径前缀";

        private final ProcessorContext processorContext;

        private ChangeRestPathPostProcessor(ProcessorContext processorContext) {
            this.processorContext = processorContext;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            Class<?> aClass = bean.getClass();
            RequestMapping requestMapping = AnnotationUtils.findAnnotation(aClass, RequestMapping.class);
            boolean isController = AnnotationUtils.existOr(aClass, new Class[]{
                Controller.class, RestController.class
            });
            if(requestMapping != null && isController){
                changePathForClass(beanName, aClass, requestMapping);
            }
            return bean;
        }

        private void changePathForClass(String beanName, Class<?> aClass, RequestMapping requestMapping){
            String pluginId = processorContext.getPluginDescriptor().getPluginId();
            IntegrationConfiguration configuration = processorContext.getConfiguration();
            String pathPrefix = PluginConfigUtils.getPluginRestPrefix(configuration, pluginId);

            if(ObjectUtils.isEmpty(pathPrefix)){
                LOG.error("插件 [{}] Controller类 [{}] 未发现 path 配置, {}",
                        pluginId, aClass.getSimpleName(), COMMON_ERROR);
                return;
            }
            Set<String> definePaths = new HashSet<>();
            definePaths.addAll(Arrays.asList(requestMapping.path()));
            definePaths.addAll(Arrays.asList(requestMapping.value()));
            try {
                Map<String, Object> memberValues = ClassUtils.getAnnotationsUpdater(requestMapping);
                if(memberValues == null){
                    LOG.error("插件 [{}] Controller 类 [{}] 无法反射获取注解属性, {}",
                            pluginId, aClass.getSimpleName(), COMMON_ERROR);
                    return;
                }
                String[] newPath = new String[definePaths.size()];
                int i = 0;
                for (String definePath : definePaths) {
                    // 解决插件启用、禁用后, 路径前缀重复的问题。
                    if(definePath.contains(pathPrefix)){
                        newPath[i++] = definePath;
                    } else {
                        newPath[i++] = FilesUtils.restJoiningPath(pathPrefix, definePath);
                    }
                }
                if(newPath.length == 0){
                    newPath = new String[]{ pathPrefix };
                }
                memberValues.put("path", newPath);
                memberValues.put("value", newPath);

                List<ControllerWrapper> controllerWrappers = processorContext.getRegistryInfo(PROCESS_CONTROLLERS);
                if(controllerWrappers == null){
                    controllerWrappers = new ArrayList<>();
                    processorContext.addRegistryInfo(PROCESS_CONTROLLERS, controllerWrappers);
                }
                ControllerWrapper controllerWrapper = new ControllerWrapper();
                controllerWrapper.setPathPrefix(newPath);
                controllerWrapper.setBeanName(beanName);
                controllerWrapper.setBeanClass(aClass);
                controllerWrappers.add(controllerWrapper);
            } catch (Exception e) {
                LOG.error("插件 [{}] Controller 类[{}] 注册异常. {}", pluginId, aClass.getName(), e.getMessage(), e);
            }
        }
    }


    static class ControllerWrapper{

        /**
         * controller bean 名称
         */
        private String beanName;

        /**
         * controller 路径前缀
         */
        private String[] pathPrefix;

        /**
         * controller bean 类型
         */
        private Class<?> beanClass;

        /**
         * controller 的 RequestMappingInfo 集合
         */
        private Set<RequestMappingInfo> requestMappingInfos;

        public Class<?> getBeanClass() {
            return beanClass;
        }

        public void setBeanClass(Class<?> beanClass) {
            this.beanClass = beanClass;
        }

        public String getBeanName() {
            return beanName;
        }

        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }

        public String[] getPathPrefix() {
            return pathPrefix;
        }

        public void setPathPrefix(String[] pathPrefix) {
            this.pathPrefix = pathPrefix;
        }

        public Set<RequestMappingInfo> getRequestMappingInfos() {
            return requestMappingInfos;
        }

        public void setRequestMappingInfos(Set<RequestMappingInfo> requestMappingInfos) {
            this.requestMappingInfos = requestMappingInfos;
        }
    }

}
