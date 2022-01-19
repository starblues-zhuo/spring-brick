package com.gitee.starblues.plugin.pack;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 从主程序加载资源配置
 * @author starBlues
 * @version 3.0.0
 */
@Data
public class LoadMainResourcePattern {

    @Parameter(name = "includes")
    private String[] includes;

    @Parameter(name = "excludes")
    private String[] excludes;

}
