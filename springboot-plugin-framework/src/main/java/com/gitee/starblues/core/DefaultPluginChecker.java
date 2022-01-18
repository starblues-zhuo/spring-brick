package com.gitee.starblues.core;

import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.utils.Assert;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginChecker implements PluginChecker{


    @Override
    public void check(Path path) throws Exception {
        if(path == null){
            throw new FileNotFoundException("path 文件路径不能为空");
        }
        if(Files.notExists(path)){
            throw new FileNotFoundException("不存在文件: " + path.toString());
        }
    }

    @Override
    public void check(PluginDescriptor descriptor) throws Exception {
        Assert.isNotNull(descriptor, "PluginDescriptor 不能为空");

        Assert.isNotEmpty(descriptor.getPluginPath(),
                "插件路径[pluginDescriptor->getPluginPath] 不能为空");

        Assert.isNotNull(descriptor.getPluginId(),
                "插件id[pluginDescriptor->getPluginId] 不能为空");

        Assert.isNotNull(descriptor.getPluginBootstrapClass(),
                "插件id[pluginDescriptor->getPluginBootstrapClass] 不能为空");

        Assert.isNotNull(descriptor.getPluginVersion(),
                "插件id[pluginDescriptor->getPluginVersion] 不能为空");
    }
}
