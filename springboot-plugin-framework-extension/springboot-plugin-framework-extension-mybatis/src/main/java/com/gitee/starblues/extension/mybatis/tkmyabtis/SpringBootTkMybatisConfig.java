package com.gitee.starblues.extension.mybatis.tkmyabtis;

import com.gitee.starblues.extension.mybatis.MybatisCommonConfig;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;
import tk.mybatis.mapper.entity.Config;

/**
 * springboot tk-mybatis 配置接口
 * @author starBlues
 * @version 2.3
 */
public interface SpringBootTkMybatisConfig extends MybatisCommonConfig {



    /**
     * 插件自主配置Mybatis的 SqlSessionFactoryBean
     * SqlSessionFactoryBean 具体配置说明参考 Mybatis 官网
     * @param sqlSessionFactoryBean SqlSessionFactoryBean
     * @param config 插件自主配置tk的 Config 具体配置说明参考 https://gitee.com/free/Mapper/wikis/1.1-java?sort_id=208196
     */
    default void oneselfConfig(SqlSessionFactoryBean sqlSessionFactoryBean, Config config){
    }

    /**
     * 重写配置当前跟随主程序的配置
     * 只有 enableOneselfConfig 返回 false, 实现该方法才生效
     * @param config 插件自主配置tk的 Config 具体配置说明参考 https://gitee.com/free/Mapper/wikis/1.1-java?sort_id=208196
     * @param configuration Mybatis Configuration 的配置
     */
    default void reSetMainConfig(Configuration configuration, Config config){

    }

}
