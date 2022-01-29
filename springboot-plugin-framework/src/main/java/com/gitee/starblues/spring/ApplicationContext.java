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

package com.gitee.starblues.spring;

/**
 * 自定义ApplicationContext
 * @author starBlues
 * @version 3.0.0
 */
public interface ApplicationContext extends AutoCloseable {

    /**
     * 得到 SpringBeanFactory
     * @return SpringBeanFactory
     */
    SpringBeanFactory getSpringBeanFactory();

}
