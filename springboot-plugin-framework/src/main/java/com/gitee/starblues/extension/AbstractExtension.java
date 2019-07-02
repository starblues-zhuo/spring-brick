package com.gitee.starblues.extension;

import com.gitee.starblues.factory.NoticePluginFactory;
import com.gitee.starblues.factory.bean.register.PluginBeanRegister;
import com.gitee.starblues.loader.PluginResourceLoader;
import com.gitee.starblues.realize.BasePlugin;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * 抽象的扩展工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public abstract class AbstractExtension {

    /**
     * 扩展key
     * @return String
     */
    public abstract String key();

    /**
     * 得到插件的资源加载者
     * @return List PluginResourceLoader
     */
    public List<PluginResourceLoader> getPluginResourceLoader(){
        return null;
    }

    /**
     * 得到可通知的插件工厂
     * @param mainApplicationContext 主程序ApplicationContext
     * @return List PluginFactory
     */
    public List<NoticePluginFactory> getNoticePluginFactory(ApplicationContext mainApplicationContext){
        return null;
    }


    /**
     * 得到插件bean注册者
     * @param mainApplicationContext 主程序ApplicationContext
     * @return List PluginBeanRegister
     */
    public List<PluginBeanRegister> getPluginBeanRegister(ApplicationContext mainApplicationContext){
        return null;
    }


}
