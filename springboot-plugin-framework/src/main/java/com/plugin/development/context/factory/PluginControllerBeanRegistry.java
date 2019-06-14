package com.plugin.development.context.factory;

import com.plugin.development.exception.PluginBeanFactoryException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 注册插件中的 Controller
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginControllerBeanRegistry implements PluginBeanRegistry<Set<RequestMappingInfo>> {

    private final Logger log = LoggerFactory.getLogger(PluginControllerBeanRegistry.class);

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;


    public PluginControllerBeanRegistry(ApplicationContext applicationContext) {
        this.requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
    }


    @Override
    public Set<RequestMappingInfo> registry(Object object) throws PluginBeanFactoryException {
        if(object == null){
            throw new PluginBeanFactoryException("object can not null");
        }

        Method getMappingForMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class,
                "getMappingForMethod", Method.class, Class.class);
        getMappingForMethod.setAccessible(true);
        try {
            Class aClass = object.getClass();
            Method[] methods = aClass.getMethods();
            Set<RequestMappingInfo> requestMappingInfos = new HashSet<>();
            for (Method method : methods) {
                if (isHaveRequestMapping(method)) {
                    RequestMappingInfo requestMappingInfo = (RequestMappingInfo)
                            getMappingForMethod.invoke(requestMappingHandlerMapping, method, aClass);
                    requestMappingHandlerMapping.registerMapping(requestMappingInfo, object, method);
                    requestMappingInfos.add(requestMappingInfo);
                }
            }
            return requestMappingInfos;
        } catch (SecurityException e) {
            throw new PluginBeanFactoryException(e);
        } catch (InvocationTargetException e) {
            throw new PluginBeanFactoryException(e);
        } catch (Exception e){
            throw new PluginBeanFactoryException(e);
        }
    }


    @Override
    public void unRegistry(Set<RequestMappingInfo> requestMappingInfos) {
        if(requestMappingInfos != null && !requestMappingInfos.isEmpty()){
            for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
                requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
            }
        }
    }



    /**
     * 方法上是否存在 @RequestMapping 注解
     * @param method
     * @return
     */
    private boolean isHaveRequestMapping(Method method){
        if (AnnotationUtils.findAnnotation(method, RequestMapping.class) != null) {
            return true;
        } else {
            return false;
        }
    }


}
