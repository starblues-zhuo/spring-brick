package com.gitee.starblues.spring.processor;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.processor.extract.ExtractBeanProcessor;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author starBlues
 * @version 1.0
 */
public class SpringPluginProcessorFactory implements SpringPluginProcessor{

    private final SpringPluginProcessor.RunMode runMode;

    private List<SpringPluginProcessor> processors;

    public SpringPluginProcessorFactory(SpringPluginProcessor.RunMode runMode) {
        this.runMode = runMode;
    }

    @Override
    public void initialize(GenericApplicationContext mainApplicationContext) throws Exception {
        List<SpringPluginProcessor> processors = getDefaultProcessors();
        if(runMode == RunMode.PLUGIN){
            List<SpringPluginProcessor> extendProcessors = SpringBeanUtils.getBeans(mainApplicationContext,
                    SpringPluginProcessor.class);
            processors.addAll(extendProcessors);
        }
        this.processors = processors.stream()
                .filter(p->{
                    RunMode runMode = p.runMode();
                    return runMode == RunMode.ALL || runMode == this.runMode;
                })
                .sorted(CommonUtils.orderPriority(SpringPluginProcessor::order))
                .collect(Collectors.toList());
        for (SpringPluginProcessor processor : this.processors) {
            processor.initialize(mainApplicationContext);
        }
    }

    private List<SpringPluginProcessor> getDefaultProcessors(){
        List<SpringPluginProcessor> processors = new ArrayList<>();
        processors.add(new BeanRegistryProcessor());
        processors.add(new FrameDefineBeanRegistryProcessor());
        processors.add(new InvokeOtherPluginProcessor());
        processors.add(new ExtractBeanProcessor());
        processors.add(new PluginControllerRegistryProcessor());

        processors.add(new OneselfBeanRegistryProcessor());
        return processors;
    }


    @Override
    public void refreshBefore(SpringPluginRegistryInfo registryInfo) throws Exception {
        for (SpringPluginProcessor processor : processors) {
            processor.refreshBefore(registryInfo);
        }
    }

    @Override
    public void refreshAfter(SpringPluginRegistryInfo registryInfo) throws Exception {
        for (SpringPluginProcessor processor : processors) {
            processor.refreshAfter(registryInfo);
        }
    }

    @Override
    public void failure(SpringPluginRegistryInfo registryInfo) throws Exception {
        for (SpringPluginProcessor processor : processors) {
            try {
                processor.failure(registryInfo);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void close(SpringPluginRegistryInfo registryInfo) throws Exception {
        for (SpringPluginProcessor processor : processors) {
            try {
                processor.close(registryInfo);
            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }

    @Override
    public RunMode runMode() {
        return RunMode.ALL;
    }
}
