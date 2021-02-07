package com.gitee.starblues.extension.resources;

import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.extension.resources.resolver.ResourceWebMvcConfigurer;
import com.gitee.starblues.extension.resources.thymeleaf.ThymeleafProcessor;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.factory.process.post.PluginPostProcessorExtend;
import org.springframework.context.ApplicationContext;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 插件静态资源访问的扩展插件
 *
 * @author starBlues
 * @version 2.3
 */
public class StaticResourceExtension extends AbstractExtension {

    private final static String KEY = "StaticResourceExtension";

    /**
     * 访问插件静态资源前缀。默认为: static-plugin。
     */
    private static String pluginStaticResourcePathPrefix = "static-plugin";

    /**
     * 访问静态资源的缓存控制。默认最大1小时。主要针对http协议的缓存。
     */
    private static CacheControl pluginStaticResourcesCacheControl =
            CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic();

    private final Set<Include> includes = new HashSet<>(1);

    public StaticResourceExtension(){
    }

    public StaticResourceExtension(Include... includes){
        if(includes != null){
            this.includes.addAll(Arrays.asList(includes));
        }
    }

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public void initialize(ApplicationContext mainApplicationContext) throws Exception{
        WebMvcConfigurer webMvcConfigurer = new ResourceWebMvcConfigurer();
        List<WebMvcConfigurer> webMvcConfigurers = new ArrayList<>();
        webMvcConfigurers.add(webMvcConfigurer);
        DelegatingWebMvcConfiguration support =
                mainApplicationContext.getBean(DelegatingWebMvcConfiguration.class);
        support.setConfigurers(webMvcConfigurers);
    }

    @Override
    public List<PluginPipeProcessorExtend> getPluginPipeProcessor(ApplicationContext mainApplicationContext) {
        if(includes.contains(Include.THYMELEAF)){
            final List<PluginPipeProcessorExtend> pluginPipeProcessorExtends = new ArrayList<>(1);
            pluginPipeProcessorExtends.add(new ThymeleafProcessor());
            return pluginPipeProcessorExtends;
        }
        return null;
    }

    @Override
    public List<PluginPostProcessorExtend> getPluginPostProcessor(ApplicationContext mainApplicationContext) {
        final List<PluginPostProcessorExtend> pluginPostProcessorExtends = new ArrayList<>();
        pluginPostProcessorExtends.add(new PluginResourceResolverProcess());
        return pluginPostProcessorExtends;
    }

    /**
     * 设置访问插件静态资源前缀
     * @param pluginStaticResourcePathPrefix 静态资源前缀。默认为: static-plugin。
     */
    public void setPathPrefix(String pluginStaticResourcePathPrefix){
        if(pluginStaticResourcePathPrefix != null && !"".equals(pluginStaticResourcePathPrefix)){
            StaticResourceExtension.pluginStaticResourcePathPrefix = pluginStaticResourcePathPrefix;
        }
    }

    /**
     * 设置缓存控制
     * @param pluginStaticResourcesCacheControl 访问静态资源的缓存控制。默认最大1小时。主要针对http协议的缓存。
     */
    public void setCacheControl(CacheControl pluginStaticResourcesCacheControl){
        if(pluginStaticResourcesCacheControl == null){
            StaticResourceExtension.pluginStaticResourcesCacheControl = null;
        } else {
            StaticResourceExtension.pluginStaticResourcesCacheControl = pluginStaticResourcesCacheControl;
        }
    }

    public static String getPluginStaticResourcePathPrefix() {
        return pluginStaticResourcePathPrefix;
    }

    public static CacheControl getPluginStaticResourcesCacheControl() {
        return pluginStaticResourcesCacheControl;
    }


    public enum Include{
        THYMELEAF
    }
}
