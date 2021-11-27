package com.gitee.starblues.factory.process.pipe.bean;

import com.gitee.starblues.annotation.ConfigDefinition;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.loader.ResourceWrapper;
import com.gitee.starblues.factory.process.pipe.loader.load.PluginConfigFileLoader;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.integration.pf4j.descriptor.DefaultPluginDescriptorExtend;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.PluginConfigUtils;
import org.pf4j.PluginDescriptor;
import org.pf4j.RuntimeMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.env.PropertiesPropertySourceLoader;
import org.springframework.boot.env.PropertySourceLoader;
import org.springframework.boot.env.YamlPropertySourceLoader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 解析springboot中的插件中的配置文件。目前支持: prop、yaml
 * @author starBlues
 * @version 2.4.5
 */
public class SpringBootConfigFileRegistrar implements PluginBeanRegistrar{

    /**
     * The "active profiles" property name.
     */
    private static final String ACTIVE_PROFILES_PROPERTY = "spring.profiles.active";

    /**
     * The "includes profiles" property name.
     */
    private static final String INCLUDE_PROFILES_PROPERTY = "spring.profiles.include";

    private final Logger logger = LoggerFactory.getLogger(SpringBootConfigFileRegistrar.class);

    private final IntegrationConfiguration integrationConfiguration;
    private final List<PropertySourceLoader> propertySourceLoaders;

    public SpringBootConfigFileRegistrar(ApplicationContext mainApplicationContext){
        integrationConfiguration =
                mainApplicationContext.getBean(IntegrationConfiguration.class);
        this.propertySourceLoaders = new ArrayList<>();
        addPropertySourceLoader();
    }

    protected void addPropertySourceLoader(){
        this.propertySourceLoaders.add(new YamlPropertySourceLoader());
        this.propertySourceLoaders.add(new PropertiesPropertySourceLoader());
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        ConfigurableEnvironment environment = pluginRegistryInfo.getPluginApplicationContext().getEnvironment();

        PluginConfigUtils.FileNamePack fileNamePack = getConfigFileName(pluginRegistryInfo);
        if(fileNamePack == null){
            return;
        }
        String configFileName = PluginConfigUtils.joinConfigFileName(fileNamePack);

        PluginConfigFileLoader pluginConfigFileLoader = new PluginConfigFileLoader(
                integrationConfiguration.pluginConfigFilePath(), configFileName
        );

        ResourceWrapper resourceWrapper = pluginConfigFileLoader.load(pluginRegistryInfo);
        List<Resource> resources = resourceWrapper.getResources();
        if(ObjectUtils.isEmpty(resources)){
            return;
        }
        // 获取注解中原始文件配置
        List<PropertySource<?>> propProfiles = getPropProfiles(resources);
        if(ObjectUtils.isEmpty(propProfiles)){
            return;
        }
        for (PropertySource<?> propertySource : propProfiles) {
            environment.getPropertySources().addLast(propertySource);
        }
        // 发现原始文件中配置的 profiles
        List<Profile> profiles = getProfiles(environment);
        if(!ObjectUtils.isEmpty(profiles)){
            loadProfilesConfig(pluginRegistryInfo, fileNamePack, environment, profiles);
        }
    }

    /**
     * 获取插件中定义的配置文件名称信息
     * @param pluginRegistryInfo PluginRegistryInfo
     * @return 配置文件信息
     */
    private PluginConfigUtils.FileNamePack getConfigFileName(PluginRegistryInfo pluginRegistryInfo){
        // 先从插件引导配置获取配置文件
        PluginDescriptor descriptor = pluginRegistryInfo.getPluginWrapper().getDescriptor();
        if(descriptor instanceof DefaultPluginDescriptorExtend){
            DefaultPluginDescriptorExtend descriptorExtend = (DefaultPluginDescriptorExtend) descriptor;
            String configFileName = descriptorExtend.getConfigFileName();
            if(!ObjectUtils.isEmpty(configFileName)){
                return new PluginConfigUtils.FileNamePack(configFileName, descriptorExtend.getConfigFileProfile());
            }
        }

        // 如果插件引导文件未设置, 则从引导类的注解添加 ConfigDefinition
        //加载成PropertySource对象，并添加到Environment环境中
        BasePlugin basePlugin = pluginRegistryInfo.getBasePlugin();
        ConfigDefinition configDefinition = basePlugin.getClass().getAnnotation(ConfigDefinition.class);
        if(configDefinition == null){
            return null;
        }
        RuntimeMode runtimeMode = pluginRegistryInfo.getPluginWrapper().getRuntimeMode();
        return PluginConfigUtils.getConfigFileName(
                configDefinition.fileName(),
                configDefinition.prodSuffix(),
                configDefinition.devSuffix(),
                runtimeMode);
    }

