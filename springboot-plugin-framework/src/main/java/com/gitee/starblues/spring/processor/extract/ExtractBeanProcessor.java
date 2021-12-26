package com.gitee.starblues.spring.processor.extract;

import com.gitee.starblues.annotation.Extract;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.processor.SpringPluginProcessor;
import com.gitee.starblues.utils.ObjectUtils;
import org.springframework.context.support.GenericApplicationContext;

import java.util.Map;

/**
 * Extract 扩展Bean注册处理者
 * @author starBlues
 * @version 1.0
 */
public class ExtractBeanProcessor implements SpringPluginProcessor {

    private final ExtractFactory extractFactory = ExtractFactory.getInstant();

    @Override
    public void initialize(GenericApplicationContext mainApplicationContext) throws Exception {
//        // 获取主程序的扩展
//        Map<String, Object> extractMap = mainApplicationContext.getBeansWithAnnotation(Extract.class);
//        if(ObjectUtils.isEmpty(extractMap)){
//            return;
//        }
//        for (Object extract : extractMap.values()) {
//            extractFactory.addOfMain(extract);
//        }
    }

    @Override
    public void refreshAfter(SpringPluginRegistryInfo registryInfo) throws Exception {
        GenericApplicationContext applicationContext = registryInfo.getPluginSpringApplication().getApplicationContext();
        Map<String, Object> extractMap = applicationContext.getBeansWithAnnotation(Extract.class);
        if(ObjectUtils.isEmpty(extractMap)){
            return;
        }
        String pluginId = registryInfo.getPluginWrapper().getPluginId();
        for (Object extract : extractMap.values()) {
            extractFactory.add(pluginId, extract);
        }
    }

    @Override
    public RunMode runMode() {
        return RunMode.ALL;
    }
}
