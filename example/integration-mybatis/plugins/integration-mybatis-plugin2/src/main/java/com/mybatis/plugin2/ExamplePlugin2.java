package com.mybatis.plugin2;

import com.gitee.starblues.extension.mybatis.configuration.SpringBootMybatisConfig;
import com.gitee.starblues.extension.resources.StaticResourceConfig;
import com.gitee.starblues.realize.BasePlugin;
import org.pf4j.PluginWrapper;

import java.util.HashSet;
import java.util.Set;

/**
 * description
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class ExamplePlugin2 extends BasePlugin
        implements SpringBootMybatisConfig, StaticResourceConfig {

    private final Set<String> mybatisMapperXmlLocationsMatch = new HashSet<>();
    private final Set<String> locations = new HashSet<>();

    public ExamplePlugin2(PluginWrapper wrapper) {
        super(wrapper);
        mybatisMapperXmlLocationsMatch.add("classpath:mapper/*Mapper.xml");
        locations.add("classpath:/static");
    }

    @Override
    protected void startEvent() {

    }

    @Override
    protected void deleteEvent() {

    }

    @Override
    protected void stopEvent() {

    }

    @Override
    public Set<String> mybatisMapperXmlLocationsMatch() {
        return mybatisMapperXmlLocationsMatch;
    }

    @Override
    public Set<String> locations() {
        return locations;
    }
}
