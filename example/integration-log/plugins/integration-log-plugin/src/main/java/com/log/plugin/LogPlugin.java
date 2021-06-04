package com.log.plugin;

import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.PluginWrapper;

/**
 * description
 *
 * @author sousouki
 * @version 2.4.3
 */
@ConfigDefinition(fileName = "config.yml")
public class LogPlugin extends BasePlugin {


    public LogPlugin(PluginWrapper wrapper) {
        super(wrapper);
    }

    @Override
    protected void startEvent() {

    }

    @Override
    protected void deleteEvent() {

    }

    @Override
    protected void stopEvent() {

    }

}

