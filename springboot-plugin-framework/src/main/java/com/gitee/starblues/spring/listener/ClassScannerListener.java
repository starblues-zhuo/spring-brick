package com.gitee.starblues.spring.listener;

import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.spring.processor.classgroup.CallerClassGroup;
import com.gitee.starblues.spring.processor.classgroup.ComposeClassGroup;
import com.gitee.starblues.spring.processor.classgroup.PluginClassGroup;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.OrderPriority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.ClassUtils;

import java.util.*;

/**
 * @author starBlues
 * @version 1.0
 */
public class ClassScannerListener implements PluginSpringApplicationRunListener {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClassScannerListener.class);

    static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

    private final List<PluginClassGroup> pluginClassGroup = new ArrayList<>();

    public ClassScannerListener(){
        addPluginClassGroup();
    }

    protected void addPluginClassGroup(){
        pluginClassGroup.add(new ComposeClassGroup());
        pluginClassGroup.add(new CallerClassGroup());
    }


    @Override
    public void starting(SpringPluginRegistryInfo registryInfo) throws Exception{
        ClassLoader pluginClassLoader = registryInfo.getPluginWrapper().getPluginClassLoader();
        ResourceLoader resourceLoader = new DefaultResourceLoader(pluginClassLoader);
        ResourcePatternResolver patternResolver = new PathMatchingResourcePatternResolver(resourceLoader);
        Set<String> packageSearchPaths = getPackageSearchPaths(registryInfo);
        MetadataReaderFactory metadataReaderFactory = getMetadataReaderFactory(registryInfo);
        for (String packageSearchPath : packageSearchPaths) {
            Resource[] resources = patternResolver.getResources(packageSearchPath);
            for (Resource resource : resources) {
                MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                String className = metadataReader.getClassMetadata().getClassName();
                Class<?> aClass = pluginClassLoader.loadClass(className);
                groupClass(aClass, registryInfo);
            }
        }
    }

    private void groupClass(Class<?> aClass, SpringPluginRegistryInfo registryInfo){
        boolean filterResult = false;
        for (PluginClassGroup classGroup : pluginClassGroup) {
            String groupId = classGroup.groupId();
            if(ObjectUtils.isEmpty(groupId)){
                continue;
            }
            boolean filter = classGroup.filter(aClass);
            if(filter){
                addClass(groupId, aClass, registryInfo);
                filterResult = true;
            }
        }
        if(!filterResult){
            addClass(PluginClassGroup.OTHER_CLASS_GROUP_ID, aClass, registryInfo);
        }
    }


    private void addClass(String groupId, Class<?> aClass, SpringPluginRegistryInfo registryInfo){
        List<Class<?>> classes = registryInfo.getRegistryInfo(groupId, ArrayList::new);
        classes.add(aClass);
    }


    protected Set<String> getPackageSearchPaths(SpringPluginRegistryInfo registryInfo) {
        Class<?> pluginClass = registryInfo.getPluginWrapper().getPluginClass();
        String[] scanBasePackages = getScanBasePackages(pluginClass);
        Set<String> packageSearchPaths = new HashSet<>();
        ConfigurableEnvironment environment = registryInfo.getPluginSpringApplication()
                .getApplicationContext().getEnvironment();
        for (String scanBasePackage : scanBasePackages) {
            String scanPackageName = ClassUtils.convertClassNameToResourcePath(
                    environment.resolveRequiredPlaceholders(scanBasePackage));
            scanPackageName = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    scanPackageName + '/' + DEFAULT_RESOURCE_PATTERN;
            packageSearchPaths.add(scanPackageName);
        }
        return packageSearchPaths;
    }


    protected String[] getScanBasePackages(Class<?> pluginClass){
        SpringBootApplication springBootApplication = pluginClass.getAnnotation(SpringBootApplication.class);
        if(springBootApplication != null){
            String[] scanBasePackages = springBootApplication.scanBasePackages();
            if(scanBasePackages.length > 0){
                return scanBasePackages;
            }
        }
        return new String[]{ pluginClass.getPackage().getName() };
    }

    protected MetadataReaderFactory getMetadataReaderFactory(SpringPluginRegistryInfo registryInfo){
        ClassLoader pluginClassLoader = registryInfo.getPluginWrapper().getPluginClassLoader();
        return new CachingMetadataReaderFactory(pluginClassLoader);
    }


    @Override
    public OrderPriority order() {
        return OrderPriority.getHighPriority();
    }

    @Override
    public ListenerRunMode runMode() {
        return ListenerRunMode.ALL;
    }
}
