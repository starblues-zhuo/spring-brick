package com.plugin.development.annotation;

import java.lang.annotation.*;

/**
 * @Description: 某类中要使用 @Autowired(required = false) 注入主程序中的bean, 则需要将该注解定义到该类上
 * @Author: zhangzhuo
 * @Version: 1.0
 * @Create Date Time: 2019-05-30 11:11
 * @Update Date Time:
 * @see
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApplyMainBean {

}
