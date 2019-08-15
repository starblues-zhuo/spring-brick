package com.gitee.starblues.factory.bean.register;

import com.gitee.starblues.exception.PluginBeanFactoryException;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.AnnotationsUtils;
import com.gitee.starblues.utils.OrderExecution;
import com.gitee.starblues.integration.IntegrationConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.*;
import java.util.*;

/**
 * 插件Controller bean注册者
 * @author zhangzhuo
 * @see Controller
 * @see RestController
 * @version 1.0
 */
public class PluginControllerBeanRegister
        extends AbstractPluginBeanRegister<PluginControllerBeanRegister.ControllerBeanWrapper> {

    private final static Logger LOG = LoggerFactory.getLogger(PluginControllerBeanRegister.class);

    private final ApplicationContext applicationContext;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final IntegrationConfiguration integrationConfiguration;
    private final PluginBasicBeanRegister pluginBasicBeanRegister;

    public PluginControllerBeanRegister(ApplicationContext mainApplicationContext,
                                        PluginBasicBeanRegister pluginBasicBeanRegister) throws PluginBeanFactoryException {
        super(mainApplicationContext);
        this.applicationContext = mainApplicationContext;
        this.requestMappingHandlerMapping = mainApplicationContext.getBean(RequestMappingHandlerMapping.class);
        this.integrationConfiguration = mainApplicationContext.getBean(IntegrationConfiguration.class);
        this.pluginBasicBeanRegister = pluginBasicBeanRegister;
    }


    @Override
    public String key() {
        return "PluginControllerBeanRegister";
    }

    @Override
    public boolean support(BasePlugin basePlugin, Class<?> aClass) {
        if(requestMappingHandlerMapping == null){
            return false;
        }
        return AnnotationsUtils.haveAnnotations(aClass, false, RestController.class, Controller.class);
    }

    @Override
    public PluginControllerBeanRegister.ControllerBeanWrapper registry(BasePlugin basePlugin, Class<?> aClass) throws PluginBeanFactoryException {
        String beanName = pluginBasicBeanRegister.registry(basePlugin, aClass);
        if(beanName == null || "".equals(beanName)){
            throw new PluginBeanFactoryException("registry "+ aClass.getName() + "failure!");
        }
        Object object = applicationContext.getBean(beanName);
        if(object == null){
            throw new PluginBeanFactoryException("registry "+ aClass.getName() + "failure! " +
                    "Not found The instance of" + aClass.getName());
        }
        PluginControllerBeanRegister.ControllerBeanWrapper controllerBeanWrapper =
                new PluginControllerBeanRegister.ControllerBeanWrapper();
        controllerBeanWrapper.setBeanName(beanName);
        setPathPrefix(basePlugin.getWrapper().getPluginId(), aClass);
        Method getMappingForMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class,
                "getMappingForMethod", Method.class, Class.class);
        getMappingForMethod.setAccessible(true);
        try {
            Method[] methods = aClass.getMethods();
            Set<RequestMappingInfo> requestMappingInfos = new HashSet<>();
            for (Method method : methods) {
                if (isHaveRequestMapping(method)) {
                    RequestMappingInfo requestMappingInfo = (RequestMappingInfo)
                            getMappingForMethod.invoke(requestMappingHandlerMapping, method, aClass);
                    requestMappingHandlerMapping.registerMapping(requestMappingInfo, object, method);
                    requestMappingInfos.add(requestMappingInfo);
                }
            }
            controllerBeanWrapper.setRequestMappingInfos(requestMappingInfos);
            return controllerBeanWrapper;
        } catch (SecurityException e) {
            throw new PluginBeanFactoryException(e);
        } catch (InvocationTargetException e) {
            throw new PluginBeanFactoryException(e);
        } catch (Exception e){
            throw new PluginBeanFactoryException(e);
        }
    }

    @Override
    public void unRegistry(BasePlugin basePlugin,
                           PluginControllerBeanRegister.ControllerBeanWrapper controllerBeanWrapper) throws PluginBeanFactoryException {
        if(controllerBeanWrapper == null){
            return;
        }
        Set<RequestMappingInfo> requestMappingInfos = controllerBeanWrapper.getRequestMappingInfos();
        if(requestMappingInfos != null && !requestMappingInfos.isEmpty()){
            for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
                requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
            }
        }
        String beanName = controllerBeanWrapper.getBeanName();
        if(beanName != null && !"".equals(beanName)){
            pluginBasicBeanRegister.unRegistry(basePlugin, beanName);
        }
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
                // 解决插件启用、禁用后, 路径前缀重复的问题。
                if(definePath.contains(pathPrefix)){
                    newPath[i++] = definePath;
                } else {
                    newPath[i++] = joiningPath(pathPrefix, definePath);
                }
            }
            if(newPath.length == 0){
                newPath = new String[]{ pathPrefix };
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


    /**
     * 方法上是否存在 @RequestMapping 注解
     * @param method method
     * @return boolean
     */
    private boolean isHaveRequestMapping(Method method){
        if (AnnotationUtils.findAnnotation(method, RequestMapping.class) != null) {
            return true;
        } else {
            return false;
        }
    }


    /**
     * Controller Bean的包装
     */
    public static final class ControllerBeanWrapper{
        /**
         * controller bean 名称
         */
        private String beanName;

        /**
         * controller 的 RequestMappingInfo 集合
         */
        private Set<RequestMappingInfo> requestMappingInfos;

        public String getBeanName() {
            return beanName;
        }

        public void setBeanName(String beanName) {
            this.beanName = beanName;
        }

        public Set<RequestMappingInfo> getRequestMappingInfos() {
            return requestMappingInfos;
        }

        public void setRequestMappingInfos(Set<RequestMappingInfo> requestMappingInfos) {
            this.requestMappingInfos = requestMappingInfos;
        }
    }



}
