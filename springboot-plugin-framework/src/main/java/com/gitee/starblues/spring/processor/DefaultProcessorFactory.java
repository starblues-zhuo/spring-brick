package com.gitee.starblues.spring.processor;

import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.processor.before.RegisterNecessaryBeanProcessor;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.List;

/**
 * @author starBlues
 * @version 1.0
 */
public class DefaultProcessorFactory extends AbstractProcessorFactory{

    public DefaultProcessorFactory(ConfigurableApplicationContext mainApplicationContext,
                                   IntegrationConfiguration configuration,
                                   ProcessorRunMode processorRunMode) {
        super(mainApplicationContext, configuration, processorRunMode);
    }

    @Override
    protected void addDefaultBeforeProcessor(List<BeforeRefreshProcessor> beforeProcessors) {
        beforeProcessors.add(new RegisterNecessaryBeanProcessor());
    }

    @Override
    protected void addDefaultAfterProcessor(List<AfterRefreshProcessor> afterProcessors){

    }

    @Override
    protected void addDefaultCommonProcessor(List<BeforeRefreshProcessor> beforeProcessors,
                                             List<AfterRefreshProcessor> afterProcessors) {
        addPluginControllerProcessor(beforeProcessors, afterProcessors);
    }

    private void addPluginControllerProcessor(List<BeforeRefreshProcessor> beforeProcessors,
                                              List<AfterRefreshProcessor> afterProcessors){
        if(configuration != null && configuration.enablePluginRestController()
                && processorRunMode == ProcessorRunMode.PLUGIN){
            addDefaultCommonProcessor(beforeProcessors, afterProcessors,
                    new PluginControllerProcessor(mainApplicationContext));
        }
    }

}
