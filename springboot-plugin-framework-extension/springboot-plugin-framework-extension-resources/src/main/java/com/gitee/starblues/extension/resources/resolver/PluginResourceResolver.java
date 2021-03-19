package com.gitee.starblues.extension.resources.resolver;

import com.gitee.starblues.extension.resources.StaticResourceConfig;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.loader.PluginResource;
import com.gitee.starblues.realize.BasePlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 插件资源发现者
 *
 * @author starBlues
 * @version 2.4.0
 */
public class PluginResourceResolver extends AbstractResourceResolver {

    private final static Logger logger = LoggerFactory.getLogger(PluginResourceResolver.class);

    private final static String RESOLVED_RESOURCE_CACHE_KEY_PREFIX = "resolvedPluginResource:";

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
            // 先判断缓存中是否存在。
            Resource resource = pluginResource.getCacheResource(key);
            if(resource != null){
                return resource;
            }

            // 从classpath 获取资源
            resource = resolveClassPath(pluginResource, partialPath);
            if(resource != null){
                pluginResource.putCacheResource(key, resource);
                return resource;
            }

            // 从外置文件路径获取资源
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
        PluginRegistryInfo pluginRegistryInfo = pluginResource.getPluginRegistryInfo();
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        if(basePlugin == null){
            return null;
        }

        ClassLoader pluginClassLoader = pluginRegistryInfo.getPluginClassLoader();
        for (String classPath : classPaths) {
            try {
                PluginResource resource = new PluginResource(classPath + partialPath, pluginRegistryInfo);
                resource.setClassLoader(pluginClassLoader);
                if(resource.exists()){
                    return resource;
                }
            } catch (Exception e){
                logger.debug("Get static resources of classpath '{}' error.", classPath, e);
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
            Path fullPath = Paths.get(filePath + partialPath);
            if(!Files.exists(fullPath)){
                continue;
            }
            try {
                FileUrlResource fileUrlResource = new FileUrlResource(fullPath.toString());
                if(fileUrlResource.exists()){
                    return fileUrlResource;
                }
            } catch (Exception e) {
                logger.debug("Get static resources of path '{}' error.", fullPath, e);
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

    /**
     * 计算key
     * @param request request
     * @param requestPath 请求路径
     * @return 返回key
     */
    protected String computeKey(HttpServletRequest request, String requestPath) {
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

    /**
     * 根据请求获取内容code key
     * @param request request
     * @return key
     */
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
     * 每新增一个插件, 都需要调用该方法，来解析该插件的 StaticResourceConfig 配置。并将其保存到 StaticResourceConfig bean 中。
     * @param pluginRegistryInfo 插件信息
     * @param staticResourceConfig 静态资源配置
     */
    public static synchronized void parse(PluginRegistryInfo pluginRegistryInfo,
                                          StaticResourceConfig staticResourceConfig){
        if(pluginRegistryInfo == null || staticResourceConfig == null){
            return;
        }

        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();

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
        pluginResource.setPluginRegistryInfo(pluginRegistryInfo);

        logger.info("PluginResources '{}' set classpath resources: {}, set file resources: {}", pluginId,
                classPaths, filePaths);

        if(PLUGIN_RESOURCE_MAP.containsKey(pluginId)){
            // 如果存在该插件id的插件资源信息, 则先移除它
            remove(pluginId);
        }
        PLUGIN_RESOURCE_MAP.put(pluginId, pluginResource);
    }



    /**
     * 卸载插件时。调用该方法移除插件的资源信息
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
     * 插件资源解析后的信息
     */
    private static class PluginStaticResource {

        /**
         * basePlugin bean
         */
        private PluginRegistryInfo pluginRegistryInfo;

        /**
         * 定义的classpath集合
         */
        private Set<String> classPaths;

        /**
         * 定义的文件路径集合
         */
        private Set<String> filePaths;

        /**
         * 缓存的资源。key 为资源的可以。值为资源
         */
        private Map<String, Resource> cacheResourceMaps = new ConcurrentHashMap<>();

        PluginRegistryInfo getPluginRegistryInfo() {
            return pluginRegistryInfo;
        }

        void setPluginRegistryInfo(PluginRegistryInfo pluginRegistryInfo) {
            this.pluginRegistryInfo = pluginRegistryInfo;
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
