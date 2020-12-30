package com.gitee.starblues.extension.mybatis;

import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.extension.mybatis.group.MybatisConfigGroup;
import com.gitee.starblues.extension.mybatis.group.PluginEntityAliasesGroup;
import com.gitee.starblues.extension.mybatis.group.PluginMapperGroup;
import com.gitee.starblues.extension.mybatis.mybatisplus.MybatisPlusProcessor;
import com.gitee.starblues.extension.mybatis.tkmyabtis.TkMybatisProcessor;
import com.gitee.starblues.factory.process.pipe.PluginPipeProcessorExtend;
import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroupExtend;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * mybatis 扩展
 * @author zhangzhuo
 * @version 2.3
 */
public class SpringBootMybatisExtension extends AbstractExtension {

    private static final String KEY = "SpringBootMybatisPlusExtension";

    private final Type type;

    /**
     * 初始化扩展
     * @param type 根据当前环境所集成的框架来选择Type类型
     */
    public SpringBootMybatisExtension(Type type) {
        if(type == null){
            this.type = Type.MYBATIS;
        } else {
            this.type = type;
        }
    }

    @Override
    public String key() {
        return KEY;
    }

    @Override
    public void initialize(ApplicationContext applicationContext) throws Exception {
    }

    @Override
    public List<PluginClassGroupExtend> getPluginClassGroup(ApplicationContext applicationContext) {
        final List<PluginClassGroupExtend> pluginClassGroups = new ArrayList<>();
        pluginClassGroups.add(new MybatisConfigGroup());
        pluginClassGroups.add(new PluginEntityAliasesGroup());
        pluginClassGroups.add(new PluginMapperGroup());
        return pluginClassGroups;
    }

    @Override
    public List<PluginPipeProcessorExtend> getPluginPipeProcessor(ApplicationContext applicationContext) {
        final List<PluginPipeProcessorExtend> pluginPipeProcessorExtends = new ArrayList<>();
        if(type == Type.MYBATIS_PLUS){
            pluginPipeProcessorExtends.add(new MybatisPlusProcessor(applicationContext));
        } else if(type == Type.TK_MYBATIS){
            pluginPipeProcessorExtends.add(new TkMybatisProcessor(applicationContext));
        } else {
            pluginPipeProcessorExtends.add(new MybatisProcessor(applicationContext));
        }
        return pluginPipeProcessorExtends;
    }

    public enum Type{
        MYBATIS,
        MYBATIS_PLUS,
        TK_MYBATIS
    }

}
