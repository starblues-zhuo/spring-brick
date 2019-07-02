package com.gitee.starblues.integration.operator.verify;

import org.pf4j.*;
import org.springframework.util.StringUtils;

import java.nio.file.Path;
import java.util.Objects;

/**
 * 插件包合法校验
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginLegalVerify implements PluginVerify{

    protected final PluginDescriptorFinder pluginDescriptorFinder;

    public PluginLegalVerify(PluginDescriptorFinder pluginDescriptorFinder) {
        Objects.requireNonNull(pluginDescriptorFinder);
        this.pluginDescriptorFinder = pluginDescriptorFinder;
    }


    @Override
    public Path verify(Path path) throws PluginException {
        if(path == null){
            throw new PluginException("path can not be null");
        }
        if(!pluginDescriptorFinder.isApplicable(path)){
            // 插件包不合法
            throw new PluginException(path.toString() + " : plugin illegal");
        }
        PluginDescriptor pluginDescriptor = pluginDescriptorFinder.find(path);
        if(pluginDescriptor == null){
            throw new PluginException(path.toString() + " : Not found Plugin Descriptor");
        }
        if(StringUtils.isEmpty(pluginDescriptor.getPluginId())){
            throw new PluginException(path.toString() + " : Not found Plugin Id");
        }
        if(StringUtils.isEmpty(pluginDescriptor.getPluginClass())){
            throw new PluginException(path.toString() + " : Not found Plugin Class");
        }
        return postVerify(path, pluginDescriptor);
    }

    /**
     * 合法后的校验.可扩展校验
     * @param path 路径
     * @param pluginDescriptor 插件解析者
     * @return 返回路径
     * @throws PluginException 插件异常
     */
    protected Path postVerify(Path path, PluginDescriptor pluginDescriptor) throws PluginException{
        return path;
    }

}
