package com.gitee.starblues.utils;

import java.lang.annotation.Annotation;

/**
 * 注解工具
 *
 * @author zhangzhuo
 * @version 1.0
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
