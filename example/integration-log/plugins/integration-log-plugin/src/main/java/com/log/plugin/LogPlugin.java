package com.log.plugin;

import com.gitee.starblues.extension.log.config.SpringBootLogConfig;
import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.PluginWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * description
 *
 * @author sousouki
 * @version 1.0
 */
public class LogPlugin extends BasePlugin implements SpringBootLogConfig {

    private Set<String> locations = new HashSet<>();

    public LogPlugin(PluginWrapper wrapper) {
        super(wrapper);
        locations.add("classpath:log.xml");
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

    @Override
    public Set<String> logConfigLocations() {
        return locations;
    }
}

