package com.gitee.starblues.bootstrap;

import com.gitee.starblues.core.descriptor.InsidePluginDescriptor;
import com.gitee.starblues.utils.Assert;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.PluginFileUtils;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.util.HashMap;
import java.util.Map;

/**
 * @author starBlues
 * @version 1.0
 */
class ConfigurePluginEnvironment {

    private final static String PLUGIN_PROPERTY_NAME = "pluginPropertySources";

    private final static String SPRING_CONFIG_NAME = "spring.config.name";

    private final static String SPRING_JMX_UNIQUE_NAMES = "spring.jmx.unique-names";
    private final static String SPRING_ADMIN_JMX_NAME = "spring.application.admin.jmx-name";
    private final static String SPRING_ADMIN_JMX_VALUE = "org.springframework.boot:type=Admin,name=";

    public static final String REGISTER_SHUTDOWN_HOOK_PROPERTY = "logging.register-shutdown-hook";
    public static final String MBEAN_DOMAIN_PROPERTY_NAME = "spring.liveBeansView.mbeanDomain";


    private final InsidePluginDescriptor pluginDescriptor;

    ConfigurePluginEnvironment(InsidePluginDescriptor pluginDescriptor) {
        this.pluginDescriptor = Assert.isNotNull(pluginDescriptor, "pluginDescriptor 不能为空");
    }

    void configureEnvironment(ConfigurableEnvironment environment, String[] args) {
        Map<String, Object> env = new HashMap<>();
        String pluginId = pluginDescriptor.getPluginId();
        String configFileName = pluginDescriptor.getConfigFileName();
        if(!ObjectUtils.isEmpty(configFileName)){
            env.put(SPRING_CONFIG_NAME, PluginFileUtils.getFileName(configFileName));
        }
        env.put(SPRING_JMX_UNIQUE_NAMES, true);
        env.put(SPRING_ADMIN_JMX_NAME, SPRING_ADMIN_JMX_VALUE + pluginId);
        env.put(REGISTER_SHUTDOWN_HOOK_PROPERTY, false);
        env.put(MBEAN_DOMAIN_PROPERTY_NAME, null);
        environment.getPropertySources().addLast(new MapPropertySource(PLUGIN_PROPERTY_NAME, env));
    }

}
