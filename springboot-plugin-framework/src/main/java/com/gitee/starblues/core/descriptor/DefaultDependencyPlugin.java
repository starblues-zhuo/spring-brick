package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.common.AbstractDependencyPlugin;

/**
 * 依赖的插件信息
 * @author starBlues
 * @version 3.0.0
 */
public class DefaultDependencyPlugin extends AbstractDependencyPlugin {

    private String id;
    private String version;
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
        if(optional == null){
            optional = false;
        }
        this.optional = optional;
    }
}
