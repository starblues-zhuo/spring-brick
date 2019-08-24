package com.gitee.starblues.extension;

import com.gitee.starblues.loader.PluginResourceLoader;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroupExtend;
import com.gitee.starblues.factory.process.post.PluginPostProcessorExtend;
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
     * 得到扩展的插件中的类分组器
     * @param applicationContext 主程序ApplicationContext
     * @return List PluginPipeProcessorExtend
     */
    public List<PluginClassGroupExtend> getPluginClassGroup(ApplicationContext applicationContext){
        return null;
    }


    /**
     * 得到扩展的流插件处理者
     * @param applicationContext 主程序ApplicationContext
     * @return List PluginPipeProcessorExtend
     */
    public List<PluginPipeProcessorExtend> getPluginPipeProcessor(ApplicationContext applicationContext){
        return null;
    }

    /**
     * 得到扩展的插件后置处理者
     * @param applicationContext 主程序ApplicationContext
     * @return List PluginPostProcessorExtend
     */
    public List<PluginPostProcessorExtend> getPluginPostProcessor(ApplicationContext applicationContext){
        return null;
    }


}
