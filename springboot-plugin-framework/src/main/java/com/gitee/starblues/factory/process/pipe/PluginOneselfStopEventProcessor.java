package com.gitee.starblues.factory.process.pipe;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.post.bean.PluginOneselfStartEventProcessor;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.realize.OneselfListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 插件中 OneselfListener 监听者处理者。主要执行监听器的停止事件。
 * @author starBlues
 * @version 1.0
 * @since 2021-05-30
 */
public class PluginOneselfStopEventProcessor implements PluginPipeProcessor{

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        // 当前不执行启动事件
        // 由类 com.gitee.starblues.factory.process.post.bean.PluginOneselfStartEventProcessor 实现启动
        // 主要是资源全部加载注册完成后再触发启动事件
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        List<OneselfListener> oneselfListeners = pluginRegistryInfo
                .getExtension(PluginOneselfStartEventProcessor.KEY);
        if(oneselfListeners == null || oneselfListeners.isEmpty()){
            return;
        }
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
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
