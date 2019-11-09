package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessor;
import com.gitee.starblues.factory.process.post.bean.PluginOneselfStartEventProcessor;
import com.gitee.starblues.integration.application.PluginApplication;
import com.gitee.starblues.integration.user.PluginUser;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.realize.OneselfListener;
import com.gitee.starblues.utils.CommonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.util.List;
import java.util.Objects;

/**
 * 执行插件自监听器的停止事件的处理者。必须在所有处理者中第一个执行。否则会导致所依赖的bean被卸载。
 *
 * @see PluginOneselfStartEventProcessor 触发启动事件
 * @author zhangzhuo
 * @version 2.2.1
 */
public class OneselfListenerStopEventProcessor implements PluginPipeProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final PluginUser pluginUser;

    public OneselfListenerStopEventProcessor(ApplicationContext applicationContext){
        Objects.requireNonNull(applicationContext);
        PluginApplication pluginApplication = applicationContext.getBean(PluginApplication.class);
        this.pluginUser = pluginApplication.getPluginUser();
    }


    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {}

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        String pluginId = basePlugin.getWrapper().getPluginId();
        List<OneselfListener> oneselfListeners = pluginUser.getPluginBeans(pluginId, OneselfListener.class);
        oneselfListeners.stream()
                .sorted(CommonUtils.orderPriority(oneselfListener -> oneselfListener.order()))
                .forEach(oneselfListener -> {
                    try {
                        oneselfListener.stopEvent(basePlugin);
                    } catch (Exception e){
                        log.error("OneselfListener {} execute stopEvent exception. {}",
                                oneselfListener.getClass().getName(), e.getMessage(), e);
                    }
                });
    }
}
