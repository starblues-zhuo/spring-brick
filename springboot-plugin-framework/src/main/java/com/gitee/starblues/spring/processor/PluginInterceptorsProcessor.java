package com.gitee.starblues.spring.processor;

import com.gitee.starblues.spring.processor.interceptor.PluginInterceptorRegister;
import com.gitee.starblues.spring.processor.interceptor.PluginInterceptorRegistry;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.utils.ClassUtils;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;
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
public class PluginInterceptorsProcessor implements SpringPluginProcessor{

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String INTERCEPTORS = "pluginHandlerInterceptors";

    private AbstractHandlerMapping handlerMapping;


    @Override
    public void initialize(GenericApplicationContext mainApplicationContext) throws Exception {
        handlerMapping = SpringBeanUtils.getExistBean(mainApplicationContext,
                AbstractHandlerMapping.class);
        if(handlerMapping == null){
            logger.warn("Not found AbstractHandlerMapping, Plugin interceptor can't use");
        }
    }

    @Override
    public void refreshAfter(SpringPluginRegistryInfo registryInfo) throws Exception {
        if(handlerMapping == null){
            return;
        }
        GenericApplicationContext pluginApplicationContext = registryInfo.getPluginSpringApplication().getApplicationContext();
        List<PluginInterceptorRegister> interceptorRegisters = SpringBeanUtils.getBeans(pluginApplicationContext,
                PluginInterceptorRegister.class);
        List<HandlerInterceptor> interceptorsObjects = new ArrayList<>();
        List<HandlerInterceptor> adaptedInterceptors = getAdaptedInterceptors();
        if(adaptedInterceptors == null){
            return;
        }
        IntegrationConfiguration configuration = registryInfo.getConfiguration();
        String pluginId = registryInfo.getPluginWrapper().getPluginId();
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
        registryInfo.addRegistryInfo(INTERCEPTORS, interceptorsObjects);
    }

    @Override
    public void close(SpringPluginRegistryInfo registryInfo) throws Exception {
        if(handlerMapping == null){
            return;
        }
        List<HandlerInterceptor> interceptorsObjects = registryInfo.getRegistryInfo(INTERCEPTORS);
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
