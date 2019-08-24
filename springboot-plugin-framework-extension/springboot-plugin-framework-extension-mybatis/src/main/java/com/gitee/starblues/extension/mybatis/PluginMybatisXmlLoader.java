package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.extension.mybatis.configuration.SpringBootMybatisConfig;
import com.gitee.starblues.loader.PluginResourceLoader;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.OrderExecution;
import com.gitee.starblues.utils.OrderPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * 定制插件 Mybatis xml 加载者
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginMybatisXmlLoader implements PluginResourceLoader {

    private final static Logger LOG = LoggerFactory.getLogger(PluginMybatisXmlLoader.class);

    public final static String KEY = "PluginMybatisXmlLoader";

    private final static String TYPE_FILE = "file";
    private final static String TYPE_CLASSPATH = "classpath";
    private final static String TYPE_PACKAGE = "package";

    PluginMybatisXmlLoader(){

    }


    @Override
    public String key() {
        return KEY;
    }

    @Override
    public List<Resource> load(BasePlugin basePlugin) throws Exception {
        if(!(basePlugin instanceof SpringBootMybatisConfig)){
            String error = "Plugin<" + basePlugin.getClass().getName() +
                    "> not implements SpringBootMybatisConfig, Please implements SpringBootMybatisConfig interface";
            LOG.error(error);
            return null;
        }
        SpringBootMybatisConfig springBootMybatisConfig = (SpringBootMybatisConfig) basePlugin;
        Set<String> mybatisMapperXmlLocationsMatch = springBootMybatisConfig.mybatisMapperXmlLocationsMatch();
        if(mybatisMapperXmlLocationsMatch == null || mybatisMapperXmlLocationsMatch.isEmpty()){
            LOG.warn("SpringBootMybatisConfig -> mybatisMapperXmlLocationsMatch return is empty, " +
                    "Please check configuration");
            return Collections.emptyList();
        }
        ResourcePatternResolver resourcePatternResolver =
                new PathMatchingResourcePatternResolver(basePlugin.getWrapper().getPluginClassLoader());
        List<Resource> resources = new ArrayList<>();
        for (String mybatisMapperXmlLocationMatch : mybatisMapperXmlLocationsMatch) {
            if(StringUtils.isEmpty(mybatisMapperXmlLocationMatch)){
                continue;
            }
            List<Resource> loadResources = load(resourcePatternResolver, mybatisMapperXmlLocationMatch);
            if(loadResources != null && !loadResources.isEmpty()){
                resources.addAll(loadResources);
            }
        }
        return resources;
    }

    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }

    /**
     * 加载xml资源
     * @param resourcePatternResolver 资源匹配解决者
     * @param mybatisMapperXmlLocationMatch mybatis xml 文件匹配规则
     * @return List
     */
    private List<Resource> load(ResourcePatternResolver resourcePatternResolver,
                                String mybatisMapperXmlLocationMatch) throws Exception{
        String[] split = mybatisMapperXmlLocationMatch.split(":");
        if(split.length != 2){
            return null;
        }
        String type = split[0];
        String location = split[1];
        String matchLocation = null;
        if(Objects.equals(type, TYPE_CLASSPATH) || Objects.equals(type, TYPE_FILE)){
            matchLocation = location;
        } else if(Objects.equals(type, TYPE_PACKAGE)){
            matchLocation = location.replace(".", "/");
        }
        if(matchLocation == null){
            LOG.error("mybatisMapperXmlLocation {} illegal", mybatisMapperXmlLocationMatch);
            return null;
        }
        try {
            Resource[] resources = resourcePatternResolver.getResources(matchLocation);
            return Arrays.asList(resources);
        } catch (IOException e) {
            LOG.error("mybatisMapperXmlLocation match error : {}", e.getMessage(), e);
            throw e;
        }
    }

}
