package com.gitee.starblues.annotation.invoke;

import java.lang.annotation.*;

/**
 * @author starBlues
 * @version 1.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface InvokeParam {

    /**
     * 自定义参数名称
     * @return 参数名称
     */
    String value();


}
