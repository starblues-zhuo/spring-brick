package com.gitee.starblues.integration.operator.verify;

import org.pf4j.*;

import java.nio.file.Path;
import java.util.Objects;

/**
 * 校验上传的插件包
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginUploadVerify extends PluginLegalVerify{

    private final PluginManager pluginManager;

    public PluginUploadVerify(PluginDescriptorFinder pluginDescriptorFinder,
                              PluginManager pluginManager) {
        super(pluginDescriptorFinder);
        Objects.requireNonNull(pluginManager);
        this.pluginManager = pluginManager;
    }


    @Override
    protected Path postVerify(Path path, PluginDescriptor pluginDescriptor) throws Exception {
        PluginWrapper pluginWrapper = pluginManager.getPlugin(pluginDescriptor.getPluginId());
        if(pluginWrapper == null){
            // 当前没有该插件包运行
            return path;
        }
        // 如果当前插件在当前环境存在, 则抛出异常
        PluginDescriptor runPluginDescriptor = pluginWrapper.getDescriptor();
        StringBuffer errorMsg = new StringBuffer("The plugin (")
                .append("id:<").append(runPluginDescriptor.getPluginId())
                .append("> ; version <").append(runPluginDescriptor.getVersion())
                .append("> ) is already exist in the current environment。 ")
                .append("Please uninstall the plugin, then upload and update the plugin");
        throw new Exception(errorMsg.toString());
    }
}
