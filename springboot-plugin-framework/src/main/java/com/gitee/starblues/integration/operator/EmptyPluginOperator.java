package com.gitee.starblues.integration.operator;

import com.gitee.starblues.core.PluginException;
import com.gitee.starblues.core.PluginWrapper;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.module.PluginInfo;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 */
public class EmptyPluginOperator implements PluginOperator{

    @Override
    public boolean initPlugins(PluginInitializerListener pluginInitializerListener) throws PluginException {
        return false;
    }

    @Override
    public boolean verify(Path jarPath) throws PluginException {
        return false;
    }

    @Override
    public PluginInfo install(Path jarPath) throws PluginException {
        return null;
    }

    @Override
    public boolean uninstall(String pluginId, boolean isBackup) throws PluginException {
        return false;
    }

    @Override
    public PluginInfo load(Path jarPath) throws PluginException {
        return null;
    }

    @Override
    public PluginInfo load(MultipartFile pluginFile) throws PluginException {
        return null;
    }

    @Override
    public boolean unload(String pluginId, boolean isBackup) throws PluginException {
        return false;
    }

    @Override
    public boolean start(String pluginId) throws PluginException {
        return false;
    }

    @Override
    public boolean stop(String pluginId) throws PluginException {
        return false;
    }

    @Override
    public PluginInfo uploadPluginAndStart(MultipartFile pluginFile) throws PluginException {
        return null;
    }

    @Override
    public boolean installConfigFile(Path configFilePath) throws PluginException {
        return false;
    }

    @Override
    public boolean uploadConfigFile(MultipartFile configFile) throws PluginException {
        return false;
    }

    @Override
    public boolean backupPlugin(Path backDirPath, String sign) throws PluginException {
        return false;
    }

    @Override
    public boolean backupPlugin(String pluginId, String sign) throws PluginException {
        return false;
    }

    @Override
    public List<PluginInfo> getPluginInfo() {
        return null;
    }

    @Override
    public PluginInfo getPluginInfo(String pluginId) {
        return null;
    }

    @Override
    public Set<String> getPluginFilePaths() {
        return null;
    }

    @Override
    public List<PluginWrapper> getPluginWrapper() {
        return null;
    }

    @Override
    public PluginWrapper getPluginWrapper(String pluginId) {
        return null;
    }
}
