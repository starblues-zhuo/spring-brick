package com.gitee.starblues.core.launcher.plugin;

import com.gitee.starblues.core.launcher.MethodRunner;
import com.gitee.starblues.utils.ReflectionUtils;

import java.lang.reflect.Method;

/**
 * @author starBlues
 * @version 1.0
 */
public class PluginMethodRunner extends MethodRunner {

    private static final String PLUGIN_RUN_METHOD_NAME = "run";

    private final PluginInteractive pluginInteractive;

    public PluginMethodRunner(PluginInteractive pluginInteractive) {
        super(pluginInteractive.getPluginDescriptor().getPluginClass(), PLUGIN_RUN_METHOD_NAME, new String[]{});
        this.pluginInteractive = pluginInteractive;
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
                    + ReflectionUtils.methodToString(runClass, runMethodName, runMethod.getParameterTypes());
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
