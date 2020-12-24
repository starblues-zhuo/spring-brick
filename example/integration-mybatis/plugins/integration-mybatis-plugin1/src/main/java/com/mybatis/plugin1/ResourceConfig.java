package com.mybatis.plugin1;

import com.gitee.starblues.extension.resources.StaticResourceConfig;
import com.gitee.starblues.extension.resources.thymeleaf.SpringBootThymeleafConfig;
import com.gitee.starblues.extension.resources.thymeleaf.ThymeleafConfig;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * @author zhangzhuo
 * @version 1.0
 * @since 2020-12-19
 */
@Component
public class ResourceConfig implements StaticResourceConfig, SpringBootThymeleafConfig {
    @Override
    public Set<String> locations() {
        Set<String> locations = new HashSet<>();
        locations.add("classpath:static");
        locations.add("file:D:\\aa");
        return locations;
    }

    @Override
    public void config(ThymeleafConfig thymeleafConfig) {
        thymeleafConfig.setPrefix("tf");
        thymeleafConfig.setSuffix(".html");
    }
}
