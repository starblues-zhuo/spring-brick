package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.exception.PluginFactoryException;
import com.gitee.starblues.extension.mybatis.utils.MybatisXmlProcess;
import com.gitee.starblues.factory.FactoryType;
import com.gitee.starblues.factory.PluginFactory;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.OrderExecution;
import org.apache.ibatis.session.SqlSessionFactory;
import org.pf4j.PluginWrapper;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.util.List;

/**
 * mybatis xml 工厂
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginMybatisXmlFactory implements PluginFactory {

    private final MybatisXmlProcess mybatisXmlProcess;

    public PluginMybatisXmlFactory(ApplicationContext mainApplicationContext) {
        SqlSessionFactory sqlSessionFactory = mainApplicationContext.getBean(SqlSessionFactory.class);
        if(sqlSessionFactory != null){
            this.mybatisXmlProcess = MybatisXmlProcess.getInstance(sqlSessionFactory);
        } else {
            this.mybatisXmlProcess = null;
        }
    }


    @Override
    public FactoryType factoryType() {
        return SpringbootMybatisFactoryType.MYBATIS_XML;
    }

    @Override
    public void registry(PluginWrapper pluginWrapper) throws PluginFactoryException {
        if(mybatisXmlProcess == null){
            return;
        }
        BasePlugin basePlugin = (BasePlugin) pluginWrapper.getPlugin();
        List<Resource> pluginResources =
                basePlugin.getPluginResourceLoadFactory().getPluginResources(PluginMybatisXmlLoader.KEY);
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
    public void unRegistry(PluginWrapper pluginWrapper) throws PluginFactoryException {
        // not thing
    }

    @Override
    public int order() {
        return OrderExecution.LOW;
    }
}
