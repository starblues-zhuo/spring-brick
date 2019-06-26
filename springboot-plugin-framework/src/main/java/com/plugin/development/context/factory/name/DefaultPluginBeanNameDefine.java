package com.plugin.development.context.factory.name;

import org.springframework.util.StringUtils;

/**
 * 默认的插件bean名称定义
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class DefaultPluginBeanNameDefine implements PluginBeanNameDefine{

    private final String PREFIX = "plugin";
    private final String SPLIT = ":";


    @Override
    public boolean isPluginBeanName(String beanName) {
        if(StringUtils.isEmpty(beanName)){
            return false;
        }
        String[] split = beanName.split(SPLIT);
        if(split.length == 0){
            return false;
        }
        if(PREFIX.equals(split[0])){
            return true;
        }
        return false;
    }

    @Override
    public String getPluginName(String name) {
        if(StringUtils.isEmpty(name)){
            return name;
        }
        return PREFIX + SPLIT + name;
    }
}
