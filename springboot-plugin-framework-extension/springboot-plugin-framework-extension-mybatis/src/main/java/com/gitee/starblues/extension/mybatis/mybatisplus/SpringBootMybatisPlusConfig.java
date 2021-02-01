package com.gitee.starblues.extension.mybatis.mybatisplus;

import com.baomidou.mybatisplus.core.MybatisConfiguration;
import com.baomidou.mybatisplus.core.config.GlobalConfig;
import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.gitee.starblues.extension.mybatis.MybatisCommonConfig;

/**
 * springboot mybatis plus 配置接口
 * @author starBlues
 * @version 2.3
 */
public interface SpringBootMybatisPlusConfig extends MybatisCommonConfig {



    /**
     * 插件自主配置Mybatis-Plus的MybatisSqlSessionFactoryBean
     * MybatisSqlSessionFactoryBean 具体配置说明参考 Mybatis-plus 官网
     * @param sqlSessionFactoryBean MybatisSqlSessionFactoryBean
     */
    default void oneselfConfig(MybatisSqlSessionFactoryBean sqlSessionFactoryBean){
    }

    /**
     * 重写设置配置
     * 只有 enableOneselfConfig 返回 false, 实现该方法才生效
     * @param configuration 当前 MybatisConfiguration
     * @param globalConfig 当前全局配置GlobalConfig
     */
    default void reSetMainConfig(MybatisConfiguration configuration, GlobalConfig globalConfig){

    }


}
