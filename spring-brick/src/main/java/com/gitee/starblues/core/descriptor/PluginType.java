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

package com.gitee.starblues.core.descriptor;

import com.gitee.starblues.common.PackageType;
import com.gitee.starblues.core.RuntimeMode;
import com.gitee.starblues.core.exception.PluginException;

import java.util.Objects;

/**
 * @author starBlues
 * @version 3.0.0
 */
public enum PluginType {

    /**
     * 开发模式目录
     */
    DEV(PackageType.PLUGIN_PACKAGE_TYPE_DEV),

    /**
     * jar文件
     */
    JAR(PackageType.MAIN_PACKAGE_TYPE_JAR),

    /**
     * jar-outer 文件
     */
    JAR_OUTER(PackageType.MAIN_PACKAGE_TYPE_JAR_OUTER),

    /**
     * zip 文件
     */
    ZIP(PackageType.PLUGIN_PACKAGE_TYPE_ZIP),

    /**
     * zip-outer 文件
     */
    ZIP_OUTER(PackageType.PLUGIN_PACKAGE_TYPE_ZIP_OUTER),

    /**
     * 生产模式目录
     */
    DIR(PackageType.PLUGIN_PACKAGE_TYPE_DIR);


    public final String name;

    PluginType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static PluginType byName(String packageType){
        if(Objects.equals(packageType, PluginType.DEV.getName())){
            return PluginType.DEV;
        } else if(Objects.equals(packageType, PluginType.JAR.getName())){
            return PluginType.JAR;
        } else if(Objects.equals(packageType, PluginType.JAR_OUTER.getName())){
            return PluginType.JAR_OUTER;
        } else if(Objects.equals(packageType, PluginType.ZIP.getName())){
            return PluginType.ZIP;
        } else if(Objects.equals(packageType, PluginType.ZIP_OUTER.getName())){
            return PluginType.ZIP_OUTER;
        } else {
            throw new PluginException("不能解析'" + packageType + "'打包类型的插件");
        }
    }
}
