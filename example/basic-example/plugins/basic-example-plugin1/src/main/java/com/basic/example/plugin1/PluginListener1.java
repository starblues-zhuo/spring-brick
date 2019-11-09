package com.basic.example.plugin1;

import com.basic.example.plugin1.service.HelloService;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.realize.OneselfListener;
import com.gitee.starblues.utils.OrderPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginListener1 implements OneselfListener {

    private static final Logger logger = LoggerFactory.getLogger(PluginListener1.class);

    private final HelloService helloService;

    public PluginListener1(HelloService helloService) {
        this.helloService = helloService;
    }


    @Override
    public OrderPriority order() {
        return OrderPriority.getMiddlePriority();
    }

    @Override
    public void startEvent(BasePlugin basePlugin) {
        logger.info("PluginListener1 {} start. helloService : {} .", basePlugin.getWrapper().getPluginId(),
                helloService.sayService2());
    }

    @Override
    public void stopEvent(BasePlugin basePlugin) {
        logger.info("PluginListener1 {} stop. helloService : {} .", basePlugin.getWrapper().getPluginId(),
                helloService.sayService2());
    }
}
