package com.gitee.starblues.extension.log.logback;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import com.gitee.starblues.extension.log.LogRegistry;
import com.gitee.starblues.extension.log.config.LogConfig;
import com.gitee.starblues.extension.log.util.LogConfigUtil;
import com.gitee.starblues.factory.PluginRegistryInfo;
import org.pf4j.PluginWrapper;
import org.pf4j.util.StringUtils;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 日志配置处理者
 * @author sousouki
 * @version 2.4.3
 */
public class LogbackLogRegistry implements LogRegistry {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(LogbackLogRegistry.class);

    private final Map<String, Set<Appender<ILoggingEvent>>> pluginAppenderInfo = new ConcurrentHashMap<>();

    @Override
    public void registry(List<Resource> resources, PluginRegistryInfo pluginRegistryInfo) throws Exception {
        Set<Appender<ILoggingEvent>> appenderSet = new HashSet<>();
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        for (Resource resource : resources) {
            if(resource == null){
                continue;
            }
            LogConfig logConfig;
            try {
                logConfig = LogConfigUtil.getLogConfig(resource, pluginRegistryInfo);
            } catch (Exception e){
                log.error("Failed to read log configuration.", e);
                continue;
            }
            Set<Appender<ILoggingEvent>> logAppenderSet = addAppender(pluginRegistryInfo, logConfig);
            appenderSet.addAll(logAppenderSet);
        }
        pluginAppenderInfo.put(pluginWrapper.getPluginId(), appenderSet);
    }

    @Override
    public void unRegistry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        Set<Appender<ILoggingEvent>> logAppenderSet = pluginAppenderInfo.get(pluginWrapper.getPluginId());
        if(logAppenderSet == null || logAppenderSet.isEmpty()){
            return;
        }
        String packageName = pluginRegistryInfo.getBasePlugin().scanPackage();
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = context.getLogger(packageName);
        for (Appender<ILoggingEvent> appender : logAppenderSet) {
            logger.detachAppender(appender);
        }
        pluginAppenderInfo.remove(pluginWrapper.getPluginId());
    }

    private Set<Appender<ILoggingEvent>> addAppender(PluginRegistryInfo pluginRegistryInfo, LogConfig logConfig) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        String packageName = logConfig.getPackageName();
        Logger logger = context.getLogger(packageName);
        logger.detachAndStopAllAppenders();

        ConsoleAppender<ILoggingEvent> consoleAppender = createConsoleAppender(pluginRegistryInfo.getPluginWrapper(),
                logConfig, packageName);
        RollingFileAppender<ILoggingEvent> fileAppender = createFileAppender(pluginRegistryInfo,
                logConfig, packageName);

        logger.setAdditive(false);
        logger.setLevel(Level.toLevel(logConfig.getLevel()));
        logger.addAppender(consoleAppender);
        logger.addAppender(fileAppender);

        Set<Appender<ILoggingEvent>> appenderSet = new HashSet<>();
        appenderSet.add(consoleAppender);
        appenderSet.add(fileAppender);
        return appenderSet;
    }

    private ConsoleAppender<ILoggingEvent> createConsoleAppender(PluginWrapper pluginWrapper,
                                                                 LogConfig logConfig,
                                                                 String packageName) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        ConsoleAppender<ILoggingEvent> appender = new ConsoleAppender<>();
        Filter<ILoggingEvent> filter = new LogFilter(packageName);
        filter.start();
        appender.addFilter(filter);
        appender.setContext(context);
        appender.setName(pluginWrapper.getPluginId().concat("-console"));

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(logConfig.getPattern());
        encoder.start();

        appender.setEncoder(encoder);
        appender.start();
        return appender;
    }

    private RollingFileAppender<ILoggingEvent> createFileAppender(PluginRegistryInfo pluginRegistryInfo,
                                                                  LogConfig logConfig,
                                                                  String packageName) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();

        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        if(StringUtils.isNotNullOrEmpty(packageName)){
            Filter<ILoggingEvent> filter = new LogFilter(packageName);
            filter.start();
            appender.addFilter(filter);
        }

        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        appender.setContext(context);
        appender.setName(pluginWrapper.getPluginId());

        String logFilePrefix = LogConfigUtil.getLogFile(pluginRegistryInfo, logConfig);
        appender.setFile(OptionHelper.substVars(logFilePrefix.concat(".log"), context));

        appender.setAppend(true);
        appender.setPrudent(false);

        SizeAndTimeBasedRollingPolicy<ILoggingEvent> policy = new SizeAndTimeBasedRollingPolicy<>();

        String fp = OptionHelper.substVars(logFilePrefix.concat(".%d{yyyy-MM-dd}-%i.log"), context);
        policy.setMaxFileSize(FileSize.valueOf(logConfig.getMaxFileSize()));
        policy.setFileNamePattern(fp);
        policy.setMaxHistory(logConfig.getMaxHistory());
        policy.setTotalSizeCap(FileSize.valueOf(logConfig.getTotalFileSize()));
        policy.setParent(appender);
        policy.setContext(context);
        policy.start();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(context);
        encoder.setPattern(logConfig.getPattern());
        encoder.start();

        appender.setRollingPolicy(policy);
        appender.setEncoder(encoder);
        appender.start();
        return appender;
    }

    private static class LogFilter extends Filter<ILoggingEvent> {

        private final String packageName;
        private final LoggerContext loggerContext = (LoggerContext) LoggerFactory.getILoggerFactory();

        private LogFilter(String packageName) {
            this.packageName = packageName;
        }

        @Override
        public FilterReply decide(ILoggingEvent event) {
            Logger logger = loggerContext.getLogger(packageName);
            if (event.getLoggerName().startsWith(packageName) && event.getLevel().isGreaterOrEqual(logger.getLevel())) {
                return FilterReply.ACCEPT;
            }
            return FilterReply.DENY;
        }
    }

}
