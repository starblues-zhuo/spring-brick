package com.gitee.starblues.extension.support;

import com.gitee.starblues.extension.PluginControllerProcessorExtend;
import com.gitee.starblues.factory.process.post.bean.model.ControllerWrapper;
import com.gitee.starblues.utils.ClassUtils;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.springdoc.api.AbstractOpenApiResource;
import org.springdoc.core.OpenAPIService;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * @author starBlues
 * @version 2.4.0
 */
public class SpringDocControllerProcessor implements PluginControllerProcessorExtend {

    private final ApplicationContext applicationContext;

    private List<Class<?>> restControllers;
    private OpenAPIService openAPIService;

    public SpringDocControllerProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    @Override
    public void initialize() {
        AbstractOpenApiResource openApiResource = SpringBeanUtils.getExistBean(applicationContext, AbstractOpenApiResource.class);
        if(openApiResource == null){
            return;
        }
        try {
            restControllers =  ClassUtils.getReflectionField(openApiResource,
                    "ADDITIONAL_REST_CONTROLLERS");
        } catch (IllegalAccessException e) {
            restControllers = null;
        }
        openAPIService = SpringBeanUtils.getExistBean(applicationContext, OpenAPIService.class);
    }

    @Override
    public void registry(String pluginId, List<ControllerWrapper> controllerWrappers) throws Exception {
        if(restControllers != null){
            for (ControllerWrapper controllerWrapper : controllerWrappers) {
                restControllers.add(controllerWrapper.getBeanClass());
            }
            refresh();
        }
    }

    @Override
    public void unRegistry(String pluginId, List<ControllerWrapper> controllerWrappers) throws Exception {
        if(restControllers != null && !restControllers.isEmpty()){
            for (ControllerWrapper controllerWrapper : controllerWrappers) {
                restControllers.remove(controllerWrapper.getBeanClass());
            }
            refresh();
        }
    }

    private void refresh(){
        if(openAPIService != null){
            openAPIService.setCachedOpenAPI(null);
            openAPIService.resetCalculatedOpenAPI();
        }
    }

}
