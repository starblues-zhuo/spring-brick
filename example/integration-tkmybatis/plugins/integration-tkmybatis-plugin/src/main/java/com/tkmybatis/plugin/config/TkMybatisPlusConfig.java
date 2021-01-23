package com.tkmybatis.plugin.config;

import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.extension.mybatis.tkmyabtis.SpringBootTkMybatisConfig;
import com.google.common.collect.Sets;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Config;

import java.util.Set;

/**
 * @author starBlues
 * @version 1.0
 * @since 2020-12-14
 */
@ConfigDefinition
public class TkMybatisPlusConfig implements SpringBootTkMybatisConfig {

    @Override
    public Set<String> entityPackage() {
        return Sets.newHashSet("com.tkmybatis.plugin.entity");
    }

    @Override
    public Set<String> xmlLocationsMatch() {
        return Sets.newHashSet("classpath:mapper/*Mapper.xml");
    }

    @Override
    public boolean enableOneselfConfig() {
        return false;
    }

    @Override
    public void oneselfConfig(SqlSessionFactoryBean sqlSessionFactoryBean) {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL("jdbc:mysql://127.0.0.1:3306/plugin-tkmbatis?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC");
        mysqlDataSource.setUser("root");
        mysqlDataSource.setPassword("root");
        sqlSessionFactoryBean.setDataSource(mysqlDataSource);
    }

    @Override
    public void oneselfConfig(Config config) {

    }
}
