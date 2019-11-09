package com.gitee.starblues.factory.process.post.bean;

import com.gitee.starblues.extension.PluginControllerProcessor;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.SpringBeanRegister;
import com.gitee.starblues.factory.process.pipe.classs.group.ControllerGroup;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.utils.AopUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;

/**
 * 插件中controller处理者
 *
 * @author zhangzhuo
 * @version 2.1.0
 */
public class PluginControllerPostProcessor implements PluginPostProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private final String KEY = "PluginControllerPostProcessor";

    private final SpringBeanRegister springBeanRegister;
    private final GenericApplicationContext applicationContext;
    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final IntegrationConfiguration integrationConfiguration;

    public PluginControllerPostProcessor(ApplicationContext applicationContext){
        Objects.requireNonNull(applicationContext);
        this.springBeanRegister = new SpringBeanRegister(applicationContext);
        this.applicationContext = (GenericApplicationContext) applicationContext;
        this.requestMappingHandlerMapping = applicationContext.getBean(RequestMappingHandlerMapping.class);
        this.integrationConfiguration = applicationContext.getBean(IntegrationConfiguration.class);
    }


    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            AopUtils.resolveAop(pluginRegistryInfo.getPluginWrapper());
            try {
                List<Class<?>> groupClasses = pluginRegistryInfo.getGroupClasses(ControllerGroup.GROUP_ID);
                if(groupClasses == null || groupClasses.isEmpty()){
                    continue;
                }
                List<ControllerBeanWrapper> controllerBeanWrappers = new ArrayList<>();
                for (Class<?> groupClass : groupClasses) {
                    if(groupClass == null){
                        continue;
                    }
                    ControllerBeanWrapper controllerBeanWrapper = registry(pluginRegistryInfo, groupClass);
                    controllerBeanWrappers.add(controllerBeanWrapper);
                    process(1, pluginRegistryInfo.getPluginWrapper().getPluginId(), groupClass);
                }
                pluginRegistryInfo.addProcessorInfo(getKey(pluginRegistryInfo), controllerBeanWrappers);
            } finally {
                AopUtils.recoverAop();
            }

        }
    }



    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            List<ControllerBeanWrapper> controllerBeanWrappers =
                    pluginRegistryInfo.getProcessorInfo(getKey(pluginRegistryInfo));
            if(controllerBeanWrappers == null || controllerBeanWrappers.isEmpty()){
                continue;
            }
            String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
            for (ControllerBeanWrapper controllerBeanWrapper : controllerBeanWrappers) {
                if(controllerBeanWrapper == null){
                    continue;
                }
                unregister(pluginId, controllerBeanWrapper);
                process(2,
                        pluginRegistryInfo.getPluginWrapper().getPluginId(),
                        controllerBeanWrapper.getBeanClass());
            }

        }
    }

    /**
     * 注册单一插件
     * @param pluginRegistryInfo 注册的插件信息
     * @param aClass controller 类
     * @return ControllerBeanWrapper
     * @throws Exception  Exception
     */
    private ControllerBeanWrapper registry(PluginRegistryInfo pluginRegistryInfo, Class<?> aClass)
            throws Exception {
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        String beanName = springBeanRegister.register(pluginId, aClass);
        if(beanName == null || "".equals(beanName)){
            throw new IllegalArgumentException("registry "+ aClass.getName() + "failure!");
        }
        try {
            Object object = applicationContext.getBean(beanName);
            if(object == null){
                throw new Exception("registry "+ aClass.getName() + "failure! " +
                        "Not found The instance of" + aClass.getName());
            }
            ControllerBeanWrapper controllerBeanWrapper = new ControllerBeanWrapper();
            controllerBeanWrapper.setBeanName(beanName);
            setPathPrefix(pluginId, aClass);
            Method getMappingForMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class,
                    "getMappingForMethod", Method.class, Class.class);
            getMappingForMethod.setAccessible(true);
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
            controllerBeanWrapper.setBeanClass(aClass);
            return controllerBeanWrapper;
        } catch (Exception e){
            // 出现异常, 卸载该 controller bean
            springBeanRegister.unregister(pluginId, beanName);
            throw e;
        }
    }


    /**
     * 卸载具体的Controller操作
     * @param pluginId 插件id
     * @param controllerBeanWrapper controllerBean包装
     */
    private void unregister(String pluginId, ControllerBeanWrapper controllerBeanWrapper) {
        Set<RequestMappingInfo> requestMappingInfos = controllerBeanWrapper.getRequestMappingInfos();
        if(requestMappingInfos != null && !requestMappingInfos.isEmpty()){
            for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
                requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
            }
        }
        String beanName = controllerBeanWrapper.getBeanName();
        if(!StringUtils.isEmpty(beanName)){
            springBeanRegister.unregister(pluginId, beanName);
        }
    }



    /**
     * 得到往RegisterPluginInfo->processorInfo 保存的key
     * @param registerPluginInfo 注册的插件信息
     * @return String
     */
    private String getKey(PluginRegistryInfo registerPluginInfo){
        return KEY + "_" + registerPluginInfo.getPluginWrapper().getPluginId();
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
            log.error("Define Plugin RestController pathPrefix error : {}", e.getMessage(), e);
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

    private void process(int type, String pluginId, Class<?> aClass){
        PluginControllerProcessor pluginControllerProcessor = null;
        try {
            pluginControllerProcessor = applicationContext.getBean(PluginControllerProcessor.class);
        }catch (Exception e){
            pluginControllerProcessor = null;
        }
        if(pluginControllerProcessor == null){
            return;
        }
        if(type == 1){
            try {
                pluginControllerProcessor.registry(pluginId, aClass);
            }catch (Exception e){
                log.error("PluginControllerProcessor process {} {} error of registry",
                        pluginId, aClass.getName());
            }
        } else {
            try {
                pluginControllerProcessor.unRegistry(pluginId, aClass);
            }catch (Exception e){
                log.error("PluginControllerProcessor process {} {} error of unRegistry",
                        pluginId, aClass.getName());
            }
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

        private Class<?> beanClass;

        /**
         * controller 的 RequestMappingInfo 集合
         */
        private Set<RequestMappingInfo> requestMappingInfos;

        public Class<?> getBeanClass() {
            return beanClass;
        }

        public void setBeanClass(Class<?> beanClass) {
            this.beanClass = beanClass;
        }

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
