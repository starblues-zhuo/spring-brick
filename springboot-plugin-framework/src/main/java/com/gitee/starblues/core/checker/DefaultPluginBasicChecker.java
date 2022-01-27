package com.gitee.starblues.core.checker;

import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.common.PluginDescriptorKey;
import com.gitee.starblues.core.descriptor.PluginDescriptor;
import com.gitee.starblues.core.exception.PluginException;
import com.gitee.starblues.utils.Assert;

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 默认的基本检查者
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultPluginBasicChecker implements PluginBasicChecker {


    @Override
    public void checkPath(Path path) throws Exception {
        if(path == null){
            throw new FileNotFoundException("path 文件路径不能为空");
        }
        if(Files.notExists(path)){
            throw new FileNotFoundException("不存在文件: " + path.toString());
        }
    }

    @Override
    public void checkDescriptor(PluginDescriptor descriptor) throws PluginException {
        Assert.isNotNull(descriptor, "PluginDescriptor 不能为空");

        Assert.isNotEmpty(descriptor.getPluginPath(), "pluginPath 不能为空");

        Assert.isNotNull(descriptor.getPluginId(),
                PluginDescriptorKey.PLUGIN_ID + "不能为空");

        Assert.isNotNull(descriptor.getPluginBootstrapClass(),
                PluginDescriptorKey.PLUGIN_BOOTSTRAP_CLASS + "不能为空");

        Assert.isNotNull(descriptor.getPluginVersion(),
                PluginDescriptorKey.PLUGIN_VERSION + "不能为空");

        String illegal = PackageStructure.getIllegal(descriptor.getPluginId());
        if(illegal != null){
            throw new PluginException(descriptor, "插件id不能包含:" + illegal);
        }
        illegal = PackageStructure.getIllegal(descriptor.getPluginVersion());
        if(illegal != null){
            throw new PluginException(descriptor, "插件版本号不能包含:" + illegal);
        }
    }


}
