package com.gitee.starblues.plugin.pack.filter;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author starBlues
 * @version 1.0
 */
@Data
public abstract class FilterableDependency {

    @Parameter(required = true)
    private String groupId;

    @Parameter(required = true)
    private String artifactId;

}
