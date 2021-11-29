package com.gitee.starblues.core;

import com.gitee.starblues.core.classloader.DefaultMainResourceDefiner;
import com.gitee.starblues.core.descriptor.DevPluginDescriptorLoader;
import com.gitee.starblues.core.descriptor.PluginDescriptorLoader;
import com.gitee.starblues.core.descriptor.ProdPluginDescriptorLoader;
import com.gitee.starblues.core.loader.DefaultPluginLoader;
import com.gitee.starblues.core.loader.PluginLoader;
import com.gitee.starblues.core.scanner.BasePluginScanner;
import com.gitee.starblues.core.scanner.DevPathResolve;
import com.gitee.starblues.core.scanner.PluginScanner;
import com.gitee.starblues.core.scanner.ProdPathResolve;
import com.gitee.starblues.core.version.SemverVersionInspector;
import com.gitee.starblues.core.version.VersionInspector;
import com.gitee.starblues.utils.Assert;

/**
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultRealizeProvider implements RealizeProvider {

    private PluginScanner pluginScanner;
    private PluginDescriptorLoader pluginDescriptorLoader;
    private PluginLoader pluginLoader;
    private PluginChecker pluginChecker;
    private VersionInspector versionInspector;

    private final RuntimeMode runtimeMode;
    private final String mainPackage;

    public DefaultRealizeProvider(RuntimeMode runtimeMode,
                                  String mainPackage){
        this.runtimeMode = Assert.isNotNull(runtimeMode, "参数 runtimeMode 不能为空");
        this.mainPackage = Assert.isNotEmpty(mainPackage, "参数 mainPackage 不能为空");
        init();
    }

    protected void init() {
        BasePluginScanner basePluginScanner = new BasePluginScanner();
        PluginDescriptorLoader pluginDescriptorLoader = null;
        if(runtimeMode == RuntimeMode.DEV){
            basePluginScanner.setPathResolve(new DevPathResolve());
            pluginDescriptorLoader = new DevPluginDescriptorLoader();
        } else {
            basePluginScanner.setPathResolve(new ProdPathResolve());
            pluginDescriptorLoader = new ProdPluginDescriptorLoader();
        }

        setPluginScanner(basePluginScanner);
        setPluginDescriptorLoader(pluginDescriptorLoader);
        setPluginLoader(new DefaultPluginLoader(new DefaultMainResourceDefiner(mainPackage)));
        setPluginChecker(new DefaultPluginChecker());
        setVersionInspector(new SemverVersionInspector());
    }

    public void setPluginScanner(PluginScanner pluginScanner) {
        this.pluginScanner = Assert.isNotNull(pluginScanner, "pluginScanner 不能为空");
    }

    public void setPluginDescriptorLoader(PluginDescriptorLoader pluginDescriptorLoader) {
        this.pluginDescriptorLoader = Assert.isNotNull(pluginDescriptorLoader,
                "pluginDescriptorLoader 不能为空");
    }

    public void setPluginLoader(PluginLoader pluginLoader) {
        this.pluginLoader = Assert.isNotNull(pluginLoader, "pluginLoader 不能为空");
    }

    public void setPluginChecker(PluginChecker pluginChecker) {
        this.pluginChecker = Assert.isNotNull(pluginChecker, "pluginChecker 不能为空");
    }

    public void setVersionInspector(VersionInspector versionInspector) {
        this.versionInspector = Assert.isNotNull(versionInspector, "versionInspector 不能为空");
    }

    @Override
    public RuntimeMode getRuntimeMode() {
        return runtimeMode;
    }

    @Override
    public PluginScanner getPluginScanner() {
        return Assert.isNotNull(pluginScanner, "PluginScanner 实现不能为空");
    }

    @Override
    public PluginDescriptorLoader getPluginDescriptor() {
        return Assert.isNotNull(pluginDescriptorLoader, "PluginDescriptorLoader 实现不能为空");
    }

    @Override
    public PluginLoader getPluginLoader() {
        return Assert.isNotNull(pluginLoader, "PluginLoader 实现不能为空");
    }

    @Override
    public PluginChecker getPluginChecker() {
        return Assert.isNotNull(pluginChecker, "PluginChecker 实现不能为空");
    }

    @Override
    public VersionInspector getVersionInspector() {
        return Assert.isNotNull(versionInspector, "VersionInspector 实现不能为空");
    }
}
