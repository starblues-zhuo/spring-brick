package com.gitee.starblues.extension.log.log4j;

import ch.qos.logback.core.spi.FilterReply;
import com.gitee.starblues.extension.log.LogRegistry;
import com.gitee.starblues.extension.log.config.LogConfig;
import com.gitee.starblues.extension.log.logback.LogbackLogRegistry;
import com.gitee.starblues.extension.log.util.LogConfigUtil;
import com.gitee.starblues.factory.PluginRegistryInfo;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.*;
import org.apache.logging.log4j.core.appender.RollingFileAppender;
import org.apache.logging.log4j.core.appender.rolling.*;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.DefaultConfiguration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;
import org.apache.logging.log4j.message.Message;
import org.pf4j.PluginWrapper;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.Charset;
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
        LoggerContext loggerContext = (LoggerContext)LogManager.getContext(false);
        Configuration configuration = loggerContext.getConfiguration();
        LoggerConfig rootLogger = configuration.getRootLogger();
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
                configuration.addAppender(appender);
                rootLogger.addAppender(appender, Level.toLevel(logConfig.getLevel()), null);
                allAppender.add(appender);
            }
        }
        pluginAppenderInfo.put(pluginWrapper.getPluginId(), allAppender);
    }


    private Set<Appender> getAppender(PluginRegistryInfo pluginRegistryInfo, LogConfig logConfig){
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        Filter filter = new LogFilter(pluginRegistryInfo.getBasePlugin().scanPackage());
        PatternLayout patternLayout = PatternLayout.newBuilder()
                .withPattern(logConfig.getPattern())
                .withCharset(Charset.defaultCharset())
                .build();

        final TriggeringPolicy policy =
                CompositeTriggeringPolicy.createPolicy(
                        SizeBasedTriggeringPolicy.createPolicy(
                                logConfig.getMaxFileSize()
                        ),
                        TimeBasedTriggeringPolicy.createPolicy("1", "true")
                );

        RolloverStrategy strategy = DefaultRolloverStrategy.newBuilder()
                .withFileIndex(logConfig.getTotalFileSize())
                .withConfig(new DefaultConfiguration())
                .withMax(String.valueOf(logConfig.getMaxHistory()))
                .build();

        RollingFileAppender appender = RollingFileAppender.newBuilder()
                .withFilter(filter)
                .withName(pluginWrapper.getPluginId())
                .withLayout(patternLayout)
                .withIgnoreExceptions(false)
                .withFileName(LogConfigUtil.getLogFile(pluginWrapper, logConfig).concat(".log"))
                .withFilePattern(".%d{yyyy-MM-dd}-%i.log")
                .withAppend(true)
                .withPolicy(policy)
                .withStrategy(strategy)
                .build();

        appender.start();
        filter.start();

        return Collections.singleton(appender);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Set<Appender> allAppender = pluginAppenderInfo.get(pluginRegistryInfo.getPluginWrapper().getPluginId());
        if(allAppender == null || allAppender.isEmpty()){
            return;
        }
        LoggerContext loggerContext = (LoggerContext)LogManager.getContext(false);
        Configuration configuration = loggerContext.getConfiguration();
        for (Appender appender : allAppender) {
            configuration.getAppenders().remove(appender.getName());
        }
    }

    private static class LogFilter extends AbstractFilter{

        private final String packageName;

        private LogFilter(String packageName) {
            this.packageName = packageName;
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8, Object p9) {
            return filter(logger.getName());
        }

        @Override
        public Result filter(LogEvent event) {
            return filter(event.getLoggerName());
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, Message msg, Throwable t) {
            return filter(logger.getName());
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, Object msg, Throwable t) {
            return filter(logger.getName());
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object... params) {
            return filter(logger.getName());
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0) {
            return filter(logger.getName());
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1) {
            return filter(logger.getName());
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2) {
            return filter(logger.getName());
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3) {
            return filter(logger.getName());
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4) {
            return filter(logger.getName());
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5) {
            return filter(logger.getName());
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6) {
            return filter(logger.getName());
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7) {
            return filter(logger.getName());
        }

        @Override
        public Result filter(Logger logger, Level level, Marker marker, String msg, Object p0, Object p1, Object p2, Object p3, Object p4, Object p5, Object p6, Object p7, Object p8) {
            return filter(logger.getName());
        }

        private Result filter(String loggerName){
            if (loggerName.startsWith(packageName)) {
                return Result.ACCEPT;
            }
            return Result.DENY;
        }
    }



}
