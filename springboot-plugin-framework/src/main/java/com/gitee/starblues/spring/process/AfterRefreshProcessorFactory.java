package com.gitee.starblues.spring.process;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.process.after.ControllerAfterRefreshProcessor;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class AfterRefreshProcessorFactory implements AfterRefreshProcessor{

    private static final Logger LOGGER = LoggerFactory.getLogger(AfterRefreshProcessorFactory.class);

    private final List<AfterRefreshProcessor> afterRefreshProcessors;

    public AfterRefreshProcessorFactory(GenericApplicationContext mainApplicationContext) {
        List<AfterRefreshProcessor> afterRefreshProcessors = SpringBeanUtils.getBeans(
                mainApplicationContext, AfterRefreshProcessor.class);
        addDefault(afterRefreshProcessors);
        afterRefreshProcessors.sort(CommonUtils.orderPriority(AfterRefreshProcessor::order));
        this.afterRefreshProcessors = afterRefreshProcessors;
    }

    protected void addDefault(List<AfterRefreshProcessor> afterRefreshProcessors){
        afterRefreshProcessors.add(new ControllerAfterRefreshProcessor());
    }

    @Override
    public void registry(SpringPluginRegistryInfo registryInfo) {
        for (AfterRefreshProcessor afterRefreshProcessor : afterRefreshProcessors) {
            try {
                afterRefreshProcessor.registry(registryInfo);
            } catch (Exception e){
                LOGGER.error("AfterRefreshProcessor: [{}] registry 异常",
                        afterRefreshProcessor.getClass().getName(), e);
            }
        }
    }

    @Override
    public void unRegistry(SpringPluginRegistryInfo registryInfo) {
        for (AfterRefreshProcessor afterRefreshProcessor : afterRefreshProcessors) {
            try {
                afterRefreshProcessor.unRegistry(registryInfo);
            } catch (Exception e){
                LOGGER.error("AfterRefreshProcessor: [{}] unRegistry 异常",
                        afterRefreshProcessor.getClass().getName(), e);
            }
        }
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }

}
