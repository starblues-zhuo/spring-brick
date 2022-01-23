package com.gitee.starblues.spring;

import java.util.Set;

/**
 * 插件中对web的配置
 * @author starBlues
 * @version 3.0.0
 */
public class WebConfig {

    private boolean enable = false;
    private Set<String> resourceLocations = null;

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public Set<String> getResourceLocations() {
        return resourceLocations;
    }

    public void setResourceLocations(Set<String> resourceLocations) {
        this.resourceLocations = resourceLocations;
    }
}
