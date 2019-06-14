package com.plugin.development.annotation;

import java.lang.annotation.*;

/**
 * 某类中要使用 @Autowired(required = false) 注入主程序中的bean, 则需要将该注解定义到该类上
 * @author zhangzhuo
 * @version 1.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApplyMainBean {

}
