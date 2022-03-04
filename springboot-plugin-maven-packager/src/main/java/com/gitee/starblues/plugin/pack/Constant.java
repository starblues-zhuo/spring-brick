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

package com.gitee.starblues.plugin.pack;

/**
 * 静态类
 * @author starBlues
 * @version 3.0.0
 */
public class Constant {

    public static final String PACKAGING_POM = "pom";
    public static final String SCOPE_PROVIDED = "provided";
    public static final String SCOPE_COMPILE = "compile";
    public static final String SCOPE_TEST = "test";

    public static final String MODE_MAIN = "main";
    public static final String MODE_DEV = "dev";
    public static final String MODE_PROD = "prod";

    public static final String PLUGIN_METE_COMMENTS = "plugin meta configuration";

    public static boolean isPom(String packageType){
        return PACKAGING_POM.equalsIgnoreCase(packageType);
    }

    public static boolean scopeFilter(String scope){
        return SCOPE_PROVIDED.equalsIgnoreCase(scope)
                || SCOPE_TEST.equalsIgnoreCase(scope);
    }

}
