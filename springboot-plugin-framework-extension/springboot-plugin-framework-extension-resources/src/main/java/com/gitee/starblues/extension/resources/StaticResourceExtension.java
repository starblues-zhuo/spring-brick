package com.gitee.starblues.extension.resources;

import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.extension.resources.resolver.ResourceWebMvcConfigurer;
import com.gitee.starblues.factory.process.post.PluginPostProcessorExtend;
import org.springframework.context.ApplicationContext;
import org.springframework.web.servlet.config.annotation.DelegatingWebMvcConfiguration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.ArrayList;
import java.util.List;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class StaticResourceExtension extends AbstractExtension {

    private final static String KEY = "StaticResourceExtension";

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public void initialize(ApplicationContext applicationContext) throws Exception{
        WebMvcConfigurer webMvcConfigurer = new ResourceWebMvcConfigurer(applicationContext);
        List<WebMvcConfigurer> webMvcConfigurers = new ArrayList<>();
        webMvcConfigurers.add(webMvcConfigurer);
        DelegatingWebMvcConfiguration support =
                applicationContext.getBean(DelegatingWebMvcConfiguration.class);
        support.setConfigurers(webMvcConfigurers);
    }


    @Override
    public List<PluginPostProcessorExtend> getPluginPostProcessor(ApplicationContext applicationContext) {
        final List<PluginPostProcessorExtend> pluginPostProcessorExtends = new ArrayList<>();
        pluginPostProcessorExtends.add(new PluginHandlerMappingProcess());
        return pluginPostProcessorExtends;
    }
}
