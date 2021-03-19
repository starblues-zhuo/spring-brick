package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;

/**
 * 公共注册
 * @author starBlues
 * @version 2.4.2
 */
public class CommonRegister {

    private CommonRegister(){}


    public static void commonRegister(PluginRegistryInfo pluginRegistryInfo,
                                      SqlSessionFactory sqlSessionFactory,
                                      SqlSessionTemplate sqlSessionTemplate){
        // 注册SqlSessionFactory
        SpringBeanRegister springBeanRegister = pluginRegistryInfo.getSpringBeanRegister();
        springBeanRegister.registerSingleton("sqlSessionFactory", sqlSessionFactory);
        springBeanRegister.registerSingleton("sqlSession", sqlSessionTemplate);
    }

}
