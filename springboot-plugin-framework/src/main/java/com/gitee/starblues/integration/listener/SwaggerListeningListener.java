package com.gitee.starblues.integration.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;

/**
 * Swagger 监听事件
 * @author starBlues
 * @version 1.0
 */
public class SwaggerListeningListener implements PluginListener{

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ApplicationContext applicationContext;

    public SwaggerListeningListener(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void registry(String pluginId) {
        refresh();
    }

    @Override
    public void unRegistry(String pluginId) {
        refresh();
    }

    private void refresh(){
        try {
            DocumentationPluginsBootstrapper documentationPluginsBootstrapper =
                    applicationContext.getBean(DocumentationPluginsBootstrapper.class);
            documentationPluginsBootstrapper.stop();
            documentationPluginsBootstrapper.start();
        } catch (Exception e){
            // ignore
            log.warn("refresh swagger failure");
        }
    }

    @Override
    public void failure(String pluginId, Throwable throwable) {

    }
}
