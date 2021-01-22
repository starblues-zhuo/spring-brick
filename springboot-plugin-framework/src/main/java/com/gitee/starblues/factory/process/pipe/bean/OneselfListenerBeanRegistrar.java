package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.post.bean.PluginOneselfStartEventProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 执行插件自监听器的停止事件的处理者。必须在所有处理者中第一个执行。否则会导致所依赖的bean被卸载。
 *
 * @see PluginOneselfStartEventProcessor 触发启动事件
 * @author starBlues
 * @version 2.4.0
 */
public class OneselfListenerBeanRegistrar implements PluginBeanRegistrar {

    private final Logger log = LoggerFactory.getLogger(this.getClass());


    public OneselfListenerBeanRegistrar(){
    }


    @Override
    public void initialize() throws Exception {}

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {

    }


}
