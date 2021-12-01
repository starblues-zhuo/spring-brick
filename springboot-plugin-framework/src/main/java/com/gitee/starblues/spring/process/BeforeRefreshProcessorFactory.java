package com.gitee.starblues.spring.process;

import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.process.before.RegisterNecessaryBeanProcessor;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class BeforeRefreshProcessorFactory implements BeforeRefreshProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(BeforeRefreshProcessorFactory.class);

    private final ConfigurableApplicationContext mainApplicationContext;

    private final List<BeforeRefreshProcessor> processors;
    private final IntegrationConfiguration configuration;

    public BeforeRefreshProcessorFactory(ConfigurableApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;

        List<BeforeRefreshProcessor> processors = null;
        if(mainApplicationContext != null){
            this.configuration = mainApplicationContext.getBean(IntegrationConfiguration.class);
            processors = SpringBeanUtils.getBeans(
                    mainApplicationContext, BeforeRefreshProcessor.class);
        } else {
            configuration = null;
            processors = new ArrayList<>();
        }
        addDefault(processors);
        processors.sort(CommonUtils.orderPriority(BeforeRefreshProcessor::orderOfBefore));
        this.processors = processors;
    }

    private void addDefault(List<BeforeRefreshProcessor> processors) {
        processors.add(new RegisterNecessaryBeanProcessor());
        if(configuration != null && configuration.enablePluginRestController()){
            processors.add(PluginControllerProcessor.getInstance(mainApplicationContext));
        }
    }

    @Override
    public void registryOfBefore(SpringPluginRegistryInfo registryInfo) {
        for (BeforeRefreshProcessor processor : processors) {
            try {
                processor.registryOfBefore(registryInfo);
            } catch (Exception e){
                LOGGER.error("BeforeRefreshProcessor: [{}] registry 异常",
                        processor.getClass().getName(), e);
            }
        }
    }

    @Override
    public void unRegistryOfBefore(SpringPluginRegistryInfo registryInfo) {
        for (BeforeRefreshProcessor processor : processors) {
            try {
                processor.unRegistryOfBefore(registryInfo);
            } catch (Exception e){
                LOGGER.error("BeforeRefreshProcessor: [{}] unRegistry 异常",
                        processor.getClass().getName(), e);
            }
        }
    }

    @Override
    public OrderPriority orderOfBefore() {
        return OrderPriority.getHighPriority();
    }

}
