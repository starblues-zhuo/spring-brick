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

package com.gitee.starblues.utils;


import java.util.function.Supplier;

/**
 * 参数校验工具类
 *
 * @author starBlues
 * @version 3.0.0
 */
public abstract class Assert {

    private Assert(){};

    public static void isTrue(boolean expression, String message) {
        if (!expression) {
            throw new IllegalArgumentException(message);
        }
    }

    public static void isTrue(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
    }

    public static void state(boolean expression, String message) {
        if (!expression) {
            throw new IllegalStateException(message);
        }
    }

    public static void state(boolean expression, Supplier<String> messageSupplier) {
        if (!expression) {
            throw new IllegalStateException(nullSafeGet(messageSupplier));
        }
    }

    public static <T> T isNotNull(T t, String message) {
        if (t == null) {
            throw new IllegalArgumentException(message);
        }
        return t;
    }

    public static <T> T isNotNull(T t, Supplier<String> messageSupplier) {
        if (t == null) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
        return t;
    }

    public static <T> T isNotEmpty(T t, String message) {
        if (ObjectUtils.isEmpty(t)) {
            throw new IllegalArgumentException(message);
        }
        return t;
    }

    public static <T> T isNotEmpty(T t, Supplier<String> messageSupplier) {
        if (ObjectUtils.isEmpty(t)) {
            throw new IllegalArgumentException(nullSafeGet(messageSupplier));
        }
        return t;
    }

    private static String nullSafeGet(Supplier<String> messageSupplier) {
        return (messageSupplier != null ? messageSupplier.get() : null);
    }

}
