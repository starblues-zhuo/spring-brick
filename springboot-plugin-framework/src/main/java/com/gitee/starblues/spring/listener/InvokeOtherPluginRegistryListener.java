package com.gitee.starblues.spring.listener;

import com.gitee.starblues.annotation.Caller;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.processor.classgroup.CallerClassGroup;
import com.gitee.starblues.spring.processor.invoke.InvokeBeanFactory;
import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;

/**
 * @author starBlues
 * @version 1.0
 */
public class InvokeOtherPluginRegistryListener implements PluginSpringApplicationRunListener {


    public InvokeOtherPluginRegistryListener() {
    }

    @Override
    public void refreshPrepared(SpringPluginRegistryInfo registryInfo) throws Exception {
        List<Class<?>> classes = registryInfo.getRegistryInfo(CallerClassGroup.ID);
        if(ObjectUtils.isEmpty(classes)){
            return;
        }
        GenericApplicationContext applicationContext = registryInfo.getPluginSpringApplication().getApplicationContext();
        SpringBeanRegister springBeanRegister = new SpringBeanRegister(applicationContext);
        for (Class<?> aClass : classes) {
            Caller caller = AnnotationUtils.findAnnotation(aClass, Caller.class);
            if(caller == null){
                continue;
            }
            springBeanRegister.register(aClass, definition -> {
                // 是调用方
                definition.getPropertyValues().add("callerAnnotation", caller);
                definition.getPropertyValues().add("callerInterface", aClass);
                definition.getPropertyValues().add("registryInfo", registryInfo);
                definition.setBeanClass(InvokeBeanFactory.class);
                definition.setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
            });
        }
    }

    @Override
    public ListenerRunMode runMode() {
        return ListenerRunMode.ALL;
    }

}