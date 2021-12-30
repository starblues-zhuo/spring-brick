package com.gitee.starblues.bootstrap.processor;

import com.gitee.starblues.bootstrap.processor.interceptor.PluginInterceptorRegister;
import com.gitee.starblues.bootstrap.processor.interceptor.PluginInterceptorRegistry;
import com.gitee.starblues.bootstrap.utils.SpringBeanUtils;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.utils.ClassUtils;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.SpringBeanUtilsV3;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginInterceptorsProcessor implements SpringPluginProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String INTERCEPTORS = "pluginHandlerInterceptors";

    private AbstractHandlerMapping handlerMapping;


    @Override
    public void initialize(ProcessorContext context) throws ProcessorException {
        MainApplicationContext applicationContext = context.getMainApplicationContext();
        handlerMapping = SpringBeanUtilsV3.getExistBean(applicationContext,
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
        String pluginRestPrefix = CommonUtils.getPluginRestPrefix(configuration, pluginId);

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
    public RunMode runMode() {
        return RunMode.PLUGIN;
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
