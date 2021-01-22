package com.gitee.starblues.extension;

import org.pf4j.util.StringUtils;
import org.springframework.context.ApplicationContext;


/**
 * 扩展配置文件工具
 * @author starBlues
 * @version 2.4.0
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
                if(StringUtils.isNullOrEmpty(beanName)){
                    continue;
                }
                return applicationContext.getBean(beanName, tClass);
            }
            return null;
        } catch (Exception e){
            return null;
        }
    }

}
