package com.gitee.starblues.spring.processor;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.processor.scanner.PluginClassPathBeanDefinitionScanner;
import com.gitee.starblues.utils.OrderPriority;
import com.gitee.starblues.utils.ScanUtils;


/**
 * @author starBlues
 * @version 1.0
 */
public class BeanRegistryProcessor implements SpringPluginProcessor{

    @Override
    public void refreshBefore(SpringPluginRegistryInfo registryInfo) throws Exception {
        PluginClassPathBeanDefinitionScanner scanner = new PluginClassPathBeanDefinitionScanner(registryInfo);
        scanner.scan(ScanUtils.getScanBasePackages((registryInfo.getPluginWrapper().getPluginClass())));
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority().down(10);
    }

    @Override
    public RunMode runMode() {
        return RunMode.PLUGIN;
    }
}
