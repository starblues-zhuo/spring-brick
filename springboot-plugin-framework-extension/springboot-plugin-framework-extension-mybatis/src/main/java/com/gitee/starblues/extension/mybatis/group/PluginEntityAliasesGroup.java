package com.gitee.starblues.extension.mybatis.group;

import com.gitee.starblues.extension.mybatis.mybatisplus.SpringBootMybatisPlusConfig;
import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroupExtend;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.AnnotationsUtils;
import org.apache.ibatis.type.Alias;

import java.util.Set;

/**
 * 插件中的实体类别名
 *
 * @author zhangzhuo
 * @version 1.0
 */
public class PluginEntityAliasesGroup implements PluginClassGroupExtend {

    public static final String KEY = "plugin_mybatis_alias";

    private Set<String> typeAliasesPackage;

    public PluginEntityAliasesGroup() {
    }

    @Override
    public String groupId() {
        return KEY;
    }

    @Override
    public void initialize(BasePlugin basePlugin) {
        if(basePlugin instanceof SpringBootMybatisPlusConfig){
            SpringBootMybatisPlusConfig mybatisPlusConfig = (SpringBootMybatisPlusConfig) basePlugin;
            typeAliasesPackage = null;
        }
    }


    @Override
    public boolean filter(Class<?> aClass) {
        if(AnnotationsUtils.haveAnnotations(aClass, false, Alias.class)){
            return true;
        }
        if(typeAliasesPackage == null || typeAliasesPackage.isEmpty()){
            return false;
        }
        for (String packageName : typeAliasesPackage) {
            if(aClass.getPackage().getName().equals(packageName)){
                return true;
            }
        }
        return false;
    }

    @Override
    public String key() {
        return "PluginEntityAliasesGroup";
    }


}
