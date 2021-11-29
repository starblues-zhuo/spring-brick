package com.gitee.starblues.extension.cloud.nacos.config;

import java.util.List;

/**
 * @author starBlues
 * @version 2.4.5
 */
public class NacosProperties {

    private List<NacosConfigProperties> configs;

    private Integer timeout = 1000;

    public List<NacosConfigProperties> getConfigs() {
        return configs;
    }

    public void setConfigs(List<NacosConfigProperties> configs) {
        this.configs = configs;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }
}
