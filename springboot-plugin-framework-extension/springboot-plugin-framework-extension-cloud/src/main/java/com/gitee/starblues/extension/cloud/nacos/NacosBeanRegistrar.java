package com.gitee.starblues.extension.cloud.nacos;

import com.alibaba.cloud.nacos.NacosConfigManager;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.NacosServiceManager;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import com.gitee.starblues.extension.cloud.nacos.config.NacosConfigProperties;
import com.gitee.starblues.extension.cloud.nacos.config.NacosProperties;
import com.gitee.starblues.extension.cloud.nacos.config.SpringCloudProperties;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.bean.PluginBeanRegistrarExtend;
import com.gitee.starblues.utils.PluginConfigUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author starBlues
 * @version 2.4.6
 */
public class NacosBeanRegistrar implements PluginBeanRegistrarExtend {

    private final static Logger log = LoggerFactory.getLogger(NacosBeanRegistrar.class);

    @Override
    public String key() {
        return "SpringCloud-Nacos";
    }

    @Override
    public void registry(PluginRegistryInfo pluginRegistryInfo) throws Exception {
        GenericApplicationContext mainApplicationContext = pluginRegistryInfo.getMainApplicationContext();
        NacosConfigManager nacosConfigManager = mainApplicationContext.getBean(NacosConfigManager.class);

        SpringCloudProperties springCloudProperties = pluginRegistryInfo.getPluginBinder()
                .bind(SpringCloudProperties.PREFIX, Bindable.of(SpringCloudProperties.class))
                .orElseGet(() -> null);

        if(springCloudProperties == null){
            // todo 注释
            return;
        }

        ConfigService configService = nacosConfigManager.getConfigService();
        NacosProperties nacos = springCloudProperties.getNacos();
        if(nacos != null && nacos.getConfigs() != null){
            // 处理配置文件
            List<NacosConfigProperties> configs = nacos.getConfigs();
            ConfigurableEnvironment environment = pluginRegistryInfo.getPluginApplicationContext().getEnvironment();
            processConfig(configService, environment, configs, nacos.getTimeout());
        }


        // 注册中心
        SpringBeanRegister springBeanRegister = pluginRegistryInfo.getSpringBeanRegister();
        springBeanRegister.registerSingleton("nacosDiscoveryProperties",
                mainApplicationContext.getBean(NacosDiscoveryProperties.class));
        springBeanRegister.registerSingleton("nacosServiceManager",
                mainApplicationContext.getBean(NacosServiceManager.class));
    }


    private void processConfig(ConfigService configService,
                               ConfigurableEnvironment environment,
                               List<NacosConfigProperties> configProperties,
                               Integer timeout){
        checkConfiguration(configProperties, "nacos-config");
        for (NacosConfigProperties properties : configProperties) {
            if(properties == null){
                continue;
            }
            List<NacosConfigProperties> dataIds = new ArrayList<>();
            if(properties.isFollowProfile()){
                String[] activeProfiles = environment.getActiveProfiles();
                for (String activeProfile : activeProfiles) {
                    NacosConfigProperties nacosConfigProperties = new NacosConfigProperties();
                    BeanUtils.copyProperties(properties, nacosConfigProperties);
                    String dataId = PluginConfigUtils.joinConfigFileName(properties.getDataId(), activeProfile);
                    nacosConfigProperties.setDataId(dataId);
                    dataIds.add(nacosConfigProperties);
                }
            } else {
                dataIds.add(properties);
            }

            for (NacosConfigProperties config : dataIds) {
                List<PropertySource<?>> propertySources = loadNacosData(configService, config.getDataId(),
                        config.getGroup(),
                        config.getFileExtension(), timeout);
                NacosPropertySource nacosPropertySource = new NacosPropertySource(propertySources, config.getGroup(),
                        config.getDataId(), new Date(),
                        config.isRefresh());
                if(!nacosPropertySource.getSource().isEmpty()){
                    environment.getPropertySources().addLast(nacosPropertySource);
                }
            }
        }
    }



    private List<PropertySource<?>> loadNacosData(ConfigService configService,
                                                  String dataId, String group, String fileExtension,
                                                  Integer timeout){
        String data = null;
        try {
            data = configService.getConfig(dataId, group, timeout);
            if (StringUtils.isEmpty(data)) {
                log.warn(
                        "Ignore the empty nacos configuration and get it based on dataId[{}] & group[{}]",
                        dataId, group);
                return Collections.emptyList();
            }
            if (log.isDebugEnabled()) {
                log.debug(String.format(
                        "Loading nacos data, dataId: '%s', group: '%s', data: %s", dataId,
                        group, data));
            }
            return NacosDataParserHandler.getInstance().parseNacosData(dataId, data,
                    fileExtension);
        }
        catch (NacosException e) {
            log.error("get data from Nacos error,dataId:{} ", dataId, e);
        }
        catch (Exception e) {
            log.error("parse data from Nacos error,dataId:{},data:{}", dataId, data, e);
        }
        return Collections.emptyList();
    }

    private void checkConfiguration(List<NacosConfigProperties> configs,
                                    String tips) {
        for (int i = 0; i < configs.size(); i++) {
            String dataId = configs.get(i).getDataId();
            if (dataId == null || dataId.trim().length() == 0) {
                throw new IllegalStateException(String.format(
                        "the [ spring.cloud.nacos.config.%s[%s] ] must give a dataId",
                        tips, i));
            }
        }
    }

}
