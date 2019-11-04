package com.gitee.starblues.extension.resources.resolver;

import com.gitee.starblues.extension.resources.StaticResourceConfig;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.operator.PluginOperator;
import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.PluginWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.resource.AbstractResourceResolver;
import org.springframework.web.servlet.resource.ResourceResolverChain;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginResourceResolver extends AbstractResourceResolver {

    private final static Logger logger = LoggerFactory.getLogger(PluginResourceConfig.class);


    private final static Map<String, PluginResourceConfig> pluginResourceConfigMap = new ConcurrentHashMap<>();

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

            PluginResourceConfig pluginResourceConfig = pluginResourceConfigMap.get(pluginId);

            if(pluginResourceConfig == null){
                return null;
            }

            Resource resource = resolveClassPath(pluginResourceConfig, partialPath);
            if(resource != null){
                pluginResourceConfig.add(resource);
                return resource;
            }

            resource = resolveFilePath(pluginResourceConfig, partialPath);
            if(resource != null){
                pluginResourceConfig.add(resource);
                return resource;
            }
            return null;

        }
        return chain.resolveResource(request, requestPath, locations);
    }

    private Resource resolveClassPath(PluginResourceConfig pluginResourceConfig,
                                      String partialPath){
        Set<String> classPaths = pluginResourceConfig.getClassPaths();
        if(classPaths == null || classPaths.isEmpty()){
            return null;
        }

        PluginWrapper pluginWrapper = pluginResourceConfig.getPluginWrapper();
        if(pluginWrapper == null){
            return null;
        }

        ResourcePatternResolver resourcePatternResolver =
                new PathMatchingResourcePatternResolver(pluginWrapper.getPluginClassLoader());

        for (String classPath : classPaths) {
            try {
                Resource resource = resourcePatternResolver.getResource(classPath + partialPath);
                if(resource != null && resource.exists()){
                    return resource;
                }
            } catch (Exception e){
                e.printStackTrace();
                logger.debug(e.getMessage(), e);
            }
        }
        return null;
    }

    private Resource resolveFilePath(PluginResourceConfig pluginResourceConfig, String partialPath) {
        Set<String> filePaths = pluginResourceConfig.getFilePaths();
        if(filePaths == null || filePaths.isEmpty()){
            return null;
        }

        for (String filePath : filePaths) {
            Path path = Paths.get(filePath + partialPath);
            if(Files.exists(path)){
                try {
                    FileUrlResource fileUrlResource = new FileUrlResource(path.toString());
                    return fileUrlResource;
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
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

    public static synchronized void remove(String pluginId){
        PluginResourceConfig pluginResourceConfig = pluginResourceConfigMap.get(pluginId);
        if(pluginResourceConfig == null){
            return;
        }
        pluginResourceConfigMap.remove(pluginId);
        List<Resource> resources = pluginResourceConfig.getResources();
        if(resources != null && !resources.isEmpty()){
            for (Resource resource : resources) {
                try {
                    resource.getInputStream().close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

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
                if(!path.startsWith("/")){
                    path =  "/" + path;
                }
                if(!path.endsWith("/")){
                    path =  path + "/";
                }
                classPaths.add(path);
            } else if("file".equalsIgnoreCase(type)){
                filePaths.add(path);
            } else {
                logger.warn("The plugin '{}' type '{}' cannot be resolved", pluginId, type);
            }
        }

        PluginResourceConfig pluginResourceConfig = new PluginResourceConfig();
        pluginResourceConfig.setClassPaths(classPaths);
        pluginResourceConfig.setFilePaths(filePaths);
        pluginResourceConfig.setPluginWrapper(basePlugin.getWrapper());
        pluginResourceConfigMap.put(pluginId, pluginResourceConfig);
    }


    private static class PluginResourceConfig {



        private PluginWrapper pluginWrapper;
        private Set<String> classPaths;
        private Set<String> filePaths;
        private List<Resource> resources = new ArrayList<>();


        PluginWrapper getPluginWrapper() {
            return pluginWrapper;
        }

        void setPluginWrapper(PluginWrapper pluginWrapper) {
            this.pluginWrapper = pluginWrapper;
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

        void add(Resource resource){
            if(resource != null){
                resources.add(resource);
            }
        }

        List<Resource> getResources() {
            return resources;
        }
    }


}
