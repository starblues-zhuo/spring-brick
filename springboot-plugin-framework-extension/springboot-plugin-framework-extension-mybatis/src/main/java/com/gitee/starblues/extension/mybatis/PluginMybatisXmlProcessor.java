package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.extension.mybatis.utils.MybatisXmlProcess;
import com.gitee.starblues.loader.ResourceWrapper;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.utils.OrderPriority;
import org.apache.ibatis.session.SqlSessionFactory;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * mybatis xml 处理者
 *
 * @author zhangzhuo
 * @version 2.2.0
 */
public class PluginMybatisXmlProcessor implements PluginPipeProcessorExtend {

    private final MybatisXmlProcess mybatisXmlProcess;

    PluginMybatisXmlProcessor(ApplicationContext mainApplicationContext) {
        SqlSessionFactory sqlSessionFactory = mainApplicationContext.getBean(SqlSessionFactory.class);
        if(sqlSessionFactory != null){
            this.mybatisXmlProcess = MybatisXmlProcess.getInstance(sqlSessionFactory);
        } else {
            this.mybatisXmlProcess = null;
        }
    }

    @Override
    public String key() {
        return "PluginMybatisXmlProcessor";
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getLowPriority();
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        if(mybatisXmlProcess == null){
            return;
        }
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        ResourceWrapper resourceWrapper =
                basePlugin.getPluginResourceLoadFactory().getPluginResources(PluginMybatisXmlLoader.KEY);
        if(resourceWrapper == null){
            return;
        }
        List<Resource> pluginResources = resourceWrapper.getResources();
        if(pluginResources == null || pluginResources.isEmpty()){
            return;
        }
        mybatisXmlProcess.loadXmlResource(pluginResources, pluginWrapper.getPluginClassLoader());
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        // not thing
    }
}
