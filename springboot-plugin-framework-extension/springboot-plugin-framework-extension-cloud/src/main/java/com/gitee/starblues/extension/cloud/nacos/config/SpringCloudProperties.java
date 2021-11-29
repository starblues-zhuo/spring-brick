package com.gitee.starblues.extension.cloud.nacos.config;

/**
 * @author starBlues
 * @version 2.4.5
 */
public class SpringCloudProperties {

    public static final String PREFIX = "spring-cloud";

    private NacosProperties nacos;

    public NacosProperties getNacos() {
        return nacos;
    }

    public void setNacos(NacosProperties nacos) {
        this.nacos = nacos;
    }
}
