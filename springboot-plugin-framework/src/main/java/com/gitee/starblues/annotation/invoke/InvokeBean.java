package com.gitee.starblues.annotation.invoke;

import java.lang.annotation.*;

/**
 * @author starBlues
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InvokeBean {

    /**
     * bean 名称
     * @return String
     */
    String value();

    /**
     * 可指定具体,插件id. 可选. 如果不填, 默认会进行所有插件扫描
     * @return String
     */
    String pluginId();

}
