package com.gitee.starblues.spring.processor;

import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.processor.before.RegisterNecessaryBeanProcessor;
import com.gitee.starblues.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ConfigurableApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author starBlues
 * @version 3.0.0
 */
public abstract class AbstractProcessorFactory implements BeforeRefreshProcessor, AfterRefreshProcessor{

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractProcessorFactory.class);

    private final List<AfterRefreshProcessor> afterProcessors;
    private final List<BeforeRefreshProcessor> beforeProcessors;

    protected final ProcessorRunMode processorRunMode;

    protected final IntegrationConfiguration configuration;
    protected final ConfigurableApplicationContext mainApplicationContext;

    public AbstractProcessorFactory(ConfigurableApplicationContext mainApplicationContext,
                                    IntegrationConfiguration configuration,
                                    ProcessorRunMode processorRunMode) {
        this.mainApplicationContext = mainApplicationContext;
        this.configuration = configuration;

        this.processorRunMode = processorRunMode;

        List<BeforeRefreshProcessor> beforeProcessors = new ArrayList<>();
        List<AfterRefreshProcessor> afterProcessors = new ArrayList<>();
        addDefaultBeforeProcessor(beforeProcessors);
        addDefaultAfterProcessor(afterProcessors);
        addDefaultCommonProcessor(beforeProcessors, afterProcessors);

        this.beforeProcessors = beforeProcessors.stream()
                .filter(Objects::nonNull)
                .sorted(CommonUtils.orderPriority(BeforeRefreshProcessor::orderOfBefore))
                .collect(Collectors.toList());
        this.afterProcessors = afterProcessors.stream()
                .filter(Objects::nonNull)
                .sorted(CommonUtils.orderPriority(AfterRefreshProcessor::orderOfAfter))
                .collect(Collectors.toList());
    }

    protected abstract void addDefaultBeforeProcessor(List<BeforeRefreshProcessor> beforeProcessors);

    protected abstract void addDefaultAfterProcessor(List<AfterRefreshProcessor> afterProcessors);

    protected abstract void addDefaultCommonProcessor(List<BeforeRefreshProcessor> beforeProcessors,
                                             List<AfterRefreshProcessor> afterProcessors);

    protected void addDefaultCommonProcessor(List<BeforeRefreshProcessor> beforeProcessors,
                                             List<AfterRefreshProcessor> afterProcessors,
                                             Object o){
        if(o instanceof BeforeRefreshProcessor){
            beforeProcessors.add((BeforeRefreshProcessor)o);
        }
        if(o instanceof AfterRefreshProcessor){
            afterProcessors.add((AfterRefreshProcessor)o);
        }
    }

    @Override
    public void registryOfAfter(SpringPluginRegistryInfo registryInfo) {
        for (AfterRefreshProcessor processor : afterProcessors) {
            try {
                processor.registryOfAfter(registryInfo);
            } catch (Exception e){
                LOGGER.error("AfterRefreshProcessor: [{}] registry 异常",
                        processor.getClass().getName(), e);
            }
        }
    }

    @Override
    public void unRegistryOfAfter(SpringPluginRegistryInfo registryInfo) {
        for (AfterRefreshProcessor processor : afterProcessors) {
            try {
                processor.unRegistryOfAfter(registryInfo);
            } catch (Exception e){
                LOGGER.error("AfterRefreshProcessor: [{}] unRegistry 异常",
                        processor.getClass().getName(), e);
            }
        }
    }

    @Override
    public void registryOfBefore(SpringPluginRegistryInfo registryInfo) {
        for (BeforeRefreshProcessor processor : beforeProcessors) {
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
        for (BeforeRefreshProcessor processor : beforeProcessors) {
            try {
                processor.unRegistryOfBefore(registryInfo);
            } catch (Exception e){
                LOGGER.error("BeforeRefreshProcessor: [{}] unRegistry 异常",
                        processor.getClass().getName(), e);
            }
        }
    }

    private boolean filterProcessorRunMode(ProcessorRunMode processorRunMode){
        if(processorRunMode == ProcessorRunMode.ALL){
            return true;
        }
        return processorRunMode == this.processorRunMode;
    }

}
