/**
 * Copyright [2019-2022] [starBlues]
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.gitee.starblues.common;

import java.util.jar.Attributes;

/**
 * Manifest-Key
 *
 * @author starBlues
 * @version 3.0.0
 */
public class ManifestKey {

    /**
     * Manifest version
     */
    public static final String MANIFEST_VERSION = "Manifest-Version";

    /**
     * Manifest-version: 1.0
     */
    public static final String MANIFEST_VERSION_1_0 = "1.0";

    /**
     * plugin meta path
     */
    public static final String PLUGIN_META_PATH = "Plugin-Meta-Path";

    /**
     * plugin package type
     */
    public static final String PLUGIN_PACKAGE_TYPE = "Plugin-Package-Type";


    /**
     * main class
     */
    public static final String MAIN_CLASS = "Main-Class";

    /**
     * jar in jar: main class value
     */
    public static final String MAIN_CLASS_VALUE = "com.gitee.starblues.loader.launcher.SpringMainProdBootstrap";

    /**
     * jar in jar: start class
     */
    public static final String START_CLASS = "Start-Class";

    /**
     * jar class path
     */
    public static final String CLASS_PATH = "Class-Path";



    /**
     * 获取值
     *
     * @param attributes attributes
     * @param key 获取的key
     * @return 获取的值
     */
    public static String getValue(Attributes attributes, String key){
        try {
            return attributes.getValue(key);
        } catch (Throwable e){
            // 忽略
            return null;
        }
    }


}
