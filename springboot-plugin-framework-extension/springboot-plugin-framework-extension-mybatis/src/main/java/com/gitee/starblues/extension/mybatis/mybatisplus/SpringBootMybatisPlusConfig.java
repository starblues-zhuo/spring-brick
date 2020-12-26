package com.gitee.starblues.extension.mybatis.mybatisplus;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.gitee.starblues.extension.mybatis.MybatisCommonConfig;

/**
 * springboot mybatis plus 配置接口
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-12-11
 */
public interface SpringBootMybatisPlusConfig extends MybatisCommonConfig {



    /**
     * 插件自主配置Mybatis-Plus的MybatisSqlSessionFactoryBean
     * MybatisSqlSessionFactoryBean 具体配置说明参考 Mybatis-plus 官网
     * @param sqlSessionFactoryBean MybatisSqlSessionFactoryBean
     */
    default void oneselfConfig(MybatisSqlSessionFactoryBean sqlSessionFactoryBean){
    }


}