    /**
     * 从 Resource 中解析出 PropertySource
     * @param resources resources
     * @return List
     * @throws IOException 加载文件 IOException 异常
     */
    private List<PropertySource<?>> getPropProfiles(List<Resource> resources) throws IOException {
        List<PropertySource<?>> propProfiles = new ArrayList<>();
        if(resources == null || resources.isEmpty()){
            return propProfiles;
        }
        for (Resource resource : resources) {
            if(resource == null || !resource.exists()){
                continue;
            }
            String filename = resource.getFilename();
            if(ObjectUtils.isEmpty(filename)){
                logger.error("File name is empty!");
                return null;
            }
            for (PropertySourceLoader propertySourceLoader : propertySourceLoaders) {
                if(!canLoadFileExtension(propertySourceLoader, filename)){
                    continue;
                }
                List<PropertySource<?>> propertySources = propertySourceLoader.load(filename, resource);
                if(ObjectUtils.isEmpty(propertySources)){
                    continue;
                }
                propProfiles.addAll(propertySources);
            }
        }
        return propProfiles;
    }

    /**
     * 加载 spring.profiles.active/spring.profiles.include 定义的配置
     * @param pluginRegistryInfo 插件注册信息
     * @param fileNamePack 配置文件包装
     * @param environment ConfigurableEnvironment
     * @param profiles 主配置文件中定义的值
     * @throws Exception Exception
     */
    private void loadProfilesConfig(PluginRegistryInfo pluginRegistryInfo,
                                    PluginConfigUtils.FileNamePack fileNamePack,
                                    ConfigurableEnvironment environment, List<Profile> profiles) throws Exception {
        // 解析当前文件名称
        for (Profile profile : profiles) {
            String name = profile.getName();
            String fileName = PluginConfigUtils.joinConfigFileName(fileNamePack.getSourceFileName(), name);
            PluginConfigFileLoader pluginConfigFileLoader = new PluginConfigFileLoader(
                    integrationConfiguration.pluginConfigFilePath(), fileName
            );

            ResourceWrapper resourceWrapper = pluginConfigFileLoader.load(pluginRegistryInfo);
            List<Resource> resources = resourceWrapper.getResources();
            if(ObjectUtils.isEmpty(resources)){
                continue;
            }
            List<PropertySource<?>> propProfiles = getPropProfiles(resources);
            if(ObjectUtils.isEmpty(propProfiles)){
                return;
            }
            for (PropertySource<?> propertySource : propProfiles) {
                environment.getPropertySources().addLast(propertySource);
            }
        }
        // 重新设置 ActiveProfiles
        String[] names = profiles.stream()
                .filter((profile) -> profile != null && !profile.isDefaultProfile())
                .map(Profile::getName).toArray(String[]::new);
        environment.setActiveProfiles(names);
    }

    /**
     * 根据文件后缀判断是否可解析
     * @param loader PropertySourceLoader
     * @param name 文件名称
     * @return boolean
     */
    private boolean canLoadFileExtension(PropertySourceLoader loader, String name) {
        return Arrays.stream(loader.getFileExtensions())
                .anyMatch((fileExtension) -> StringUtils.endsWithIgnoreCase(name,
                        fileExtension));
    }


    /**
     * 得到主配置文件中的 Profile
     * @param environment Environment
     * @return List<Profile>
     */
    private List<Profile> getProfiles(Environment environment) {
        List<Profile> profiles = new ArrayList<>();
        Set<Profile> activatedViaProperty = getProfilesActivatedViaProperty(environment);
        profiles.addAll(getOtherActiveProfiles(environment, activatedViaProperty));
        profiles.addAll(activatedViaProperty);
        profiles.removeIf(
                (profile) -> (profile != null && profile.isDefaultProfile()));
        return profiles;
    }

    private Set<Profile> getProfilesActivatedViaProperty(Environment environment) {
        if (!environment.containsProperty(ACTIVE_PROFILES_PROPERTY)
                && !environment.containsProperty(INCLUDE_PROFILES_PROPERTY)) {
            return Collections.emptySet();
        }
        Binder binder = Binder.get(environment);
        Set<Profile> activeProfiles = new LinkedHashSet<>();
        activeProfiles.addAll(getProfiles(binder, INCLUDE_PROFILES_PROPERTY));
        activeProfiles.addAll(getProfiles(binder, ACTIVE_PROFILES_PROPERTY));
        return activeProfiles;
    }

    private List<Profile> getOtherActiveProfiles(Environment environment, Set<Profile> activatedViaProperty) {
        return Arrays.stream(environment.getActiveProfiles()).map(Profile::new)
                .filter((profile) -> !activatedViaProperty.contains(profile))
                .collect(Collectors.toList());
    }

    private Set<Profile> getProfiles(Binder binder, String name) {
        return binder.bind(name, String[].class).map(this::asProfileSet)
                .orElse(Collections.emptySet());
    }

    private Set<Profile> asProfileSet(String[] profileNames) {
        List<Profile> profiles = new ArrayList<>();
        for (String profileName : profileNames) {
            profiles.add(new Profile(profileName));
        }
        return new LinkedHashSet<>(profiles);
    }

    private static class Profile {

        private final String name;

        private final boolean defaultProfile;

        Profile(String name) {
            this(name, false);
        }

        Profile(String name, boolean defaultProfile) {
            Assert.notNull(name, "Name must not be null");
            this.name = name;
            this.defaultProfile = defaultProfile;
        }

        public String getName() {
            return this.name;
        }

        public boolean isDefaultProfile() {
            return this.defaultProfile;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            }
            if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            return ((Profile) obj).name.equals(this.name);
        }

        @Override
        public int hashCode() {
            return this.name.hashCode();
        }

        @Override
        public String toString() {
            return this.name;
        }

    }

}
