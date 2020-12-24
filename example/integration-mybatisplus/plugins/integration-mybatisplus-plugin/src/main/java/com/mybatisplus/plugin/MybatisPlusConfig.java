package com.mybatisplus.plugin;

import com.baomidou.mybatisplus.extension.spring.MybatisSqlSessionFactoryBean;
import com.gitee.starblues.extension.mybatis.mybatisplus.SpringBootMybatisPlusConfig;
import com.google.common.collect.Sets;
import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-12-14
 */
@Component
public class MybatisPlusConfig implements SpringBootMybatisPlusConfig {
    @Override
    public Set<String> entityPackage() {
        return Sets.newHashSet("com.mybatisplus.plugin.entity");
    }

    @Override
    public Set<String> xmlLocationsMatch() {
        return Sets.newHashSet("classpath:mapper/*Mapper.xml");
    }

    @Override
    public void oneselfConfig(MybatisSqlSessionFactoryBean sqlSessionFactoryBean) {
        MysqlDataSource mysqlDataSource = new MysqlDataSource();
        mysqlDataSource.setURL("jdbc:mysql://127.0.0.1:3306/ac_identity_auth?useUnicode=true&useSSL=false&characterEncoding=utf8&serverTimezone=UTC");
        mysqlDataSource.setUser("root");
        mysqlDataSource.setPassword("root");
        sqlSessionFactoryBean.setDataSource(mysqlDataSource);
    }

    @Override
    public boolean enableOneselfConfig() {
        return true;
    }
}
