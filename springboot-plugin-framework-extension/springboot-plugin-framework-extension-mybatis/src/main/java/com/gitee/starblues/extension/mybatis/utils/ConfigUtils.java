package com.gitee.starblues.extension.mybatis.utils;

import com.gitee.starblues.extension.mybatis.mybatisplus.SpringBootMybatisPlusConfig;
import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

import java.util.Map;

/**
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-12-16
 */
public class ConfigUtils {

    private ConfigUtils(){}

    public static <T> T getConfig(ApplicationContext applicationContext,
                                  String pluginId,
                                  Class<T> tClass){
        try {
            String[] beanNamesForType = applicationContext.getBeanNamesForType(tClass, false, false);
            if(beanNamesForType.length == 0){
                return null;
            }
            for (String beanName : beanNamesForType) {
                if(StringUtils.isEmpty(beanName)){
                    continue;
                }
                if(beanName.contains(pluginId)){
                    return applicationContext.getBean(beanName, tClass);
                }
            }
            return null;
        } catch (Exception e){
            return null;
        }
    }

}
