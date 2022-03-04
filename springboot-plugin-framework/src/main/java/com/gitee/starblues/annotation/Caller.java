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

package com.gitee.starblues.annotation;


import java.lang.annotation.*;

/**
 * 调用者的注解。配合 @Supplier 注解使用, 两者结合实现插件中的方法调用。
 *
 * @author starBlues
 * @version 2.4.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Caller {

    /**
     * 调用者的全局唯一key. 也就是Supplier 中定义的key.
     * @return String
     */
    String value();

    /**
     * 可指定调用哪一个插件
     * @return 插件id
     */
    String pluginId() default "";

    /**
     * 调用者方法注解。配合 @Supper.Method 使用。如果不定义, 则以方法名称为准。
     */
    @Target({ElementType.METHOD})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface Method {
        /**
         * 方法名
         * @return String
         */
        String value();
    }

}
