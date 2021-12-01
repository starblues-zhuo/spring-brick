package com.gitee.starblues.spring.process;

import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class AfterRefreshProcessorFactory implements AfterRefreshProcessor{

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterRefreshProcessorFactory.class);

    private final List<AfterRefreshProcessor> afterRefreshProcessors;
    private final IntegrationConfiguration configuration;
    private final ConfigurableApplicationContext mainApplicationContext;

    public AfterRefreshProcessorFactory(ConfigurableApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
        this.configuration = mainApplicationContext.getBean(IntegrationConfiguration.class);

        List<AfterRefreshProcessor> processors = SpringBeanUtils.getBeans(
                mainApplicationContext, AfterRefreshProcessor.class);
        addDefault(processors);
        processors.sort(CommonUtils.orderPriority(AfterRefreshProcessor::orderOfAfter));
        this.afterRefreshProcessors = processors;
    }

    protected void addDefault(List<AfterRefreshProcessor> processors){
        if(configuration != null && configuration.enablePluginRestController()){
            processors.add(PluginControllerProcessor.getInstance(mainApplicationContext));
        }
    }

    @Override
    public void registryOfAfter(SpringPluginRegistryInfo registryInfo) {
        for (AfterRefreshProcessor afterRefreshProcessor : afterRefreshProcessors) {
            try {
                afterRefreshProcessor.registryOfAfter(registryInfo);
            } catch (Exception e){
                LOGGER.error("AfterRefreshProcessor: [{}] registry 异常",
                        afterRefreshProcessor.getClass().getName(), e);
            }
        }
    }

    @Override
    public void unRegistryOfAfter(SpringPluginRegistryInfo registryInfo) {
        for (AfterRefreshProcessor afterRefreshProcessor : afterRefreshProcessors) {
            try {
                afterRefreshProcessor.unRegistryOfAfter(registryInfo);
            } catch (Exception e){
                LOGGER.error("AfterRefreshProcessor: [{}] unRegistry 异常",
                        afterRefreshProcessor.getClass().getName(), e);
            }
        }
    }

    @Override
    public OrderPriority orderOfAfter() {
        return OrderPriority.getHighPriority();
    }

}
