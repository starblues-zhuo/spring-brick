package com.gitee.starblues.factory.process.post.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.realize.OneselfListener;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.SpringBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * 插件中 OneselfListener 监听者处理者。主要执行监听器的启动事件。
 * @author starBlues
 * @version 2.4.0
 */
public class PluginOneselfStartEventProcessor implements PluginPostProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());
    private final static String KEY = "OneselfListeners";

    public PluginOneselfStartEventProcessor(){
    }


    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
            try {
                GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
                List<OneselfListener> oneselfListeners = SpringBeanUtils.getBeans(pluginApplicationContext, OneselfListener.class);
                List<OneselfListener> saveOneselfListeners = new ArrayList<>(oneselfListeners.size());
                oneselfListeners.stream()
                        .filter(oneselfListener -> oneselfListener != null)
                        .sorted(CommonUtils.orderPriority(oneselfListener -> oneselfListener.order()))
                        .forEach(oneselfListener -> {
                            try {
                                oneselfListener.startEvent(basePlugin);
                            } catch (Exception e){
                                log.error("OneselfListener {} execute stopEvent exception. {}",
                                        oneselfListener.getClass().getName(), e.getMessage(), e);
                            } finally {
                                saveOneselfListeners.add(oneselfListener);
                            }
                        });
                if(!saveOneselfListeners.isEmpty()){
                    pluginRegistryInfo.addExtension(KEY, saveOneselfListeners);
                }
            } catch (Exception e){
                log.error("Plugin '{}' OneselfListener process exception.", basePlugin.getWrapper().getPluginId(), e);
            }
        }
    }


    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
            List<OneselfListener> oneselfListeners = pluginRegistryInfo.getExtension(KEY);
            if(oneselfListeners == null || oneselfListeners.isEmpty()){
                continue;
            }
            for (OneselfListener oneselfListener : oneselfListeners) {
                try {
                    oneselfListener.stopEvent(basePlugin);
                } catch (Exception e){
                    log.error("OneselfListener {} execute stopEvent exception. {}",
                            oneselfListener.getClass().getName(), e.getMessage(), e);
                }
            }
        }
    }


}
