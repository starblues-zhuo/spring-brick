package com.gitee.starblues.extension.log.util;

import com.gitee.starblues.extension.log.annotation.ConfigItem;
import com.gitee.starblues.extension.log.config.LogConfig;
import com.gitee.starblues.extension.log.logback.LogbackLogRegistry;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.utils.CommonUtils;
import org.pf4j.PluginWrapper;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
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

/**
 * LogConfig 对象和 xml 映射的工具类
 * @author starBlues
 * @version 2.4.3
 */
public class LogConfigUtil {

    private static final Logger LOG = LoggerFactory.getLogger(LogConfigUtil.class);


    public static LogConfig getLogConfig(Resource xmlResource, PluginRegistryInfo pluginRegistryInfo)
            throws Exception{
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        String configText = readConfigText(xmlResource);
        LogConfig logConfig = (LogConfig) xml2object(configText);
        checkLogConfig(logConfig, pluginWrapper.getPluginId());
        String packageName = logConfig.getPackageName();
        if(StringUtils.isNullOrEmpty(packageName)){
            logConfig.setPackageName(pluginRegistryInfo.getBasePlugin().scanPackage());
        }
        return logConfig;
    }


    public static String getLogFile(PluginWrapper pluginWrapper, LogConfig logConfig){
        String rootDir = logConfig.getRootDir();
        String home;
        String pluginPath = pluginWrapper.getPluginPath().toString();
        if(StringUtils.isNullOrEmpty(rootDir)){
            home = CommonUtils.joiningFilePath(pluginPath, "logs");
        } else {
            if(rootDir.startsWith(LogConfig.ROOT_PLUGIN_SIGN)){
                // 如果root路径中开始存在ROOT_PLUGIN_SIGN,则说明进行插件根路替换
                home = rootDir.replaceFirst("\\" + LogConfig.ROOT_PLUGIN_SIGN, "");
                home = CommonUtils.joiningFilePath(pluginPath, home);
            } else {
                home = rootDir;
            }
        }
        String fileName = logConfig.getFileName();
        if (StringUtils.isNullOrEmpty(fileName)) {
            fileName = pluginWrapper.getPluginId();
        }
        return CommonUtils.joiningFilePath(home, pluginWrapper.getPluginId(), fileName);
    }

    private static void checkLogConfig(LogConfig logConfig, String pluginId) {
        String fileName = logConfig.getFileName();
        if (StringUtils.isNullOrEmpty(fileName)) {
            logConfig.setFileName(pluginId.concat("-log"));
        }
        Field[] fields = LogConfig.class.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAccessible()) {
                field.setAccessible(true);
            }
            ConfigItem configItem = field.getDeclaredAnnotation(ConfigItem.class);
            if (configItem == null) {
                continue;
            }
            try {
                Object fieldValue = field.get(logConfig);
                Class<?> fieldType = field.getType();
                if (fieldValue == null || "".equals(fieldValue.toString()) ||
                        ObjectUtil.isEmptyObject(fieldType, fieldValue)) {
                    String defaultValue = configItem.defaultValue();
                    LOG.debug("Field {} is not config or invalid in log config of plugin {}, set it to default value {}.", field.getName(), defaultValue, pluginId);
                    Object fixedValue = ObjectUtil.parseBasicTypeValue(fieldType, defaultValue);
                    field.set(logConfig, fixedValue);
                }
            } catch (IllegalAccessException e) {
                LOG.error("Failed to check config item {} in log config.", field.getName());
            }
        }
    }

    private static String readConfigText(Resource resource) throws IOException {
        String fileContent;
        try (InputStream inputStream = resource.getInputStream();
             ByteArrayOutputStream stream = new ByteArrayOutputStream()) {
            byte[] buff = new byte[1024];
            int len;
            while ((len = inputStream.read(buff)) != -1) {
                stream.write(buff, 0, len);
            }
            byte[] data = stream.toByteArray();
            fileContent = new String(data);
        }
        return fileContent;
    }

    private static Object xml2object(String xml) throws Exception {
        Object object;
        try {
            JAXBContext context = JAXBContext.newInstance(LogConfig.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            StringReader stringReader = new StringReader(xml);
            object = unmarshaller.unmarshal(stringReader);
        } catch (JAXBException e) {
            e.printStackTrace();
            throw new Exception("Invalid xml definition");
        }
        return object;
    }
}
