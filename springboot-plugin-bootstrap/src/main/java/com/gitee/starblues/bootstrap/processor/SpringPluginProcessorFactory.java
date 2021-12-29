package com.gitee.starblues.bootstrap.processor;

import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.OrderPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author starBlues
 * @version 1.0
 */
public class SpringPluginProcessorFactory implements SpringPluginProcessor {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final RunMode runMode;

    private List<SpringPluginProcessor> processors;

    public SpringPluginProcessorFactory(RunMode runMode) {
        this(runMode, null);
    }

    public SpringPluginProcessorFactory(RunMode runMode, List<SpringPluginProcessor> processors) {
        this.runMode = runMode;
        if(!ObjectUtils.isEmpty(processors)){
            this.processors = processors;
        } else {
            this.processors = new ArrayList<>();
        }
    }

    public void addSpringPluginProcessor(SpringPluginProcessor springPluginProcessor){
        if(springPluginProcessor != null){
            this.processors.add(springPluginProcessor);
        }
    }

    private List<SpringPluginProcessor> getDefaultProcessors(){
        List<SpringPluginProcessor> processors = new ArrayList<>();
        processors.add(new FrameDefineBeanProcessor());
        processors.add(new PluginControllerRegistryProcessor());
        processors.add(new InvokeOtherPluginProcessor());
        return processors;
    }

    @Override
    public void initialize(ProcessorContext processorContext) throws ProcessorException {
        List<SpringPluginProcessor> processors = getDefaultProcessors();
        processors.addAll(this.processors);
        this.processors = processors.stream()
                .filter(p->{
                    RunMode runMode = p.runMode();
                    return runMode == RunMode.ALL || runMode == this.runMode;
                })
                .sorted(CommonUtils.orderPriority(SpringPluginProcessor::order))
                .collect(Collectors.toList());
        for (SpringPluginProcessor processor : this.processors) {
            try {
                processor.initialize(processorContext);
            } catch (Exception e){
                processException(processor, "initialize", e, true);
            }
        }
    }

    @Override
    public void refreshBefore(ProcessorContext context) throws ProcessorException {
        for (SpringPluginProcessor processor : processors) {
            try {
                processor.refreshBefore(context);
            } catch (Exception e){
                processException(processor, "refreshBefore", e, true);
            }
        }
    }

    @Override
    public void refreshAfter(ProcessorContext context) throws ProcessorException {
        for (SpringPluginProcessor processor : processors) {
            try {
                processor.refreshAfter(context);
            } catch (Exception e){
                processException(processor, "refreshAfter", e, true);
            }
        }
    }

    @Override
    public void failure(ProcessorContext context) throws ProcessorException {
        for (SpringPluginProcessor processor : processors) {
            try {
                processor.failure(context);
            } catch (Exception e){
                processException(processor, "failure", e, false);
            }
        }
    }

    @Override
    public void close(ProcessorContext context) throws ProcessorException {
        for (SpringPluginProcessor processor : processors) {
            try {
                processor.close(context);
            } catch (Exception e){
                processException(processor, "close", e, false);
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

    private void processException(SpringPluginProcessor processor, String executeType,
                                  Exception e, boolean isThrow){
        String error =  "Processor[" + processor.getClass().getName() + "] execute[" + executeType + "] failure : "
                + e.getMessage();
        if(isThrow){
            throw new ProcessorException(error, e);
        } else {
            logger.error(error, e);
        }
    }
}
