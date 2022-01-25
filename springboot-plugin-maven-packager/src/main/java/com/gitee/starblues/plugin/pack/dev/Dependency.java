package com.gitee.starblues.plugin.pack.dev;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * @author starBlues
 * @version 1.0
 */
@Data
public class Dependency {

    @Parameter(required = true)
    private String groupId;

    @Parameter(required = true)
    private String artifactId;

    @Parameter(required = true)
    private String classesPath;

}
