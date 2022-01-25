package com.gitee.starblues.plugin.pack.prod;

import lombok.Data;
import org.apache.maven.plugins.annotations.Parameter;

/**
 * 生产环境打包配置
 * @author starBlues
 * @version 3.0.0
 */
@Data
public class ProdConfig {


    /**
     * 打包类型。默认jar包
     */
    @Parameter(required = true, defaultValue = "jar")
    private String packageType = "jar";

    /**
     * 文件名称。默认 pluginId-version.jar
     */
    private String fileName;

    /**
     * 输出文件目录。默认target
     */
    private String outputDirectory;

    /**
     * 是否将依赖导入包中. 默认打入
     */
    @Parameter(required = true, defaultValue = "true")
    private Boolean includeDependencies = true;

    /**
     * 生成环境依赖包的路径。默认 lib
     */
    private String libPath;

}
