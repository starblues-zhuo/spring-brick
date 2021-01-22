package com.gitee.starblues.factory.process.post.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.bean.OneselfListenerBeanRegistrar;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;
import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.user.PluginUser;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.realize.OneselfListener;
import com.gitee.starblues.utils.AopUtils;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.PluginBeanUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.List;
import java.util.Objects;

/**
 * 插件中 OneselfListener 监听者处理者。主要执行监听器的启动事件。
 * @see OneselfListenerBeanRegistrar 触发停止事件
 * @author starBlues
 * @version 2.1.0
 */
public class PluginOneselfStartEventProcessor implements PluginPostProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PluginUser pluginUser;

    public PluginOneselfStartEventProcessor(ApplicationContext applicationContext){
        Objects.requireNonNull(applicationContext);
        PluginApplication pluginApplication = applicationContext.getBean(PluginApplication.class);
        this.pluginUser = pluginApplication.getPluginUser();
    }


    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            AopUtils.resolveAop(pluginRegistryInfo.getPluginWrapper());
            try {
                BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
                String pluginId = basePlugin.getWrapper().getPluginId();
                GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
                List<OneselfListener> oneselfListeners = PluginBeanUtils.getPluginBeans(pluginApplicationContext, OneselfListener.class);
                oneselfListeners.stream()
                        .filter(oneselfListener -> oneselfListener != null)
                        .sorted(CommonUtils.orderPriority(oneselfListener -> oneselfListener.order()))
                        .forEach(oneselfListener -> {
                            try {
                                oneselfListener.startEvent(basePlugin);
                            } catch (Exception e){
                                log.error("OneselfListener {} execute startEvent exception. {}",
                                        oneselfListener.getClass().getName(), e.getMessage(), e);
                            }
                        });
            } finally {
                AopUtils.recoverAop();
            }
        }
    }



    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) {
        // 此处不卸载调用。
    }



}
