package com.gitee.starblues.spring.processor;

import com.gitee.starblues.factory.process.post.bean.model.ControllerWrapper;
import com.gitee.starblues.integration.IntegrationConfiguration;
import com.gitee.starblues.spring.PluginSpringApplication;
import com.gitee.starblues.spring.SpringPluginRegistryInfo;
import com.gitee.starblues.utils.ClassUtils;
import com.gitee.starblues.utils.CommonUtils;
import com.gitee.starblues.utils.ObjectUtils;
import com.gitee.starblues.utils.ReflectionUtils;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginControllerRegistryProcessor  implements SpringPluginProcessor{

    private final static Logger LOG = LoggerFactory.getLogger(PluginControllerRegistryProcessor.class);

    private static final String PROCESS_CONTROLLERS = "PROCESS_SUCCESS";

    private GenericApplicationContext mainApplicationContext;

    private RequestMappingHandlerMapping requestMappingHandlerMapping;
    private Method getMappingForMethod;

    private final AtomicBoolean canRegistered = new AtomicBoolean(false);


    @Override
    public void initialize(GenericApplicationContext mainApplicationContext) throws Exception {
        this.requestMappingHandlerMapping = mainApplicationContext.getBean(RequestMappingHandlerMapping.class);
        this.getMappingForMethod = ReflectionUtils.findMethod(RequestMappingHandlerMapping.class,
                "getMappingForMethod", Method.class, Class.class);
        if(getMappingForMethod == null){
            LOG.warn("RequestMappingHandlerMapping 类中没有发现 <getMappingForMethod> 方法, 无法注册插件接口. " +
                    "请检查当前环境是否为 web 环境");
        }
        this.getMappingForMethod.setAccessible(true);
        canRegistered.set(true);
    }


    @Override
    public void refreshBefore(SpringPluginRegistryInfo registryInfo) throws Exception {
        if(!canRegistered.get()){
            return;
        }
        PluginSpringApplication pluginSpringApplication = registryInfo.getPluginSpringApplication();
        AnnotationConfigApplicationContext applicationContext =
                (AnnotationConfigApplicationContext)pluginSpringApplication.getApplicationContext();
        applicationContext.registerBean("changeRestPathPostProcessor",
                ChangeRestPathPostProcessor.class, ()-> new ChangeRestPathPostProcessor(registryInfo));
    }

    @Override
    public void refreshAfter(SpringPluginRegistryInfo registryInfo) throws Exception {
        if(!canRegistered.get()){
            return;
        }
        IntegrationConfiguration configuration = registryInfo.getConfiguration();
        if(ObjectUtils.isEmpty(configuration.pluginRestPathPrefix())
                && !configuration.enablePluginIdRestPathPrefix()){
            // 如果 pluginRestPathPrefix 为空, 并且没有启用插件id作为插件前缀, 则不进行修改插件controller地址前缀
            return;
        }
        String pluginId = registryInfo.getPluginWrapper().getPluginId();
        List<ControllerWrapper> controllerWrappers = registryInfo.getRegistryInfo(PROCESS_CONTROLLERS);
        if(ObjectUtils.isEmpty(controllerWrappers)){
            LOG.warn("插件 [{}] 没有发现可注册的 Controller", pluginId);
            return;
        }
        ConfigurableApplicationContext applicationContext = registryInfo.getPluginSpringApplication().getApplicationContext();

        Iterator<ControllerWrapper> iterator = controllerWrappers.iterator();
        while (iterator.hasNext()){
            ControllerWrapper controllerWrapper = iterator.next();
            if(!applicationContext.containsBean(controllerWrapper.getBeanName())){
                iterator.remove();
            }
            Object controllerBean = applicationContext.getBean(controllerWrapper.getBeanName());
            Set<RequestMappingInfo> requestMappingInfos = registry(registryInfo, controllerBean.getClass());
            if(requestMappingInfos.isEmpty()){
                iterator.remove();
            } else {
                for (RequestMappingInfo requestMappingInfo : requestMappingInfos) {
                    LOG.info("插件[{}]注册接口: {}", pluginId, requestMappingInfo.toString());
                }
            }
        }
    }

    @Override
    public RunMode runMode() {
        return RunMode.PLUGIN;
    }


    private Set<RequestMappingInfo> registry(SpringPluginRegistryInfo pluginRegistryInfo, Class<?> aClass)
            throws Exception {
        ConfigurableApplicationContext pluginApplicationContext = pluginRegistryInfo.getPluginSpringApplication()
                .getApplicationContext();
        Object object = pluginApplicationContext.getBean(aClass);

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
        return requestMappingInfos;
    }

    /**
     * 方法上是否存在 @RequestMapping 注解
     * @param method method
     * @return boolean
     */
    private boolean isHaveRequestMapping(Method method){
        return AnnotationUtils.findAnnotation(method, RequestMapping.class) != null;
    }

    private static class ChangeRestPathPostProcessor implements BeanPostProcessor {

        private final static Logger LOG = LoggerFactory.getLogger(ChangeRestPathPostProcessor.class);
        private final static String COMMON_ERROR = "无法统一处理该 Controller 统一请求路径前缀";

        private final SpringPluginRegistryInfo registryInfo;



        private ChangeRestPathPostProcessor(SpringPluginRegistryInfo registryInfo) {
            this.registryInfo = registryInfo;
        }

        @Override
        public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
            Class<?> aClass = bean.getClass();
            Controller controller = AnnotationUtils.findAnnotation(aClass, Controller.class);
            if(controller == null){
                return bean;
            }
            RequestMapping requestMapping = aClass.getAnnotation(RequestMapping.class);
            if(requestMapping != null){
                changePathForClass(beanName, aClass, requestMapping);
            }
            return bean;
        }

        private void changePathForClass(String beanName, Class<?> aClass, RequestMapping requestMapping){
            String pluginId = registryInfo.getPluginWrapper().getPluginId();
            String pathPrefix = CommonUtils.getPluginRestPrefix(registryInfo.getConfiguration(), pluginId);

            if(StringUtils.isNullOrEmpty(pathPrefix)){
                LOG.error("插件 [{}] Controller类 [{}] 未发现 path 配置, {}",
                        pluginId, aClass.getSimpleName(), COMMON_ERROR);
                return;
            }
            Set<String> definePaths = new HashSet<>();
            definePaths.addAll(Arrays.asList(requestMapping.path()));
            definePaths.addAll(Arrays.asList(requestMapping.value()));
            try {
                Map<String, Object> memberValues = ClassUtils.getAnnotationsUpdater(requestMapping);
                if(memberValues == null){
                    LOG.error("插件 [{}] Controller 类 [{}] 无法反射获取注解属性, {}",
                            pluginId, aClass.getSimpleName(), COMMON_ERROR);
                    return;
                }
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
                memberValues.put("value", newPath);

                List<ControllerWrapper> controllerWrappers = this.registryInfo.getRegistryInfo(PROCESS_CONTROLLERS);
                if(controllerWrappers == null){
                    controllerWrappers = new ArrayList<>();
                    this.registryInfo.addRegistryInfo(PROCESS_CONTROLLERS, controllerWrappers);
                }
                ControllerWrapper controllerWrapper = new ControllerWrapper();
                controllerWrapper.setPathPrefix(newPath);
                controllerWrapper.setBeanName(beanName);
                controllerWrappers.add(controllerWrapper);
            } catch (Exception e) {
                LOG.error("插件 [{}] Controller 类[{}] 注册异常. {}", pluginId, aClass.getName(), e.getMessage(), e);
            }
        }
    }

}
