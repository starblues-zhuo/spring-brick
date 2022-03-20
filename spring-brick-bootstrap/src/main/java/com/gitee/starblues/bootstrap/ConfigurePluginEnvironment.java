/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gitee.starblues.bootstrap;

import com.gitee.starblues.bootstrap.processor.ProcessorContext;
import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.integration.AutoIntegrationConfiguration;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.FilesUtils;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.PluginFileUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 插件环境配置
 * @author starBlues
 * @version 3.0.0
 */
class ConfigurePluginEnvironment {

    private final static String PLUGIN_PROPERTY_NAME = "pluginPropertySources";

    private final static String SPRING_CONFIG_NAME = "spring.config.name";
    private final static String SPRING_CONFIG_LOCATION = "spring.config.location";

    private final static String SPRING_JMX_UNIQUE_NAMES = "spring.jmx.unique-names";
    private final static String SPRING_ADMIN_JMX_NAME = "spring.application.admin.jmx-name";
    private final static String SPRING_ADMIN_JMX_VALUE = "org.springframework.boot:type=Admin,name=";

    public static final String REGISTER_SHUTDOWN_HOOK_PROPERTY = "logging.register-shutdown-hook";
    public static final String MBEAN_DOMAIN_PROPERTY_NAME = "spring.liveBeansView.mbeanDomain";

    private final ProcessorContext processorContext;
    private final InsidePluginDescriptor pluginDescriptor;

    ConfigurePluginEnvironment(ProcessorContext processorContext) {
        this.processorContext = Assert.isNotNull(processorContext, "processorContext 不能为空");
        this.pluginDescriptor = Assert.isNotNull(processorContext.getPluginDescriptor(),
                "pluginDescriptor 不能为空");
    }

    void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
        Map<String, Object> env = new HashMap<>();
        String pluginId = pluginDescriptor.getPluginId();
        String configFileName = pluginDescriptor.getConfigFileName();
        if(!ObjectUtils.isEmpty(configFileName)){
            env.put(SPRING_CONFIG_NAME, PluginFileUtils.getFileName(configFileName));
        }
        String configFileLocation = pluginDescriptor.getConfigFileLocation();
        if(!ObjectUtils.isEmpty(configFileLocation)){
            env.put(SPRING_CONFIG_LOCATION, getConfigFileLocation(configFileLocation));
        }
        env.put(AutoIntegrationConfiguration.ENABLE_STARTER_KEY, false);
        env.put(SPRING_JMX_UNIQUE_NAMES, true);
        env.put(SPRING_ADMIN_JMX_NAME, SPRING_ADMIN_JMX_VALUE + pluginId);
        env.put(REGISTER_SHUTDOWN_HOOK_PROPERTY, false);
        env.put(MBEAN_DOMAIN_PROPERTY_NAME, pluginId);
        environment.getPropertySources().addFirst(new MapPropertySource(PLUGIN_PROPERTY_NAME, env));

        if(processorContext.runMode() == ProcessorContext.RunMode.ONESELF){
            ConfigureMainPluginEnvironment configureMainPluginEnvironment =
                    new ConfigureMainPluginEnvironment(processorContext);
            configureMainPluginEnvironment.configureEnvironment(environment, args);
        }
    }

    private String getConfigFileLocation(String configFileLocation){
        String s = FilesUtils.resolveRelativePath(new File("").getAbsolutePath(), configFileLocation);
        if(s.endsWith("/") || s.endsWith(File.separator)){
            return s;
        } else {
            return s + File.separator;
        }
    }

}
