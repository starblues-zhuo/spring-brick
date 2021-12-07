package com.gitee.starblues.spring.listener;

import com.gitee.starblues.annotation.Supplier;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.processor.classgroup.ComposeClassGroup;
import com.gitee.starblues.spring.processor.invoke.InvokeSupperCache;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.OrderPriority;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;

import java.util.List;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class BeanRegistryListener implements PluginSpringApplicationRunListener {

    @Override
    public void refreshPrepared(SpringPluginRegistryInfo registryInfo) throws Exception {
        GenericApplicationContext applicationContext = registryInfo.getPluginSpringApplication()
                .getApplicationContext();
        SpringBeanRegister springBeanRegister = new SpringBeanRegister(applicationContext);
        List<Class<?>> classes = registryInfo.getRegistryInfo(ComposeClassGroup.ID);
        if(ObjectUtils.isEmpty(classes)){
            return;
        }
        for (Class<?> aClass : classes) {
            String beanName = springBeanRegister.register(aClass);
            cacheInvokeSupplier(aClass, beanName, registryInfo);
        }
    }

    @Override
    public void stop(SpringPluginRegistryInfo registryInfo) throws Exception {
        InvokeSupperCache.remove(registryInfo.getPluginWrapper().getPluginId());
    }

    private void cacheInvokeSupplier(Class<?> aClass, String beanName, SpringPluginRegistryInfo registryInfo){
        Supplier supplier = AnnotationUtils.findAnnotation(aClass, Supplier.class);
        if(supplier == null){
            return;
        }
        String pluginId = registryInfo.getPluginWrapper().getPluginId();
        GenericApplicationContext applicationContext = registryInfo.getPluginSpringApplication()
                .getApplicationContext();
        InvokeSupperCache.add(pluginId, new InvokeSupperCache.Cache(supplier.value(), beanName, applicationContext));
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }

    @Override
    public ListenerRunMode runMode() {
        return ListenerRunMode.PLUGIN;
    }
}
