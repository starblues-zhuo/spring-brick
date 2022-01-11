package com.gitee.starblues.core.classloader;

import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.PluginFileUtils;

import java.io.File;
import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginResourceLoaderFactory extends ResourceLoaderFactory{


    public synchronized void addResource(PluginDescriptor pluginDescriptor) throws Exception{
        PluginDescriptor.Type type = pluginDescriptor.getType();
        if(type == PluginDescriptor.Type.JAR || type == PluginDescriptor.Type.ZIP){
            NestedJarResourceLoader resourceLoader = new NestedJarResourceLoader(pluginDescriptor, this);
            resourceLoader.init();
            addResourceLoader(resourceLoader);
        } else if(type == PluginDescriptor.Type.DIR_OF_DEV || type == PluginDescriptor.Type.DIR_OF_PROD){
            addClasspath(pluginDescriptor);
            addLibFile(pluginDescriptor);
        }
    }

    private void addClasspath(PluginDescriptor pluginDescriptor) throws Exception {
        String pluginClassPath = pluginDescriptor.getPluginClassPath();
        File existFile = PluginFileUtils.getExistFile(pluginClassPath);
        if(existFile != null){
            addResource(existFile);
        }
    }

    private void addLibFile(PluginDescriptor pluginDescriptor) throws Exception {
        Set<String> pluginLibPaths = pluginDescriptor.getPluginLibPaths();
        if(ObjectUtils.isEmpty(pluginLibPaths)){
            return;
        }
        for (String pluginLibPath : pluginLibPaths) {
            File existFile = PluginFileUtils.getExistFile(pluginLibPath);
            if(existFile != null){
                addResource(existFile);
            }
        }
    }

}
