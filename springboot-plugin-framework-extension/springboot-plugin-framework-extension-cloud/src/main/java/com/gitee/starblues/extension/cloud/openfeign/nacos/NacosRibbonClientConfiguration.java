package com.gitee.starblues.extension.cloud.openfeign.nacos;

import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.ribbon.NacosServerIntrospector;
import com.alibaba.cloud.nacos.ribbon.NacosServerList;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ServerList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.cloud.netflix.ribbon.PropertiesFactory;
import org.springframework.context.annotation.Bean;

/**
 * @author zhangzhuo@acoinfo.com
 * @version 1.0
 * @date 2021-11-05
 */
public class NacosRibbonClientConfiguration {

    @Autowired
    private PropertiesFactory propertiesFactory;

    @Bean
    @ConditionalOnMissingBean
    public ServerList<?> ribbonServerList(IClientConfig config,
                                          NacosDiscoveryProperties nacosDiscoveryProperties) {
        if (this.propertiesFactory.isSet(ServerList.class, config.getClientName())) {
            ServerList serverList = this.propertiesFactory.get(ServerList.class, config,
                    config.getClientName());
            return serverList;
        }
        NacosServerList serverList = new NacosServerList(nacosDiscoveryProperties);
        serverList.initWithNiwsConfig(config);
        return serverList;
    }

    @Bean
    @ConditionalOnMissingBean
    public NacosServerIntrospector nacosServerIntrospector() {
        return new NacosServerIntrospector();
    }

}
