package com.gitee.starblues.spring.process.after;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.process.AfterRefreshProcessor;
import com.gitee.starblues.utils.OrderPriority;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Controller;

import java.util.Map;

/**
 * @author starBlues
 * @version 1.0
 */
public class ControllerAfterRefreshProcessor implements AfterRefreshProcessor {

    @Override
    public void registry(SpringPluginRegistryInfo registryInfo) {
        ConfigurableApplicationContext applicationContext = registryInfo.getPluginSpringApplication().getApplicationContext();
        String[] beanDefinitionNames = applicationContext.getBeanDefinitionNames();
        for (String beanDefinitionName : beanDefinitionNames) {
            System.out.println(beanDefinitionName);
        }

        String[] singletonNames = applicationContext.getBeanFactory().getSingletonNames();
        for (String singletonName : singletonNames) {
            System.out.println(singletonName);
        }


        Map<String, Object> beansWithAnnotation = registryInfo.getPluginSpringApplication().getApplicationContext()
                .getBeansWithAnnotation(Controller.class);
        System.out.println(beansWithAnnotation);
    }

    @Override
    public void unRegistry(SpringPluginRegistryInfo registryInfo) {

    }

    @Override
    public OrderPriority order() {
        return null;
    }
}
