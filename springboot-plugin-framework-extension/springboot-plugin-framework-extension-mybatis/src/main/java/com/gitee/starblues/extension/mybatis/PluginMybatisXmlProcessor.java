package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.exception.PluginFactoryException;
import com.gitee.starblues.extension.mybatis.utils.MybatisXmlProcess;
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
 * @version 1.0
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
        List<Resource> pluginResources =
                basePlugin.getPluginResourceLoadFactory().getPluginResources(PluginMybatisXmlLoader.KEY);
        if(pluginResources == null || pluginResources.isEmpty()){
            return;
        }
        try {
            boolean change = mybatisXmlProcess.isChange(pluginResources);
            if(change){
                mybatisXmlProcess.loadXmlResource(pluginResources, pluginWrapper.getPluginClassLoader());
            }
        } catch (Exception e){
            e.printStackTrace();
            throw new PluginFactoryException("load mybatis xml error :  " + e.getMessage());
        }
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        // not thing
    }
}
