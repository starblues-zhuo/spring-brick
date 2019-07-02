package com.gitee.starblues.factory.bean.register;

import com.gitee.starblues.exception.PluginBeanFactoryException;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.AnnotationsUtils;
import com.gitee.starblues.utils.OrderExecution;
import com.gitee.starblues.integration.IntegrationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * 插件Controller bean注册者
 * @author zhangzhuo
 * @see Controller
 * @see RestController
 * @version 1.0
 */
public class PluginControllerBeanRegister extends PluginBasicBeanRegister {

    private final static Logger LOG = LoggerFactory.getLogger(PluginControllerBeanRegister.class);

    private final IntegrationConfiguration integrationConfiguration;

    public PluginControllerBeanRegister(ApplicationContext mainApplicationContext) throws PluginBeanFactoryException {
        super(mainApplicationContext);
        this.integrationConfiguration = mainApplicationContext.getBean(IntegrationConfiguration.class);
    }


    @Override
    public String key() {
        return "PluginControllerBeanRegister";
    }

    @Override
    public String registry(BasePlugin basePlugin, Class<?> aClass) throws PluginBeanFactoryException {
        if(!AnnotationsUtils.haveAnnotations(aClass, false, RestController.class, Controller.class)){
            return null;
        }
        setPathPrefix(basePlugin.getWrapper().getPluginId(), aClass);
        return super.register(basePlugin, aClass);
    }

    @Override
    public int order() {
        return OrderExecution.LOW - 10;
    }

    /**
     * 设置请求路径前缀
     * @param aClass controller 类
     */
    private void setPathPrefix(String pluginId,
                               Class<?> aClass) {
        RequestMapping requestMapping = aClass.getAnnotation(RequestMapping.class);
        if(requestMapping == null){
            return;
        }
        String pathPrefix = integrationConfiguration.pluginRestControllerPathPrefix();
        if(integrationConfiguration.enablePluginIdRestControllerPathPrefix()){
            if(pathPrefix != null && !"".equals(pathPrefix)){
                pathPrefix = joiningPath(pathPrefix, pluginId);
            } else {
                pathPrefix = pluginId;
            }
        } else {
            if(pathPrefix == null || "".equals(pathPrefix)){
                // 不启用插件id作为路径前缀, 并且路径前缀为空, 则直接返回。
                return;
            }
        }
        InvocationHandler invocationHandler = Proxy.getInvocationHandler(requestMapping);
        Set<String> definePaths = new HashSet<>();
        definePaths.addAll(Arrays.asList(requestMapping.path()));
        definePaths.addAll(Arrays.asList(requestMapping.value()));
        try {
            Field field = invocationHandler.getClass().getDeclaredField("memberValues");
            field.setAccessible(true);
            Map<String, Object> memberValues = (Map<String, Object>) field.get(invocationHandler);
            String[] newPath = new String[definePaths.size()];
            int i = 0;
            for (String definePath : definePaths) {
                newPath[i++] = joiningPath(pathPrefix, definePath);
            }
            memberValues.put("path", newPath);
            memberValues.put("value", new String[]{});
        } catch (Exception e) {
            LOG.error("Define Plugin RestController pathPrefix error : {}", e.getMessage(), e);
        }
    }



    /**
     * 拼接路径
     * @param path1 路径1
     * @param path2 路径2
     * @return 拼接的路径
     */
    private String joiningPath(String path1, String path2){
        if(path1 != null && path2 != null){
            if(path1.endsWith("/") && path2.startsWith("/")){
                return path1 + path2.substring(1);
            } else if(!path1.endsWith("/") && !path2.startsWith("/")){
                return path1 + "/" + path2;
            } else {
                return path1 + path2;
            }
        } else if(path1 != null){
            return path1;
        } else if(path2 != null){
            return path2;
        } else {
            return "";
        }
    }


}
