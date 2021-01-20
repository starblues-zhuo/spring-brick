package com.gitee.starblues.extension;

import com.gitee.starblues.factory.process.pipe.PluginPreProcessorExtend;
import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.factory.process.pipe.loader.PluginResourceLoader;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroupExtend;
import com.gitee.starblues.factory.process.post.PluginPostProcessorExtend;
import org.springframework.context.ApplicationContext;

import java.util.List;

/**
 * 抽象的扩展工厂
 *
 * @author starBlues
 * @version 2.1.0
 */
public abstract class AbstractExtension {

    protected PluginApplication pluginApplication;


    public void setPluginApplication(PluginApplication pluginApplication) {
        this.pluginApplication = pluginApplication;
    }

    /**
     * 扩展唯一的key
     * @return String
     */
    public abstract String key();

    /**
     * 该扩展初始化的操作
     * 主要是在插件初始化阶段被调用
     * @param applicationContext applicationContext
     * @throws Exception 初始化异常
     */
    public void initialize(ApplicationContext applicationContext) throws Exception{
    }


    /**
     * 返回插件的资源加载者。
     * 主要是加载插件中的某些资源，比如文件、图片等。
     * @return List PluginResourceLoader
     */
    public List<PluginResourceLoader> getPluginResourceLoader(){
        return null;
    }

    /**
     * 返回扩展的插件中的类分组器。
     * 该扩展主要是对插件中的Class文件分组，然后供 PluginPipeProcessor、PluginPostProcessor 阶段使用。
     * @param applicationContext 主程序ApplicationContext
     * @return List PluginPipeProcessorExtend
     */
    public List<PluginClassGroupExtend> getPluginClassGroup(ApplicationContext applicationContext){
        return null;
    }

    /**
     * 返回扩展的插件前置处理者。
     * 该扩展主要是对每一个插件进行处理
     * @param applicationContext 主程序ApplicationContext
     * @return List PluginPipeProcessorExtend
     */
    public List<PluginPreProcessorExtend> getPluginPreProcessor(ApplicationContext applicationContext){
        return null;
    }

    /**
     * 返回扩展的流插件处理者。
     * 该扩展主要是对每一个插件进行处理
     * @param applicationContext 主程序ApplicationContext
     * @return List PluginPipeProcessorExtend
     */
    public List<PluginPipeProcessorExtend> getPluginPipeProcessor(ApplicationContext applicationContext){
        return null;
    }

    /**
     * 返回扩展的插件后置处理者。
     * 该扩展主要是对全部插件进行处理。
     * @param applicationContext 主程序ApplicationContext
     * @return List PluginPostProcessorExtend
     */
    public List<PluginPostProcessorExtend> getPluginPostProcessor(ApplicationContext applicationContext){
        return null;
    }


}
