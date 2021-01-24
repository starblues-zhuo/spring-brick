package com.gitee.starblues.factory.process.pipe;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.realize.ConfigBean;
import com.gitee.starblues.utils.PluginBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author starBlues
 * @version 2.4.0
 */
public class PluginConfigBeanPipeProcessor implements PluginPipeProcessor{

    private final static String KEY = "ConfigBeans";

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        List<ConfigBean> pluginBeans = PluginBeanUtils.getPluginBeans(pluginRegistryInfo.getPluginApplicationContext(),
                ConfigBean.class);
        if(pluginBeans.isEmpty()){
            return;
        }
        List<ConfigBean> successConfigBeans = new ArrayList<>(pluginBeans.size());
        for (ConfigBean pluginBean : pluginBeans) {
            try {
                pluginBean.initialize();
                successConfigBeans.add(pluginBean);
            } catch (Exception e){
                logger.error("Plugin '{}' configBean '{}' initialize exception.",
                        pluginRegistryInfo.getPluginWrapper().getPluginId(),
                        pluginBean.getClass().getName(), e);
            }
        }
        pluginRegistryInfo.addExtension(KEY, successConfigBeans);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        List<ConfigBean> pluginBeans = pluginRegistryInfo.getExtension(KEY);
        if(pluginBeans == null || pluginBeans.isEmpty()){
            return;
        }
        for (ConfigBean pluginBean : pluginBeans) {
            try {
                pluginBean.destroy();
            } catch (Exception e){
                logger.error("Plugin '{}' configBean '{}' destroy exception.",
                        pluginRegistryInfo.getPluginWrapper().getPluginId(),
                        pluginBean.getClass().getName(), e);
            }
        }
    }
}
