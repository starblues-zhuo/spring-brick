package com.gitee.starblues.extension.resources.resolver;

import com.gitee.starblues.extension.resources.StaticResourceConfig;
import com.gitee.starblues.loader.PluginResource;
import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 插件资源发现者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginResourceResolver extends AbstractResourceResolver {

    private final static Logger logger = LoggerFactory.getLogger(PluginResourceResolver.class);
    public static final String RESOLVED_RESOURCE_CACHE_KEY_PREFIX = "resolvedResource:";

    private final static Map<String, PluginStaticResource> PLUGIN_RESOURCE_MAP = new ConcurrentHashMap<>();

    PluginResourceResolver() {
    }


    @Override
    protected Resource resolveResourceInternal(HttpServletRequest request,
                                               String requestPath, List<? extends Resource> locations,
                                               ResourceResolverChain chain) {

        int startOffset = (requestPath.startsWith("/") ? 1 : 0);
        int endOffset = requestPath.indexOf('/', 1);
        if (endOffset != -1) {
            String pluginId = requestPath.substring(startOffset, endOffset);
            String partialPath = requestPath.substring(endOffset + 1);

            PluginStaticResource pluginResource = PLUGIN_RESOURCE_MAP.get(pluginId);

            if(pluginResource == null){
                return null;
            }

            String key = computeKey(request, requestPath);
            Resource resource = pluginResource.getCacheResource(key);
            if(resource != null){
                return resource;
            }

            resource = resolveClassPath(pluginResource, partialPath);
            if(resource != null){
                pluginResource.putCacheResource(key, resource);
                return resource;
            }

            resource = resolveFilePath(pluginResource, partialPath);
            if(resource != null){
                pluginResource.putCacheResource(key, resource);
                return resource;
            }
            return null;

        }
        return chain.resolveResource(request, requestPath, locations);
    }

    /**
     * 解决 ClassPath 的资源文件。也就是插件中定义的  classpath:/xx/xx/ 配置
     * @param pluginResource 插件资源配置Bean
     * @param partialPath 部分路径
     * @return 资源。没有发现则返回null
     */
    private Resource resolveClassPath(PluginStaticResource pluginResource,
                                      String partialPath){
        Set<String> classPaths = pluginResource.getClassPaths();
        if(classPaths == null || classPaths.isEmpty()){
            return null;
        }

        BasePlugin basePlugin = pluginResource.getBasePlugin();
        if(basePlugin == null){
            return null;
        }


        for (String classPath : classPaths) {
            try {
                Resource resource = new PluginResource(classPath + partialPath, basePlugin);
                if(resource.exists()){
                    return resource;
                }
            } catch (Exception e){
                e.printStackTrace();
                logger.debug(e.getMessage(), e);
            }
        }
        return null;
    }

    /**
     * 解决插件中配置的绝对文件路径的文件资源。也就是插件中定义的  file:D://xx/xx/ 配置
     * @param pluginResource 插件资源配置Bean
     * @param partialPath 部分路径
     * @return 资源。没有发现则返回null
     */
    private Resource resolveFilePath(PluginStaticResource pluginResource, String partialPath) {
        Set<String> filePaths = pluginResource.getFilePaths();
        if(filePaths == null || filePaths.isEmpty()){
            return null;
        }

        for (String filePath : filePaths) {
            Path path = Paths.get(filePath + partialPath);
            if(!Files.exists(path)){
                continue;
            }
            try {
                FileUrlResource fileUrlResource = new FileUrlResource(path.toString());
                if(fileUrlResource.exists()){
                    return fileUrlResource;
                } else {
                    return null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }

        return null;

    }




    @Override
    protected String resolveUrlPathInternal(String resourceUrlPath,
                                            List<? extends Resource> locations,
                                            ResourceResolverChain chain) {
        return null;
    }

    protected String computeKey(@Nullable HttpServletRequest request, String requestPath) {
        StringBuilder key = new StringBuilder(RESOLVED_RESOURCE_CACHE_KEY_PREFIX);
        key.append(requestPath);
        if (request != null) {
            String codingKey = getContentCodingKey(request);
            if (StringUtils.hasText(codingKey)) {
                key.append("+encoding=").append(codingKey);
            }
        }
        return key.toString();
    }

    private String getContentCodingKey(HttpServletRequest request) {
        String header = request.getHeader(HttpHeaders.ACCEPT_ENCODING);
        if (!StringUtils.hasText(header)) {
            return null;
        }
        return Arrays.stream(StringUtils.tokenizeToStringArray(header, ","))
                .map(token -> {
                    int index = token.indexOf(';');
                    return (index >= 0 ? token.substring(0, index) : token).trim().toLowerCase();
                })
                .sorted()
                .collect(Collectors.joining(","));
    }


    /**
     * 全局移除插件时调用。主要删除
     * @param pluginId 插件id
     */
    public static synchronized void remove(String pluginId){
        PluginStaticResource pluginResource = PLUGIN_RESOURCE_MAP.get(pluginId);
        if(pluginResource == null){
            return;
        }
        PLUGIN_RESOURCE_MAP.remove(pluginId);
    }


    /**
     * 新增插件时，解析该插件的 StaticResourceConfig 配置。并将其保存到 StaticResourceConfig bean 中。
     * @param basePlugin 插件信息
     */
    public static synchronized void parse(BasePlugin basePlugin){
        if(basePlugin == null){
            return;
        }

        if(!(basePlugin instanceof StaticResourceConfig)){
            return;
        }

        StaticResourceConfig staticResourceConfig = (StaticResourceConfig) basePlugin;
        String pluginId = basePlugin.getWrapper().getPluginId();

        Set<String> locations = staticResourceConfig.locations();
        if(locations == null || locations.isEmpty()){
            return;
        }

        Set<String> classPaths = new HashSet<>();
        Set<String> filePaths = new HashSet<>();

        for (String location : locations) {
            if(StringUtils.isEmpty(location)){
                continue;
            }
            final int first = location.indexOf(":");
            if(first == -1){
                logger.warn("This plugin '{}' location config '{}' cannot be resolved", pluginId, location);
                continue;
            }
            String type = location.substring(0, first);
            String path = location.substring(first+1);

            if("classpath".equalsIgnoreCase(type)){
                if(path.startsWith("/")){
                    path = path.substring(1);
                }
                if(!path.endsWith("/")){
                    path =  path + "/";
                }
                classPaths.add(path);
            } else if("file".equalsIgnoreCase(type)){
                if(!path.endsWith(File.separator)){
                    path = path + File.separator;
                }
                filePaths.add(path);
            } else {
                logger.warn("The plugin '{}' type '{}' cannot be resolved", pluginId, type);
            }
        }

        PluginStaticResource pluginResource = new PluginStaticResource();
        pluginResource.setClassPaths(classPaths);
        pluginResource.setFilePaths(filePaths);
        pluginResource.setBasePlugin(basePlugin);

        if(PLUGIN_RESOURCE_MAP.containsKey(pluginId)){
            // 如果存在该插件id的插件资源信息, 则先移除它
            remove(pluginId);
        }
        PLUGIN_RESOURCE_MAP.put(pluginId, pluginResource);
    }


    /**
     * 插件资源解析后的信息
     */
    private static class PluginStaticResource {

        private BasePlugin basePlugin;
        private Set<String> classPaths;
        private Set<String> filePaths;
        private Map<String, Resource> cacheResourceMaps = new ConcurrentHashMap<>();


        BasePlugin getBasePlugin() {
            return basePlugin;
        }

        void setBasePlugin(BasePlugin basePlugin) {
            this.basePlugin = basePlugin;
        }

        Set<String> getClassPaths() {
            return classPaths;
        }

        void setClassPaths(Set<String> classPaths) {
            this.classPaths = classPaths;
        }

        Set<String> getFilePaths() {
            return filePaths;
        }

        void setFilePaths(Set<String> filePaths) {
            this.filePaths = filePaths;
        }


        Resource getCacheResource(String key){
            return cacheResourceMaps.get(key);
        }

        void putCacheResource(String key, Resource resource){
            if(StringUtils.isEmpty(key) || resource == null){
                return;
            }
            cacheResourceMaps.put(key, resource);
        }

    }


}
