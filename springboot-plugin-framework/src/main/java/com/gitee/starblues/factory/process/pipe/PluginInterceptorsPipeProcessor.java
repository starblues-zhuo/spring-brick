package com.gitee.starblues.factory.process.pipe;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.interceptor.PluginInterceptorRegister;
import com.gitee.starblues.factory.process.pipe.interceptor.PluginInterceptorRegistry;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.utils.ClassUtils;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.context.request.WebRequestInterceptor;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.WebRequestHandlerInterceptorAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件 SpringMVC 拦截器的处理
 * @author starBlues
 * @version 2.4.1
 */
public class PluginInterceptorsPipeProcessor implements PluginPipeProcessor{

    private final ApplicationContext mainApplicationContext;
    private final IntegrationConfiguration configuration;
    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private final static String INTERCEPTORS = "interceptors";

    private AbstractHandlerMapping handlerMapping;


    public PluginInterceptorsPipeProcessor(ApplicationContext mainApplicationContext){
        this.mainApplicationContext = mainApplicationContext;
        this.configuration = mainApplicationContext.getBean(IntegrationConfiguration.class);
    }

    @Override
    public void initialize() throws Exception {
        handlerMapping = SpringBeanUtils.getExistBean(mainApplicationContext,
                AbstractHandlerMapping.class);
        if(handlerMapping == null){
            logger.warn("Not found AbstractHandlerMapping, Plugin interceptor can't use");
        }
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        if(handlerMapping == null){
            return;
        }
        GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
        List<PluginInterceptorRegister> interceptorRegisters = SpringBeanUtils.getBeans(pluginApplicationContext,
                PluginInterceptorRegister.class);
        List<HandlerInterceptor> interceptorsObjects = new ArrayList<>();
        List<HandlerInterceptor> adaptedInterceptors = getAdaptedInterceptors();
        if(adaptedInterceptors == null){
            return;
        }
        String pluginRestPrefix = CommonUtils.getPluginRestPrefix(configuration, pluginRegistryInfo.getPluginWrapper().getPluginId());

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
        pluginRegistryInfo.addExtension(INTERCEPTORS, interceptorsObjects);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        if(handlerMapping == null){
            return;
        }
        List<HandlerInterceptor> interceptorsObjects = pluginRegistryInfo.getExtension(INTERCEPTORS);
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
