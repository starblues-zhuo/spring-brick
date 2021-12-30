//package com.gitee.starblues.extension;
//
//
//import com.gitee.starblues.factory.process.post.bean.model.ControllerWrapper;
//
//import java.util.List;
//
///**
// * 可扩展的 controller 处理者
// *
// * @author starBlues
// * @version 2.4.0
// */
//public interface PluginControllerProcessorExtend {
//
//    /**
//     * 初始化
//     */
//    void initialize();
//
//    /**
//     * 注册
//     * @param pluginId 插件id
//     * @param controllerWrappers controller 类集合
//     * @throws Exception 异常
//     */
//    void registry(String pluginId, List<ControllerWrapper> controllerWrappers) throws Exception;
//
//    /**
//     * 注册
//     * @param pluginId 插件id
//     * @param controllerWrappers controller 类集合
//     * @throws Exception 异常
//     */
//    void unRegistry(String pluginId, List<ControllerWrapper> controllerWrappers) throws Exception;
//}
