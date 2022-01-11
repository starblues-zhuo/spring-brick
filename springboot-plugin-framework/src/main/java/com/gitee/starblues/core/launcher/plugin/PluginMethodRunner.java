package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.RuntimeMode;
import com.gitee.starblues.core.launcher.MethodRunner;
import com.gitee.starblues.utils.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 插件方法运行器。
 * @author starBlues
 * @version 3.0.0
 */
public class PluginMethodRunner extends MethodRunner {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private static final String PLUGIN_RUN_METHOD_NAME = "run";

    private final PluginInteractive pluginInteractive;

    public PluginMethodRunner(PluginInteractive pluginInteractive) {
        super(pluginInteractive.getPluginDescriptor().getPluginBootstrapClass(), PLUGIN_RUN_METHOD_NAME, new String[]{});
        this.pluginInteractive = pluginInteractive;
    }

    @Override
    protected Class<?> loadRunClass(ClassLoader classLoader) throws Exception {
        try {
            return super.loadRunClass(classLoader);
        } catch (Exception e){
            if(e instanceof ClassNotFoundException){
                String pluginId = pluginInteractive.getPluginDescriptor().getPluginId();
                String error = "插件[" + pluginId + "]没有发现" + "[" + className + "]引导类";
                if(pluginInteractive.getConfiguration().environment() == RuntimeMode.DEV){
                    error = error + ", 请确保已经编译！";
                }
                throw new ClassNotFoundException(error);
            }
            throw e;
        }
    }

    @Override
    protected Object runMethod(Class<?> runClass) throws Exception {
        Method runMethod = ReflectionUtils.findMethod(runClass, runMethodName, Class.class, String[].class);
        if(runMethod == null) {
            throw new NoSuchMethodException(runClass.getName() + "." + runMethodName
                    + "(Class<?> arg0, String[] arg1)");
        }
        Object instance = getInstance(runClass);
        setPluginInteractive(instance);
        runMethod.setAccessible(true);
        try {
            return runMethod.invoke(instance, runClass, this.args);
        } catch (Exception e){
            String error = "Invoke failure: "
                    + ReflectionUtils.methodToString(runClass, runMethodName, runMethod.getParameterTypes())
                    + ". " + e.getMessage();
            logger.error(error, e);
            throw new Exception(error);
        }
    }

    private void setPluginInteractive(Object launchObject) throws Exception {
        if(launchObject == null){
            return;
        }
        ReflectionUtils.setAttribute(launchObject, "setPluginInteractive", pluginInteractive);
    }


}
