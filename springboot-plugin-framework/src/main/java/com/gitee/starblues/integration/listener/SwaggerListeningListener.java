package com.gitee.starblues.integration.listener;

import com.gitee.starblues.utils.PluginBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;

import java.util.List;

/**
 * Swagger 监听事件
 * @author starBlues
 * @version 2.4.0
 */
public class SwaggerListeningListener implements PluginListener{

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ApplicationContext mainApplicationContext;

    public SwaggerListeningListener(ApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
    }

    @Override
    public void registry(String pluginId, boolean isInitialize) {
        if(isInitialize){
            return;
        }
        refresh();
    }

    @Override
    public void unRegistry(String pluginId) {
        refresh();
    }

    private void refresh(){
        try {
            DocumentationPluginsBootstrapper documentationPluginsBootstrapper = PluginBeanUtils.getExistBean(mainApplicationContext,
                    DocumentationPluginsBootstrapper.class);
            if(documentationPluginsBootstrapper != null){
                documentationPluginsBootstrapper.stop();
                documentationPluginsBootstrapper.start();
            } else {
                log.warn("Not found DocumentationPluginsBootstrapper, so cannot refresh swagger");
            }
        } catch (Exception e){
            // ignore
            log.warn("refresh swagger failure");
        }
    }

    @Override
    public void failure(String pluginId, Throwable throwable) {

    }
}
