package com.gitee.starblues.extension.log.log4j;

import ch.qos.logback.core.util.OptionHelper;
import com.gitee.starblues.extension.log.LogRegistry;
import com.gitee.starblues.extension.log.config.LogConfig;
import com.gitee.starblues.extension.log.logback.LogbackLogRegistry;
import com.gitee.starblues.extension.log.util.LogConfigUtil;
import com.gitee.starblues.factory.PluginRegistryInfo;
import org.apache.log4j.*;
import org.apache.log4j.spi.Filter;
import org.apache.log4j.spi.LoggingEvent;
import org.pf4j.PluginWrapper;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志注册统一接口
 * @author starBlues
 * @version 2.4.3
 */
public class Log4jLogRegistry implements LogRegistry {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(LogbackLogRegistry.class);
    private final Map<String, Set<Appender>> pluginAppenderInfo = new ConcurrentHashMap<>();

    @Override
    public void registry(List<Resource> resources, PluginRegistryInfo pluginRegistryInfo) throws Exception {
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        Logger rootLogger = LogManager.getRootLogger();
        Set<Appender> allAppender = new HashSet<>();
        for (Resource resource : resources) {
            if(resource == null || !resource.exists()){
                continue;
            }
            LogConfig logConfig;
            try {
                logConfig = LogConfigUtil.getLogConfig(resource, pluginRegistryInfo);
            } catch (Exception e){
                log.error("Failed to read log configuration.", e);
                continue;
            }
            Set<Appender> appenderSet = getAppender(pluginRegistryInfo, logConfig);
            for (Appender appender : appenderSet) {
                rootLogger.addAppender(appender);
                allAppender.add(appender);
            }
        }
        pluginAppenderInfo.put(pluginWrapper.getPluginId(), allAppender);
    }


    private Set<Appender> getAppender(PluginRegistryInfo pluginRegistryInfo, LogConfig logConfig){
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        String packageName = pluginRegistryInfo.getBasePlugin().scanPackage();
        RollingFileAppender appender = new RollingFileAppender();
        appender.addFilter(new Filter() {
            @Override
            public int decide(LoggingEvent event) {
                if (event.getLoggerName().startsWith(packageName)) {
                    return Filter.ACCEPT;
                }
                return Filter.DENY;
            }
        });
        String logFilePrefix = LogConfigUtil.getLogFile(pluginWrapper, logConfig);
        appender.setName(pluginWrapper.getPluginId());
        appender.setFile(logFilePrefix.concat(".log"));
        appender.setMaxFileSize(logConfig.getMaxFileSize());
        appender.setAppend(true);
        appender.setMaxBackupIndex(logConfig.getMaxHistory());
        appender.setThreshold(Level.toLevel(logConfig.getLevel()));
        PatternLayout patternLayout = new PatternLayout();
        patternLayout.setConversionPattern(logConfig.getPattern());
        appender.setLayout(patternLayout);
        appender.activateOptions();
        return Collections.singleton(appender);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Set<Appender> allAppender = pluginAppenderInfo.get(pluginRegistryInfo.getPluginWrapper().getPluginId());
        if(allAppender == null || allAppender.isEmpty()){
            return;
        }
        Logger rootLogger = LogManager.getRootLogger();
        for (Appender appender : allAppender) {
            rootLogger.removeAppender(appender);
        }
    }
}
