package com.gitee.starblues.bootstrap.processor.extract;

import com.gitee.starblues.annotation.Extract;
import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.bootstrap.processor.ProcessorException;
import com.gitee.starblues.bootstrap.processor.SpringPluginProcessor;
import com.gitee.starblues.spring.extract.OpExtractFactory;
import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;

/**
 * Extract 扩展Bean注册处理者
 * @author starBlues
 * @version 1.0
 */
public class ExtractBeanProcessor implements SpringPluginProcessor {

    @Override
    public void refreshAfter(ProcessorContext context) throws ProcessorException {
        GenericApplicationContext applicationContext = context.getApplicationContext();
        Map<String, Object> extractMap = applicationContext.getBeansWithAnnotation(Extract.class);
        if(ObjectUtils.isEmpty(extractMap)){
            return;
        }
        String pluginId = context.getPluginDescriptor().getPluginId();
        OpExtractFactory opExtractFactory = context.getPluginInteractive().getOpExtractFactory();
        for (Object extract : extractMap.values()) {
            opExtractFactory.add(pluginId, extract);
        }
    }

    @Override
    public void close(ProcessorContext context) throws ProcessorException {
        OpExtractFactory opExtractFactory = context.getPluginInteractive().getOpExtractFactory();
        String pluginId = context.getPluginDescriptor().getPluginId();
        opExtractFactory.remove(pluginId);
    }

    @Override
    public RunMode runMode() {
        return RunMode.ALL;
    }
}
