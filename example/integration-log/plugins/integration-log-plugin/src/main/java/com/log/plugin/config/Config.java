package com.log.plugin.config;

import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.extension.log.config.SpringBootLogConfig;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * description
 *
 * @author starBlues
 * @version 2.4.3
 */
@ConfigDefinition(fileName="config.yml")
public class Config implements SpringBootLogConfig {

    private String logLocation;

    @Override
    public Set<String> logConfigLocations() {
        Set<String> logConfigLocations = new HashSet<>();
        logConfigLocations.add(logLocation);
        return logConfigLocations;
    }

    public String getLogLocation() {
        return logLocation;
    }

    public void setLogLocation(String logLocation) {
        this.logLocation = logLocation;
    }
}
