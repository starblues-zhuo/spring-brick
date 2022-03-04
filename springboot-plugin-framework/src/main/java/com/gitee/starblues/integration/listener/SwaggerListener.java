/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

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

    @Override
    public void stopSuccess(PluginInfo pluginInfo) {
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
