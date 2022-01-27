package com.gitee.starblues.core.checker;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.springframework.context.ApplicationContext;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 组合插件检查者
 * @author starBlues
 * @version 3.0.0
 */
public class ComposePluginBasicChecker implements PluginBasicChecker {

    private final List<PluginBasicChecker> pluginCheckers;

    public ComposePluginBasicChecker(ApplicationContext applicationContext) {
        this.pluginCheckers = new ArrayList<>();
        addDefaultChecker();
        addCustomChecker(applicationContext);
    }

    protected void addDefaultChecker(){
        pluginCheckers.add(new DefaultPluginBasicChecker());
    }

    protected void addCustomChecker(ApplicationContext applicationContext){
        List<PluginBasicChecker> pluginCheckers = SpringBeanUtils.getBeans(applicationContext,
                PluginBasicChecker.class);
        this.pluginCheckers.addAll(pluginCheckers);
    }

    public void add(PluginBasicChecker pluginChecker){
        if(pluginChecker != null){
            this.pluginCheckers.add(pluginChecker);
        }
    }


    @Override
    public void checkPath(Path path) throws Exception {
        for (PluginBasicChecker pluginChecker : pluginCheckers) {
            pluginChecker.checkPath(path);
        }
    }

    @Override
    public void checkDescriptor(PluginDescriptor descriptor) throws PluginException {
        for (PluginBasicChecker pluginChecker : pluginCheckers) {
            pluginChecker.checkDescriptor(descriptor);
        }
    }
}
