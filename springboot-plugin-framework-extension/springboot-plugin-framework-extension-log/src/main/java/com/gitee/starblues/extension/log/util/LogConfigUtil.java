package com.gitee.starblues.extension.log.util;

import com.gitee.starblues.extension.log.annotation.ConfigItem;
import com.gitee.starblues.extension.log.config.LogConfig;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.ResourceUtils;
import org.pf4j.PluginWrapper;
import org.pf4j.RuntimeMode;
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
    public static final String ROOT_PLUGIN_SIGN = "~";

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


    public static String getLogFile(PluginRegistryInfo pluginRegistryInfo, LogConfig logConfig){
        String rootDir = logConfig.getRootDir();
        String home;
        PluginWrapper pluginWrapper = pluginRegistryInfo.getPluginWrapper();
        IntegrationConfiguration configuration = pluginRegistryInfo.getConfiguration();
        String pluginRootDir;
        RuntimeMode runtimeMode = pluginWrapper.getRuntimeMode();
        if(runtimeMode == RuntimeMode.DEVELOPMENT){
            pluginRootDir = pluginWrapper.getPluginPath().toString();
        } else {
            pluginRootDir =  configuration.pluginPath();
        }
        if(StringUtils.isNullOrEmpty(rootDir)){
            home = CommonUtils.joiningFilePath(pluginRootDir, "logs");
        } else {
            home = ResourceUtils.getAbsolutePath(pluginRegistryInfo, rootDir);
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
