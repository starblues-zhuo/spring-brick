package com.gitee.starblues.extension;

import org.springframework.context.ApplicationContext;
import org.springframework.util.StringUtils;

/**
 * 扩展配置文件工具
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-12-19
 */
public class ExtensionConfigUtils {

    private ExtensionConfigUtils(){}

    /**
     * 得到扩展的配置
     * @param applicationContext ApplicationContext
     * @param pluginId 插件id
     * @param tClass 配置类
     * @param <T> 配置类的类型
     * @return 配置类的Spring容器对象
     */
    public static <T> T getConfig(ApplicationContext applicationContext,
                                  String pluginId,
                                  Class<T> tClass){
        try {
            String[] beanNamesForType = applicationContext.getBeanNamesForType(tClass,
                    false, false);
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