package com.gitee.starblues.factory.process.post.bean;

import com.gitee.starblues.extension.ExtensionFactory;
import com.gitee.starblues.extension.PluginControllerProcessorExtend;
import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.classs.group.ControllerGroup;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;
import com.gitee.starblues.factory.process.post.bean.model.ControllerWrapper;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.utils.ClassUtils;
import com.gitee.starblues.utils.CommonUtils;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.function.Consumer;

/**
 * 插件中controller处理者
 *
 * @author starBlues
 * @version 2.4.0
 */
public class PluginControllerPostProcessor implements PluginPostProcessor {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    private static final String KEY = "PluginControllerPostProcessor";

    private final RequestMappingHandlerMapping requestMappingHandlerMapping;
    private final IntegrationConfiguration configuration;

    private final List<PluginControllerProcessorExtend> pluginControllerProcessors;

    public PluginControllerPostProcessor(ApplicationContext mainApplicationContext){
        Objects.requireNonNull(mainApplicationContext);
        this.requestMappingHandlerMapping = mainApplicationContext.getBean(RequestMappingHandlerMapping.class);
        this.configuration = mainApplicationContext.getBean(IntegrationConfiguration.class);
        this.pluginControllerProcessors = ExtensionFactory
                .getPluginControllerProcessorExtend(mainApplicationContext);
    }


    @Override
    public void initialize() throws Exception {
        resolveProcessExtend(extend->{
            try {
                extend.initialize();
            }catch (Exception e){
                log.error("'{}' initialize error",
                        extend.getClass().getName(),
                        e);
            }
        });
    }

    @Override
    public void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            List<Class<?>> groupClasses = pluginRegistryInfo.getGroupClasses(ControllerGroup.GROUP_ID);
            if(groupClasses == null || groupClasses.isEmpty()){
                continue;
            }
            String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
            List<ControllerWrapper> controllerBeanWrappers = new ArrayList<>();
            for (Class<?> groupClass : groupClasses) {
                if(groupClass == null){
                    continue;
                }
                try {
                    ControllerWrapper controllerBeanWrapper = registry(pluginRegistryInfo, groupClass);
                    controllerBeanWrappers.add(controllerBeanWrapper);
                } catch (Exception e){
                    pluginRegistryInfo.addProcessorInfo(getKey(pluginRegistryInfo), controllerBeanWrappers);
                    throw e;
                }
            }
            resolveProcessExtend(extend->{
                try {
                    extend.registry(pluginId, controllerBeanWrappers);
                }catch (Exception e){
                    log.error("'{}' process plugin[{}] error in registry",
                            extend.getClass().getName(),
                            pluginId,  e);
                }
            });
            pluginRegistryInfo.addProcessorInfo(getKey(pluginRegistryInfo), controllerBeanWrappers);
        }
    }



    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) {
        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            List<ControllerWrapper> controllerBeanWrappers =
                    pluginRegistryInfo.getProcessorInfo(getKey(pluginRegistryInfo));
            if(controllerBeanWrappers == null || controllerBeanWrappers.isEmpty()){
                continue;
            }
            String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
            for (ControllerWrapper controllerBeanWrapper : controllerBeanWrappers) {
                if(controllerBeanWrapper == null){
                    continue;
                }
                unregister(controllerBeanWrapper);
            }
            resolveProcessExtend(extend->{
                try {
                    extend.unRegistry(pluginId, controllerBeanWrappers);
                }catch (Exception e){
                    log.error("'{}' process plugin[{}] error in unRegistry",
                            extend.getClass().getName(),
                            pluginId,  e);
                }
            });
        }
    }

    /**
     * 注册单一插件
     * @param pluginRegistryInfo 注册的插件信息
     * @param aClass controller 类
     * @return ControllerBeanWrapper
     * @throws Exception  Exception
     */
    private ControllerWrapper registry(PluginRegistryInfo pluginRegistryInfo, Class<?> aClass)
            throws Exception {
        String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
        GenericApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginApplicationContext();
        try {
            Object object = pluginApplicationContext.getBean(aClass);
            ControllerWrapper controllerBeanWrapper = new ControllerWrapper();
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
            throw e;
        }
    }


    /**
     * 卸载具体的Controller操作
     * @param controllerBeanWrapper controllerBean包装
     */
    private void unregister(ControllerWrapper controllerBeanWrapper) {
        Set<RequestMappingInfo> requestMappingInfos = controllerBeanWrapper.getRequestMappingInfos();
        if(requestMappingInfos != null && !requestMappingInfos.isEmpty()){
            for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
                requestMappingHandlerMapping.unregisterMapping(requestMappingInfo);
            }
        }
    }

    /**
     * 调用扩展出的接口控制器
     * @param extendConsumer 扩展消费者
     */
    private void resolveProcessExtend(Consumer<PluginControllerProcessorExtend> extendConsumer){
        if(pluginControllerProcessors == null || pluginControllerProcessors.isEmpty()){
            return;
        }
        for (PluginControllerProcessorExtend pluginControllerProcessor : pluginControllerProcessors) {
            extendConsumer.accept(pluginControllerProcessor);
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
        String pathPrefix = CommonUtils.getPluginRestPrefix(configuration, pluginId);
        if(StringUtils.isNullOrEmpty(pathPrefix)){
            return;
        }
        Set<String> definePaths = new HashSet<>();
        definePaths.addAll(Arrays.asList(requestMapping.path()));
        definePaths.addAll(Arrays.asList(requestMapping.value()));
        try {
            Map<String, Object> memberValues = ClassUtils.getAnnotationsUpdater(requestMapping);
            String[] newPath = new String[definePaths.size()];
            int i = 0;
            for (String definePath : definePaths) {
                // 解决插件启用、禁用后, 路径前缀重复的问题。
                if(definePath.contains(pathPrefix)){
                    newPath[i++] = definePath;
                } else {
                    newPath[i++] = CommonUtils.restJoiningPath(pathPrefix, definePath);
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




}
