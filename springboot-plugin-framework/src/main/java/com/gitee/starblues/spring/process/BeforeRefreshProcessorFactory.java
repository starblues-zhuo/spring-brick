package com.gitee.starblues.spring.process;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.process.before.RegisterNecessaryBean;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class BeforeRefreshProcessorFactory implements BeforeRefreshProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeforeRefreshProcessorFactory.class);

    private final List<BeforeRefreshProcessor> processors;

    public BeforeRefreshProcessorFactory(ConfigurableApplicationContext mainApplicationContext) {
        List<BeforeRefreshProcessor> processors = null;
        if(mainApplicationContext != null){
            processors = SpringBeanUtils.getBeans(
                    mainApplicationContext, BeforeRefreshProcessor.class);
        } else {
            processors = new ArrayList<>();
        }
        addDefault(processors);
        processors.sort(CommonUtils.orderPriority(BeforeRefreshProcessor::order));
        this.processors = processors;
    }

    private void addDefault(List<BeforeRefreshProcessor> processors) {
        processors.add(new RegisterNecessaryBean());
    }

    @Override
    public void registry(SpringPluginRegistryInfo registryInfo) {
        for (BeforeRefreshProcessor processor : processors) {
            try {
                processor.registry(registryInfo);
            } catch (Exception e){
                LOGGER.error("BeforeRefreshProcessor: [{}] registry 异常",
                        processor.getClass().getName(), e);
            }
        }
    }

    @Override
    public void unRegistry(SpringPluginRegistryInfo registryInfo) {
        for (BeforeRefreshProcessor processor : processors) {
            try {
                processor.unRegistry(registryInfo);
            } catch (Exception e){
                LOGGER.error("BeforeRefreshProcessor: [{}] unRegistry 异常",
                        processor.getClass().getName(), e);
            }
        }
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }

}
