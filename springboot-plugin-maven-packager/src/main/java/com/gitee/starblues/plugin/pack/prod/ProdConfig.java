/**
 * Copyright [2019-2022] [starBlues]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
     *
     * {@link com.gitee.starblues.common.PackageType#PLUGIN_PACKAGE_TYPE_JAR}
     * {@link com.gitee.starblues.common.PackageType#PLUGIN_PACKAGE_TYPE_JAR_OUTER}
     * {@link com.gitee.starblues.common.PackageType#PLUGIN_PACKAGE_TYPE_ZIP}
     * {@link com.gitee.starblues.common.PackageType#PLUGIN_PACKAGE_TYPE_ZIP_OUTER}
     * {@link com.gitee.starblues.common.PackageType#PLUGIN_PACKAGE_TYPE_DIR}
     */
    @Parameter(required = true, defaultValue = "jar")
    private String packageType = "jar";

    /**
     * 文件名称。默认 pluginId-version-repackage
     */
    private String fileName;

    /**
     * 输出文件目录。默认target
     */
    private String outputDirectory;

}
