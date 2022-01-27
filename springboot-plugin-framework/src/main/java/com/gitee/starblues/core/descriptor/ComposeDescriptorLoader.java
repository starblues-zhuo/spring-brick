package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.core.checker.PluginBasicChecker;
import com.gitee.starblues.core.exception.PluginException;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * 组合插件描述加载者
 * @author starBlues
 * @version 3.0.0
 */
public class ComposeDescriptorLoader implements PluginDescriptorLoader{
    
    private final List<PluginDescriptorLoader> pluginDescriptorLoaders = new ArrayList<>();
    
    private final PluginBasicChecker pluginChecker;

    public ComposeDescriptorLoader(PluginBasicChecker pluginChecker) {
        this.pluginChecker = pluginChecker;
        addDefaultLoader();
    }

    protected void addDefaultLoader(){
        addLoader(new DevPluginDescriptorLoader());
        addLoader(new ProdPluginDescriptorLoader());
    }


    public void addLoader(PluginDescriptorLoader descriptorLoader){
        if(descriptorLoader != null){
            pluginDescriptorLoaders.add(descriptorLoader);
        }
    }
    
    
    @Override
    public InsidePluginDescriptor load(Path location) throws PluginException {
        for (PluginDescriptorLoader pluginDescriptorLoader : pluginDescriptorLoaders) {
            try {
                InsidePluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(location);
                if(pluginDescriptor != null){
                    pluginChecker.checkDescriptor(pluginDescriptor);
                    return pluginDescriptor;
                }
            } catch (Exception e){
                // 忽略异常
            }
        }
        return null;
    }

    @Override
    public void close() throws Exception {

    }
}
