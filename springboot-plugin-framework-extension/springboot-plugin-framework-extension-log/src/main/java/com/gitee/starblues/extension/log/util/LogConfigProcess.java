package com.gitee.starblues.extension.log.util;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.spi.FilterReply;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import com.gitee.starblues.extension.log.annotation.ConfigItem;
import com.gitee.starblues.extension.log.config.LogConfig;
import org.pf4j.PluginWrapper;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LogConfigProcess {

    private org.slf4j.Logger log = LoggerFactory.getLogger(LogConfigProcess.class);

    private final Map<String, LogConfig> pluginLogMap = new HashMap<>();

    private static LogConfigProcess instance = null;

    private LogConfigProcess() {
    }

    public static LogConfigProcess getInstance() {
        if (instance == null) {
            synchronized (LogConfigProcess.class) {
                if (instance == null) {
                    instance = new LogConfigProcess();
                }
            }
        }
        return instance;
    }

    public void loadLogConfig(List<Resource> resources, PluginWrapper pluginWrapper) {
        resources.forEach(resource -> {
            String configText;
            try {
                configText = readConfigText(resource);
            } catch (IOException e) {
                log.error("Failed to read log configuration.", e);
                return;
            }
            LogConfig logConfig;
            try {
                logConfig = (LogConfig) xml2object(configText);
            } catch (Exception e) {
                log.error("", e);
                return;
            }
            checkLogConfig(logConfig, pluginWrapper.getPluginId());
            String pluginClass = pluginWrapper.getDescriptor().getPluginClass();
            String packageName = pluginClass.substring(0, pluginClass.lastIndexOf("."));
            splitPluginLog(pluginWrapper, logConfig, packageName);
            String pluginId = pluginWrapper.getPluginId();
            logConfig.setPattern(packageName);
            pluginLogMap.put(pluginId, logConfig);
        });
    }

    public void unloadLogConfig(PluginWrapper pluginWrapper) {
        pluginLogMap.remove(pluginWrapper.getPluginId());
    }

    private void splitPluginLog(PluginWrapper pluginWrapper, LogConfig logConfig, String packageName) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        Logger logger = context.getLogger(packageName);
        logger.detachAndStopAllAppenders();

        ConsoleAppender<ILoggingEvent> consoleAppender = createConsoleAppender(pluginWrapper, logConfig, packageName);
        RollingFileAppender<ILoggingEvent> fileAppender = createFileAppender(pluginWrapper, logConfig, packageName);

        logger.setAdditive(false);
        logger.setLevel(Level.toLevel(logConfig.getLevel()));
        logger.addAppender(consoleAppender);
        logger.addAppender(fileAppender);
    }

    private ConsoleAppender<ILoggingEvent> createConsoleAppender(PluginWrapper pluginWrapper, LogConfig logConfig, String packageName) {
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

    private RollingFileAppender<ILoggingEvent> createFileAppender(PluginWrapper pluginWrapper, LogConfig logConfig, String packageName) {
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
        RollingFileAppender<ILoggingEvent> appender = new RollingFileAppender<>();
        Filter<ILoggingEvent> filter = new LogFilter(packageName);
        filter.start();

        appender.addFilter(filter);

        appender.setContext(context);
        appender.setName(pluginWrapper.getPluginId().concat("-file"));

        String fileName = logConfig.getFileName();
        if (fileName == null || "".equals(fileName)) {
            fileName = pluginWrapper.getPluginId();
        }

        String home = pluginWrapper.getPluginPath().toString();
        String logPrefix = home.concat("/logs/").concat(pluginWrapper.getPluginId()).concat("/").concat(fileName);
        appender.setFile(OptionHelper.substVars(logPrefix.concat(".log"), context));

        appender.setAppend(true);
        appender.setPrudent(false);

        SizeAndTimeBasedRollingPolicy<ILoggingEvent> policy = new SizeAndTimeBasedRollingPolicy<>();

        String fp = OptionHelper.substVars(logPrefix.concat(".%d{yyyy-MM-dd}%i.log"), context);
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

    private void checkLogConfig(LogConfig logConfig, String pluginId) {
        String fileName = logConfig.getFileName();
        if (fileName == null || "".equals(fileName)) {
            logConfig.setFileName(pluginId.concat("Log"));
        }
        Field[] fields = LogConfig.class.getDeclaredFields();
        Arrays.stream(fields).forEach(field -> {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            ConfigItem configItem = field.getDeclaredAnnotation(ConfigItem.class);
            if (configItem == null) {
                return;
            }
            try {
                Object fieldValue = field.get(logConfig);
                Class<?> fieldType = field.getType();
                if ("".equals(fieldValue.toString()) || ObjectUtil.isEmptyObject(fieldType, fieldValue)) {
                    String defaultValue = configItem.defaultValue();
                    log.debug("Field {} is not config or invalid in log config of plugin {}, set it to default value {}.", field.getName(), defaultValue, pluginId);
                    Object fixedValue = ObjectUtil.parseBasicTypeValue(fieldType, defaultValue);
                    field.set(logConfig, fixedValue);
                }
            } catch (IllegalAccessException e) {
                log.error("Failed to check config item {} in log config.", field.getName());
            }
        });
    }

    public Map<String, LogConfig> getPluginLogMap() {
        return pluginLogMap;
    }

    private String readConfigText(Resource resource) throws IOException {
        String fileContent;
        try (InputStream inputStream = resource.getInputStream();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buff = new byte[1024];
            int len;
            while ((len = inputStream.read(buff)) != -1) {
                baos.write(buff, 0, len);
            }
            byte[] data = baos.toByteArray();
            fileContent = new String(data);
        }
        return fileContent;
    }

    private Object xml2object(String xml) throws Exception {
        Object object;
        try {
            JAXBContext context = JAXBContext.newInstance(LogConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader stringReader = new StringReader(xml);
            object = unmarshaller.unmarshal(stringReader);
        } catch (JAXBException e) {
            throw new Exception("Invalid xml definition");
        }
        return object;
    }

}
