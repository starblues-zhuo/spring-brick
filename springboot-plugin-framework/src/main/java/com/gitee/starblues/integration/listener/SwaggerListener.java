package com.gitee.starblues.integration.listener;

import com.gitee.starblues.core.PluginInfo;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import springfox.documentation.spring.web.plugins.DocumentationPluginsBootstrapper;

/**
 * Swagger 监听事件
 * @author starBlues
 * @version 3.0.0
 */
public class SwaggerListener implements PluginListener{

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final ApplicationContext mainApplicationContext;

    public SwaggerListener(ApplicationContext mainApplicationContext) {
        this.mainApplicationContext = mainApplicationContext;
    }

    @Override
    public void startSuccess(PluginInfo pluginInfo) {
        if(pluginInfo.isFollowSystem()){
            return;
        }
        refresh();
    }

    void refresh(){
        try {
            DocumentationPluginsBootstrapper documentationPluginsBootstrapper = SpringBeanUtils.getExistBean(mainApplicationContext,
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

}
