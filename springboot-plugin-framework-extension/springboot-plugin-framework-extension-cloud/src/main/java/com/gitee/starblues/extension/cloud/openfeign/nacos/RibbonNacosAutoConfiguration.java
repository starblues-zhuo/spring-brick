package com.gitee.starblues.extension.cloud.openfeign.nacos;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cloud.netflix.ribbon.RibbonAutoConfiguration;
import org.springframework.cloud.netflix.ribbon.RibbonClients;
import org.springframework.context.annotation.Configuration;

/**
 * @author zhangzhuo@acoinfo.com
 * @version 1.0
 * @date 2021-11-05
 */
@Configuration
//@EnableConfigurationProperties
//@ConditionalOnBean(SpringClientFactory.class)
//@ConditionalOnRibbonNacos
//@ConditionalOnNacosDiscoveryEnabled
@AutoConfigureAfter(RibbonAutoConfiguration.class)
@RibbonClients(defaultConfiguration = {
        NacosRibbonClientConfiguration.class
})
public class RibbonNacosAutoConfiguration {
}
