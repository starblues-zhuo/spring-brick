package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.bean.inset.ExtractFactoryInset;
import com.gitee.starblues.factory.process.pipe.bean.inset.PluginInsetBean;
import com.gitee.starblues.factory.process.pipe.bean.inset.PluginUtilsInset;
import org.pf4j.util.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 系统内嵌的Bean注册者
 * @author starBlues
 * @version 1.0
 */
public class PluginInsetBeanRegistrar implements PluginBeanRegistrar{

    private final List<PluginInsetBean> pluginInsetBeans = new ArrayList<>(2);

    public PluginInsetBeanRegistrar() {
        this.pluginInsetBeans.add(new PluginUtilsInset());
        this.pluginInsetBeans.add(new ExtractFactoryInset());
    }


    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        SpringBeanRegister springBeanRegister = pluginRegistryInfo.getSpringBeanRegister();
        for (PluginInsetBean pluginInsetBean : this.pluginInsetBeans) {
            String beanName = pluginInsetBean.getBeanName();
            Object bean = pluginInsetBean.getBean(pluginRegistryInfo);
            if(bean == null){
                continue;
            }
            if(StringUtils.isNullOrEmpty(beanName)){
                beanName = bean.getClass().getName();
            }
            springBeanRegister.registerSingleton(beanName, bean);
        }
    }

}
