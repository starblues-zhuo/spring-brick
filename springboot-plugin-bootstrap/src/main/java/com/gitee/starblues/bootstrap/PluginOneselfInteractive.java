package com.gitee.starblues.bootstrap;

import com.gitee.starblues.common.PackageStructure;
import com.gitee.starblues.core.descriptor.*;
import com.gitee.starblues.core.launcher.plugin.PluginInteractive;
import com.gitee.starblues.integration.AutoIntegrationConfiguration;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.MainApplicationContext;
import com.gitee.starblues.spring.extract.DefaultOpExtractFactory;
import com.gitee.starblues.spring.extract.OpExtractFactory;
import com.gitee.starblues.spring.invoke.DefaultInvokeSupperCache;
import com.gitee.starblues.spring.invoke.InvokeSupperCache;
import com.gitee.starblues.utils.CommonUtils;

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 插件自己的Interactive
 * @author starBlues
 * @version 3.0.0
 */
public class PluginOneselfInteractive implements PluginInteractive {

    private final InsidePluginDescriptor pluginDescriptor;
    private final MainApplicationContext mainApplicationContext;
    private final IntegrationConfiguration configuration;
    private final InvokeSupperCache invokeSupperCache;
    private final OpExtractFactory opExtractFactory;

    public PluginOneselfInteractive(){
        this.pluginDescriptor = createPluginDescriptor();
        this.mainApplicationContext = new EmptyMainApplicationContext();
        this.configuration = new AutoIntegrationConfiguration();
        this.invokeSupperCache = new DefaultInvokeSupperCache();
        this.opExtractFactory = new DefaultOpExtractFactory();
    }

    @Override
    public InsidePluginDescriptor getPluginDescriptor() {
        return pluginDescriptor;
    }

    @Override
    public MainApplicationContext getMainApplicationContext() {
        return mainApplicationContext;
    }

    @Override
    public IntegrationConfiguration getConfiguration() {
        return configuration;
    }

    @Override
    public InvokeSupperCache getInvokeSupperCache() {
        return invokeSupperCache;
    }

    @Override
    public OpExtractFactory getOpExtractFactory() {
        return opExtractFactory;
    }

    private InsidePluginDescriptor createPluginDescriptor(){
        try (PluginDescriptorLoader pluginDescriptorLoader = new DevPluginDescriptorLoader()){
            Path classesPath = Paths.get(this.getClass().getResource("/").toURI()).getParent();
            String metaInf = CommonUtils.joiningFilePath(classesPath.toString(), PackageStructure.META_INF_NAME);
            InsidePluginDescriptor pluginDescriptor = pluginDescriptorLoader.load(Paths.get(metaInf));
            if(pluginDescriptor == null){
                throw new RuntimeException("没有发现插件信息, 请使用框架提供的Maven插件器对插件进行编译!");
            }
            return pluginDescriptor;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
