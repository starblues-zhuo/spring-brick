package com.plugin.development.context;

import com.plugin.development.context.process.PluginPostBeanProcess;
import com.plugin.development.exception.PluginBeanFactoryException;
import com.plugin.development.context.factory.PluginBeanRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 针对 AnnotationConfigApplicationContext 处理的插件工厂管理者
 * @author zhangzhuo
 * @version 1.0
 */
public class AnnotationConfigPluginContextFactory
        extends AbstractPluginContextFactory<AnnotationConfigApplicationContext> {

    private final Logger log = LoggerFactory.getLogger(AnnotationConfigPluginContextFactory.class);
    /**
     * spring 管理的插件bean集合Map.key为插件的id
     */
    private final static Map<String, PluginSpringBean> SPRING_BEAN_MAP = new ConcurrentHashMap<>();
    private final static Lock LOCK = new ReentrantLock(true);

    /**
     * 主程序中的 ApplicationContext。不要和插件中的 ApplicationContext 混淆
     */
    private final ApplicationContext mainApplicationContext;
    private final PluginBeanRegistry<String> componentBeanRegistry;
    private final PluginBeanRegistry<Set<RequestMappingInfo>> pluginControllerBeanRegistry;

    private final List<PluginPostBeanProcess> pluginPostBeanProcess;

    public AnnotationConfigPluginContextFactory(PluginContext pluginContext) {
        Objects.requireNonNull(pluginContext);
        Objects.requireNonNull(pluginContext.getMainApplicationContext());
        this.mainApplicationContext = pluginContext.getMainApplicationContext();
        this.componentBeanRegistry = pluginContext.getComponentBeanRegistry();
        this.pluginControllerBeanRegistry = pluginContext.getControllerBeanRegistry();
        List<PluginPostBeanProcess> pluginPostBeanProcess = pluginContext.getPluginPostBeanProcess();
        if(pluginPostBeanProcess == null){
            this.pluginPostBeanProcess = new ArrayList<>();
        } else {
            this.pluginPostBeanProcess = pluginPostBeanProcess;
        }

    }


    @Override
    public void registry(String pluginId, AnnotationConfigApplicationContext applicationContext)
            throws PluginBeanFactoryException {
        log.debug("Start registry");
        refreshBefore(pluginId, applicationContext);
        // 刷新插件ApplicationContext
        applicationContext.refresh();
        processPluginApplication(pluginId, applicationContext);
        notifyRegistry(pluginId);
        log.debug("registry finish");
    }



    /**
     * 处理插件中的Application
     * @param pluginId 插件id
     * @param applicationContext 主程序上下文
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    protected void processPluginApplication(String pluginId, AnnotationConfigApplicationContext applicationContext)
            throws PluginBeanFactoryException {
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        LOCK.lock();
        try {
            registryBefore(pluginId, applicationContext);
            if(SPRING_BEAN_MAP.containsKey(pluginId)){
                throw new PluginBeanFactoryException("The plugin " + pluginId + " has been registered");
            }
            PluginSpringBean pluginSpringBean = new PluginSpringBean();
            Set<String> controllerComponentNames = resolveComponent(applicationContext, beanDefinitionNames, pluginSpringBean);
            resolveController(pluginSpringBean, controllerComponentNames);
            SPRING_BEAN_MAP.put(pluginId, pluginSpringBean);
        } finally {
            LOCK.unlock();
        }
    }

    /**
     * 解决 @Component 组件
     * @param pluginApplicationContext 插件上下文
     * @param beanDefinitionNames bean 定义的名称
     * @param pluginSpringBean 插件中要注册到spring中的bean
     * @return 返回注册的组件名称
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    private Set<String> resolveComponent(AnnotationConfigApplicationContext pluginApplicationContext,
                                         String[] beanDefinitionNames,
                                         PluginSpringBean pluginSpringBean) throws PluginBeanFactoryException {
        Set<String> controllerComponentNames = new HashSet<>();
        // 对插件的applicationContext刷新
        // 先向spring注入插件中Component
        for (String beanDefinitionName : beanDefinitionNames) {
            if(ignoreBeanNames(beanDefinitionName)){
                continue;
            }
            Object bean = pluginApplicationContext.getBean(beanDefinitionName);

            Class<?> aClass = bean.getClass();
            registryBeforePostBeanProcess(bean, pluginApplicationContext);
            String beanName = componentBeanRegistry.registry(bean);
            if(StringUtils.isEmpty(beanName)){
                continue;
            }
            pluginSpringBean.addComponentBeanName(beanName);
            if (aClass.getAnnotation(RestController.class) != null ||
                    aClass.getAnnotation(Controller.class) != null) {
                // 如果存在RestController 或者 Controller 注解, 说明该组件是Controller组件。
                controllerComponentNames.add(beanName);
            }
        }
        return controllerComponentNames;
    }


    /**
     * 刷新之前。此处用于扩展
     * @param pluginId 插件id
     * @param applicationContext 上下文
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    protected void refreshBefore(String pluginId, AnnotationConfigApplicationContext applicationContext)
            throws PluginBeanFactoryException {
        // 在此处扩展新增对插件中bean的处理
        pluginPostBeanProcess.sort(Comparator.comparing(PluginPostBeanProcess::order,
                Comparator.nullsLast(Comparator.naturalOrder())));
    }

    /**
     * 向主容器注册之前的操作
     * @param pluginId 插件id
     * @param applicationContext 主程序上下文
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    protected void registryBefore(String pluginId, AnnotationConfigApplicationContext applicationContext)
            throws PluginBeanFactoryException{
        // 什么事也不做。
    }

    /**
     * 扩展点： 向主容器注册之前处理 插件中的bean。
     * @param bean bean实例对象
     * @param pluginApplicationContext 插件的 applicationContext
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    protected void registryBeforePostBeanProcess(Object bean,
                                                 AnnotationConfigApplicationContext pluginApplicationContext)
            throws PluginBeanFactoryException{
        for (PluginPostBeanProcess postBeanProcess : pluginPostBeanProcess) {
            if(postBeanProcess == null){
                continue;
            }
            postBeanProcess.process(bean, pluginApplicationContext);
        }
    }


    /**
     * 注册 @Controller
     * @param pluginSpringBean  插件中要注册到spring中的bean
     * @param controllerComponentNames controller 组件名称
     * @throws PluginBeanFactoryException 插件bean工厂异常
     */
    private void resolveController(PluginSpringBean pluginSpringBean,
                                   Set<String> controllerComponentNames) throws PluginBeanFactoryException {
        for (String controllerComponentName : controllerComponentNames) {
            // 从spring容器中获取到controller实例
            Object controllerComponent = mainApplicationContext.getBean(controllerComponentName);
            if(controllerComponent == null){
                continue;
            }
            Set<RequestMappingInfo> requestMappingInfos = pluginControllerBeanRegistry.registry(
                    controllerComponent);
            if(requestMappingInfos == null){
                continue;
            }
            for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
                pluginSpringBean.addRequestMappingInfo(requestMappingInfo);
            }
        }
    }



    @Override
    public void unRegistry(String pluginId) throws PluginBeanFactoryException {
        PluginSpringBean pluginSpringBean = SPRING_BEAN_MAP.get(pluginId);
        if(pluginSpringBean == null){
            log.warn("unRegistry->Not found plugin id : {}", pluginId);
            return;
        }
        LOCK.lock();
        try {
            Set<String> componentBeanNames = pluginSpringBean.getComponentBeanNames();
            for (String componentBeanName : componentBeanNames) {
                componentBeanRegistry.unRegistry(componentBeanName);
            }
            Set<RequestMappingInfo> requestMappingInfos = pluginSpringBean.getRequestMappingInfos();
            pluginControllerBeanRegistry.unRegistry(requestMappingInfos);
            SPRING_BEAN_MAP.remove(pluginId);
            notifyUnRegistry(pluginId);
        } finally {
            LOCK.unlock();
        }
    }

    @Override
    public Class<? extends ApplicationContext> supportApplicationContextClass() {
        return AnnotationConfigApplicationContext.class;
    }

    /**
     * 要忽略的bean名称
     * @param beanName bean名称
     * @return 是否忽略
     */
    private Boolean ignoreBeanNames(String beanName){
        if(StringUtils.isEmpty(beanName)){
            return true;
        }
        switch (beanName){
            case "org.springframework.context.annotation.internalConfigurationAnnotationProcessor":
                return true;
            case "org.springframework.context.annotation.internalAutowiredAnnotationProcessor":
                return true;
            case "org.springframework.context.annotation.internalRequiredAnnotationProcessor":
                return true;
            case "org.springframework.context.annotation.internalCommonAnnotationProcessor":
                return true;
            case "org.springframework.context.event.internalEventListenerProcessor":
                return true;
            case "org.springframework.context.event.internalEventListenerFactory":
                return true;
            default:
                return false;
        }
    }

    /**
     * 插件bean定义
     */
    private class PluginSpringBean {
        private Set<String> componentBeanNames = new HashSet<>();
        private Set<RequestMappingInfo> requestMappingInfos = new HashSet<>();

        public void addComponentBeanName(String componentBeanName){
            if(componentBeanName == null || "".equals(componentBeanName)){
                log.error("addComponentBeanName->componentBeanName can not empty!");
                return;
            }
            componentBeanNames.add(componentBeanName);
        }

        public void addRequestMappingInfo(RequestMappingInfo requestMappingInfo){
            if(requestMappingInfo == null){
                log.error("addRequestMappingInfo->requestMappingInfo can not null!");
                return;
            }
            requestMappingInfos.add(requestMappingInfo);
        }

        public Set<String> getComponentBeanNames() {
            return componentBeanNames;
        }

        public Set<RequestMappingInfo> getRequestMappingInfos() {
            return requestMappingInfos;
        }
    }

}
