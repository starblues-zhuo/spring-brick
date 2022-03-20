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

package com.gitee.starblues.bootstrap.utils;


import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 注解工具类
 * @author starBlues
 * @version 3.0.0
 */
public class AnnotationUtils {


    private AnnotationUtils(){}

    public static <A extends Annotation> A findAnnotation(Method method, Class<A> annotationType) {
        return org.springframework.core.annotation.AnnotationUtils.findAnnotation(method, annotationType);
    }


    public static <A extends Annotation> A findAnnotation(Class<?> clazz, Class<A> annotationType) {
        return org.springframework.core.annotation.AnnotationUtils.findAnnotation(clazz, annotationType);
    }

    public static <A extends Annotation> boolean existOr(Class<?> clazz, Class<A>[] annotationTypes) {
        for (Class<A> annotationType : annotationTypes) {
            A annotation = findAnnotation(clazz, annotationType);
            if(annotation != null){
                return true;
            }
        }
        return false;
    }

    public static <A extends Annotation> boolean existAnd(Class<?> clazz, Class<A>[] annotationTypes) {
        for (Class<A> annotationType : annotationTypes) {
            A annotation = findAnnotation(clazz, annotationType);
            if(annotation == null){
                return false;
            }
        }
        return true;
    }

}
