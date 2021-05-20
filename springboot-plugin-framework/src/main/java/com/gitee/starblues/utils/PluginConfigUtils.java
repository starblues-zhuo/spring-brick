package com.gitee.starblues.utils;

import com.gitee.starblues.annotation.ConfigDefinition;
import org.apache.catalina.core.ApplicationContext;
import org.pf4j.RuntimeMode;
import org.pf4j.util.StringUtils;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.PropertySource;

/**
 * 插件配置工具类
 * @author starBlues
 * @version 2.4.3
 */
public class PluginConfigUtils {

    private PluginConfigUtils(){}

    /**
     * 根据项目运行环境模式来获取配置文件名称
     * @param configDefinition 配置的注解
     * @param runtimeMode 运行模式
     * @return 文件名称
     */
    public static String getConfigFileName(ConfigDefinition configDefinition, RuntimeMode runtimeMode){
        // TODO 后期移除 value
        String fileName = configDefinition.value();
        if(StringUtils.isNullOrEmpty(fileName)){
            fileName = configDefinition.fileName();
            if(StringUtils.isNullOrEmpty(fileName)){
                return null;
            }
        }

        String fileNamePrefix;
        String fileNamePrefixSuffix;

        if(fileName.lastIndexOf(".") == -1) {
            fileNamePrefix = fileName;
            fileNamePrefixSuffix = "";
        } else {
            int index = fileName.lastIndexOf(".");
            fileNamePrefix = fileName.substring(0, index);
            fileNamePrefixSuffix = fileName.substring(index);
        }

        if(runtimeMode == RuntimeMode.DEPLOYMENT){
            // 生产环境
            fileNamePrefix = fileNamePrefix + configDefinition.prodSuffix();
        } else if(runtimeMode == RuntimeMode.DEVELOPMENT){
            // 开发环境
            fileNamePrefix = fileNamePrefix + configDefinition.devSuffix();
        }
        return fileNamePrefix + fileNamePrefixSuffix;
    }

}
