package com.gitee.starblues.integration.operator;

import com.gitee.starblues.core.PluginException;
import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.integration.listener.PluginInitializerListener;
import com.gitee.starblues.integration.operator.upload.UploadParam;
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
    public PluginInfo parse(Path pluginPath) throws PluginException {
        return null;
    }

    @Override
    public PluginInfo install(Path jarPath, boolean unpackPlugin) throws PluginException {
        return null;
    }

    @Override
    public void uninstall(String pluginId, boolean isDelete, boolean isBackup) throws PluginException {
    }

    @Override
    public PluginInfo load(Path jarPath, boolean unpackPlugin) throws PluginException {
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
    public PluginInfo uploadPlugin(UploadParam uploadParam) throws PluginException {
        return null;
    }

    @Override
    public Path backupPlugin(Path backDirPath, String sign) throws PluginException {
        return null;
    }

    @Override
    public Path backupPlugin(String pluginId, String sign) throws PluginException {
        return null;
    }

    @Override
    public List<PluginInfo> getPluginInfo() {
        return null;
    }

    @Override
    public PluginInfo getPluginInfo(String pluginId) {
        return null;
    }
}
