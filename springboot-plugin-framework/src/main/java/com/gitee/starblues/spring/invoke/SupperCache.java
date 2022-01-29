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

package com.gitee.starblues.spring.invoke;

import com.gitee.starblues.spring.ApplicationContext;

/**
 * 提供者缓存包装
 * @author starBlues
 * @version 3.0.0
 */
public class SupperCache {

    private final String supperKey;
    private final String beanName;
    private final ApplicationContext applicationContext;

    public SupperCache(String supperKey, String beanName, ApplicationContext applicationContext) {
        this.supperKey = supperKey;
        this.beanName = beanName;
        this.applicationContext = applicationContext;
    }

    public String getSupperKey() {
        return supperKey;
    }

    public String getBeanName() {
        return beanName;
    }

    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

}
