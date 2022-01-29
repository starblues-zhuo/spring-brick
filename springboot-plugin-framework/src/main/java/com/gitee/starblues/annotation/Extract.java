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

import org.springframework.core.annotation.AliasFor;
import org.springframework.stereotype.Component;

import java.lang.annotation.*;

/**
 * 基于业务的扩展注解
 * @author starBlues
 * @version 2.4.4
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Extract {

    /**
     * 指定 Component Bean 名称
     * @return component name
     */
    @AliasFor(annotation = Component.class)
    String value() default "";

    /**
     * 业务
     * @return 业务标志
     */
    String bus();

    /**
     * 场景
     * @return 场景标志
     */
    String scene() default "";

    /**
     * 用例
     * @return 用例标志
     */
    String useCase() default "";

    /**
     * 不同插件存在同一业务时, 用于指定优先级别. 数字越大, 优先级别越高
     * @return 优先级别
     */
    int order() default 0;

}
