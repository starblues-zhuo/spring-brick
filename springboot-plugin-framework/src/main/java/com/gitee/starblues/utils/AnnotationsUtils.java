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

import java.lang.annotation.Annotation;

/**
 * 注解工具
 *
 * @author starBlues
 * @version 3.0.0
 */
public class AnnotationsUtils {

    private AnnotationsUtils(){

    }


    /**
     * 存在注解判断
     * @param aClass 类
     * @param isAllMatch 是否匹配全部注解
     * @param annotationClasses 注解类
     * @return boolean
     */
    @SafeVarargs
    public static boolean haveAnnotations(Class<?> aClass, boolean isAllMatch,
                                          Class<? extends Annotation> ...annotationClasses){
        if(aClass == null){
            return false;
        }
        if(annotationClasses == null){
            return false;
        }
        for (Class<? extends Annotation> annotationClass : annotationClasses) {
            Annotation annotation = aClass.getAnnotation(annotationClass);
            if(isAllMatch){
                if(annotation == null){
                    return false;
                }
            } else {
                if(annotation != null){
                    return true;
                }
            }
        }
        if(isAllMatch){
            return true;
        } else {
            return false;
        }
    }

}
