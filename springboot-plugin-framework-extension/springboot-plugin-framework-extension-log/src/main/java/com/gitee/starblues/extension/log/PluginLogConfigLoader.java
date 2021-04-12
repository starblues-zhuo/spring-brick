package com.gitee.starblues.extension.log;

import com.gitee.starblues.extension.log.config.SpringBootLogConfig;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.loader.PluginResourceLoader;
import com.gitee.starblues.factory.process.pipe.loader.ResourceWrapper;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.OrderPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.util.CollectionUtils;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class PluginLogConfigLoader implements PluginResourceLoader {

    private Logger log = LoggerFactory.getLogger(PluginLogConfigLoader.class);

    public static final String KEY = "SpringBootLogConfigLoader";

    private final static String TYPE_FILE = "file";
    private final static String TYPE_CLASSPATH = "classpath";
    private final static String TYPE_PACKAGE = "package";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public ResourceWrapper load(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        if (!(basePlugin instanceof SpringBootLogConfig)) {
            log.warn("Plugin '{}' not implements SpringBootLogConfig, If you need to use log in the plugin," + "Please implements SpringBootLogConfig interface", basePlugin.getWrapper().getPluginId());
            return null;
        }
        SpringBootLogConfig springBootLogConfig = (SpringBootLogConfig) basePlugin;
        Set<String> logConfigLocations = springBootLogConfig.logConfigLocations();
        if (logConfigLocations == null || logConfigLocations.isEmpty()) {
            log.warn("SpringBootLogConfig -> logConfigLocations return is empty, " + "Please check configuration");
            return new ResourceWrapper();
        }
        List<Resource> resources = new ArrayList<>();
        for (String logConfigLocation : logConfigLocations) {
            if (logConfigLocation == null || "".equals(logConfigLocation)) {
                continue;
            }
            List<Resource> loadResources = load(logConfigLocation, basePlugin);
            if (loadResources != null && !loadResources.isEmpty()) {
                resources.addAll(loadResources);
            }
        }
        ResourceWrapper resourceWrapper = new ResourceWrapper();
        resourceWrapper.addResources(resources);
        return resourceWrapper;
    }

    @Override
    public void unload(PluginRegistryInfo pluginRegistryInfo, ResourceWrapper resourceWrapper) throws Exception {

    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }

    private List<Resource> load(String logConfigLocation, BasePlugin basePlugin) throws Exception {
        String[] split = logConfigLocation.split(":");
        if (split.length != 2) {
            return null;
        }
        String type = split[0];
        String location = split[1];
        String matchLocation;
        if (Objects.equals(type, TYPE_CLASSPATH)) {
            matchLocation = location;
        } else if (Objects.equals(type, TYPE_FILE)) {

            return loadFileSystemResources(basePlugin, location);
        } else if (Objects.equals(type, TYPE_PACKAGE)) {
            matchLocation = location.replaceAll("\\.", "/");
        } else {
            log.warn("logConfigLocation {} illegal", logConfigLocation);
            return null;
        }
        return loadClassPathResources(basePlugin, matchLocation);
    }

    private List<Resource> loadClassPathResources(BasePlugin basePlugin, String matchLocation) throws Exception {
        Logger log = LoggerFactory.getLogger(getClass());
        String pluginPath = basePlugin.getWrapper().getPluginPath().toString();
        try (JarFile jarFile = new JarFile(pluginPath)) {
            Enumeration<JarEntry> entryEnumeration = jarFile.entries();
            List<JarEntry> entries = Collections.list(entryEnumeration);
            return entries.stream().filter(entry -> {
                String entryName = entry.getName();
                // 将资源规则转换为正则表达式，例如“mapper/*Mapper.xml”转换后为“mapper/.*Mapper\\.xml”
                String regex = matchLocation.replaceAll("\\\\", "/").replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*");
                return entryName.matches(regex);
            }).map(entry -> {
                try (InputStream inputStream = jarFile.getInputStream(entry);
                     ByteArrayOutputStream bos = new ByteArrayOutputStream();
                     BufferedInputStream in = new BufferedInputStream(inputStream)) {
                    int buf_size = 1024;
                    byte[] buffer = new byte[buf_size];
                    int len;
                    while (-1 != (len = in.read(buffer, 0, buf_size))) {
                        bos.write(buffer, 0, len);
                    }
                    // 这里只能用ByteArrayResource，使用InputStreamResource后续读取时会报流已关闭
                    ByteArrayResource resource = new ByteArrayResource(bos.toByteArray());
                    log.debug("Loaded plugin resource '{}'.", entry.getName());
                    return resource;
                } catch (IOException e) {
                    log.warn("Load resource failed, caused by: {}", e.toString());
                    return null;
                }
            }).filter(Objects::nonNull).collect(Collectors.toList());
        }
    }

    private List<Resource> loadFileSystemResources(BasePlugin basePlugin, String matchLocation) {
        String basePath = basePlugin.getWrapper().getPluginPath().toString().concat(File.separator).concat(basePlugin.getWrapper().getPluginId()).concat("/config/");
        String tmpPath = basePath.concat(matchLocation);
        String regex = tmpPath.replaceAll("\\\\", "/").replaceAll("\\.", "\\\\.").replaceAll("\\*", ".*");
        List<String> matchedFilePaths = new ArrayList<>();
        getMatchedFilePaths(regex, new File(basePath), matchedFilePaths);
        if (CollectionUtils.isEmpty(matchedFilePaths)) {
            return new ArrayList<>();
        }
        return matchedFilePaths.stream().map(FileSystemResource::new).collect(Collectors.toList());
    }

    private void getMatchedFilePaths(String regex, File file, List<String> matchedFilePath) {
        File[] files = file.listFiles();
        if (files == null) {
            return;
        }
        for (File subFile : files) {
            if (subFile.isDirectory()) {
                getMatchedFilePaths(regex, subFile, matchedFilePath);
            } else {
                try {
                    String filePath = subFile.getCanonicalPath();
                    filePath = filePath.replaceAll("\\\\", "/");
                    if (filePath.matches(regex)) {
                        matchedFilePath.add(subFile.getCanonicalPath());
                    }
                } catch (IOException ignored) {
                }
            }
        }
    }
}
