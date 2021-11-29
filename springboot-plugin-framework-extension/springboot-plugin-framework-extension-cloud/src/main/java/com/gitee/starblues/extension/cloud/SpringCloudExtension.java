package com.gitee.starblues.extension.cloud;

import com.gitee.starblues.extension.AbstractExtension;
import com.gitee.starblues.extension.cloud.nacos.NacosBeanRegistrar;
import com.gitee.starblues.extension.cloud.openfeign.FeignClientBeanGroup;
import com.gitee.starblues.extension.cloud.openfeign.OpenFeignRegistrar;
import com.gitee.starblues.factory.process.pipe.bean.PluginBeanRegistrarExtend;
import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroupExtend;
import org.springframework.context.ApplicationContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author starBlues
 * @version 2.4.6
 */
public class SpringCloudExtension extends AbstractExtension {

    private final Type type;
    private final boolean enableOpenFeign;

    public SpringCloudExtension(Type type, boolean enableOpenFeign){
        this.type = type;
        this.enableOpenFeign = enableOpenFeign;
    }

    public SpringCloudExtension(Type type){
        this(type, false);
    }

    @Override
    public String key() {
        return "SpringCloud";
    }

    @Override
    public List<PluginBeanRegistrarExtend> getPluginBeanRegistrar(ApplicationContext mainApplicationContext) {
        List<PluginBeanRegistrarExtend> pluginBeanRegistrarExtends = new ArrayList<>();
        if(type == Type.NACOS){
            pluginBeanRegistrarExtends.add(new NacosBeanRegistrar());
        }
        if(enableOpenFeign){
            pluginBeanRegistrarExtends.add(new OpenFeignRegistrar());
        }
        return pluginBeanRegistrarExtends;
    }

    @Override
    public List<PluginClassGroupExtend> getPluginClassGroup(ApplicationContext mainApplicationContext) {
        if(enableOpenFeign){
            List<PluginClassGroupExtend> pluginClassGroupExtends = new ArrayList<>(1);
            pluginClassGroupExtends.add(new FeignClientBeanGroup());
            return pluginClassGroupExtends;
        }
        return null;
    }

    public enum Type{
        /**
         * nacos
         */
        NACOS
    }

}
