package com.gitee.starblues.plugin.pack;

import com.gitee.starblues.common.AbstractDependencyPlugin;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 依赖的插件
 * @author starBlues
 * @version 3.0.0
 */
public class DependencyPlugin extends AbstractDependencyPlugin {

    @Parameter(required = true)
    private String id;

    @Parameter(required = true)
    private String version;

    @Parameter(required = false, defaultValue = "true")
    private Boolean optional = false;

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public Boolean getOptional() {
        if(optional == null){
            return false;
        }
        return optional;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setVersion(String version) {
        this.version = version;
    }

    @Override
    public void setOptional(Boolean optional) {
        this.optional = optional;
    }

}
