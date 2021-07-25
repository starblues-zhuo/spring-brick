package com.gitee.starblues.factory.process.post.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.classs.group.WebSocketGroup;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import javax.servlet.ServletContext;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;

import com.gitee.starblues.utils.ClassUtils;
import org.pf4j.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 插件中websocket处理者
 *
 * @author sousouki
 * @version 2.4.2
 */
public class PluginWebSocketProcessor implements PluginPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(PluginWebSocketProcessor.class);

    public static final String KEY = "PluginWsConfigProcessor";
    private static final String WEB_SOCKET_PATH = "WEB_SOCKET_PATH";

    private final ApplicationContext applicationContext;

    public PluginWebSocketProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        ServerContainer serverContainer = getServerContainer();
        if (serverContainer == null) return;

        for (PluginRegistryInfo pluginRegistryInfo : pluginRegistryInfos) {
            Map<String, String> webSocketPathMap = new HashMap<>();
            List<Class<?>> websocketClasses = pluginRegistryInfo.getGroupClasses(WebSocketGroup.GROUP_ID);
            String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
            websocketClasses.forEach(websocketClass -> {
                ServerEndpoint serverEndpoint = websocketClass.getDeclaredAnnotation(ServerEndpoint.class);
                if (serverEndpoint == null) {
                    log.warn("WebSocket class {} doesn't has annotation {}", websocketClass.getName(), ServerEndpoint.class.getName());
                    return;
                }
                String sourcePath = serverEndpoint.value();
                if (StringUtils.isNullOrEmpty(sourcePath)) {
                    return;
                }
                String processPath = sourcePath;
                if(!processPath.startsWith("/")){
                    processPath = "/".concat(processPath);
                }
                UriTemplate uriTemplate;

                try {
                    uriTemplate = new UriTemplate(processPath);
                } catch (DeploymentException e) {
                    log.error("Websocket path validate failed.", e);
                    return;
                }
                String newWebsocketPath = "/".concat(pluginId).concat(processPath);
                String newWebsocketTemplatePath = "/".concat(pluginId).concat(uriTemplate.getPath());
                Map<String, Object> annotationsUpdater = null;
                try {
                    annotationsUpdater = ClassUtils.getAnnotationsUpdater(serverEndpoint);
                } catch (Exception e) {
                    log.error("Process and update websocket path '{}' annotation exception.", sourcePath, e);
                    return;
                }
                try {
                    annotationsUpdater.put("value", newWebsocketPath);
                    serverContainer.addEndpoint(websocketClass);
                    webSocketPathMap.put(newWebsocketPath, newWebsocketTemplatePath);
                    log.info("Succeed to create websocket service for path {}", newWebsocketPath);
                } catch (Exception e) {
                    log.error("Create websocket service for websocket class " + websocketClass.getName() + " failed.", e);
                } finally {
                    annotationsUpdater.put("value", sourcePath);
                }
            });
            pluginRegistryInfo.addExtension(WEB_SOCKET_PATH, webSocketPathMap);
        }
    }

    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        ServerContainer serverContainer = getServerContainer();
        if (serverContainer == null) {
            log.warn("Not found ServerContainer, So websocket can't used!");
            return;
        }
        Map<String, Object> configExactMatchMap = ClassUtils.getReflectionField(serverContainer, "configExactMatchMap");
        Map<Integer, ConcurrentSkipListMap<String, Object>> configTemplateMatchMap =
                ClassUtils.getReflectionField(serverContainer, "configTemplateMatchMap");
        Map<String, Object> endpointSessionMap = ClassUtils.getReflectionField(serverContainer, "endpointSessionMap");
        Map<Session, Session> sessions = ClassUtils.getReflectionField(serverContainer, "sessions");
        pluginRegistryInfos.forEach(pluginRegistryInfo -> {
            Map<String, String> webSocketPathMap = pluginRegistryInfo.getExtension(WEB_SOCKET_PATH);
            webSocketPathMap.forEach((webSocketPath,newWebsocketTemplatePath)->{
                configExactMatchMap.remove(webSocketPath);
                log.debug("Removed websocket config for path {}", webSocketPath);
                configTemplateMatchMap.forEach((key, value) -> {
                    value.remove(newWebsocketTemplatePath);
                });
                endpointSessionMap.remove(webSocketPath);
                log.debug("Removed websocket session for path {}", webSocketPath);

                for (Map.Entry<Session, Session> entry : sessions.entrySet()) {
                    Session session = entry.getKey();
                    try {
                        if(closeSession(session, webSocketPath)){
                            sessions.remove(session);
                            log.debug("Removed websocket session {} for path {}", session.getId(), webSocketPath);
                        }
                    } catch (Exception e) {
                        log.debug("Close websocket session {} for path {} failure", session.getId(), webSocketPath, e);
                    }
                }
                log.info("Remove websocket for path {} success.", webSocketPath);
            });
        });
    }

    /**
     * 得到 Tomcat ServerContainer
     * @return ServerContainer
     */
    private ServerContainer getServerContainer() {
        try {
            applicationContext.getBean(ServerEndpointExporter.class);
        } catch (BeansException e) {
            log.debug("The required bean of {} not found, if you want to use plugin websocket, please create it.", ServerEndpointExporter.class.getName());
            return null;
        }
        if (!(applicationContext instanceof WebApplicationContext)) {
            return null;
        }
        WebApplicationContext webApplicationContext = (WebApplicationContext) applicationContext;
        ServletContext servletContext = webApplicationContext.getServletContext();
        if (servletContext == null) {
            log.warn("Servlet context is null.");
            return null;
        }
        Object obj = servletContext.getAttribute("javax.websocket.server.ServerContainer");
        if (!(obj instanceof ServerContainer)) {
            return null;
        }
        return (ServerContainer) obj;
    }

    /**
     * 关闭session
     * @param session session
     * @param websocketPath websocketPath 路径
     * @return 如果需要关闭并且关闭成功, 则返回true。 否则返回false
     * @throws Exception 关闭异常
     */
    private boolean closeSession(Session session, String websocketPath) throws Exception{
        EndpointConfig endpointConfig = ClassUtils.getReflectionField(session, "endpointConfig");
        ServerEndpointConfig perEndpointConfig = ClassUtils.getReflectionField(endpointConfig, "perEndpointConfig");
        String path = ClassUtils.getReflectionField(perEndpointConfig, "path");
        if (path.equals(websocketPath)) {
            session.close();
            log.info("Closed websocket session {} for path {}", session.getId(), websocketPath);
            return true;
        }
        return false;
    }

    /**
     * websocket路径解析类，主要用于处理参数
     */
    private static class UriTemplate {

        private final Map<String, Integer> paramMap = new ConcurrentHashMap<>();
        private final String path;

        private UriTemplate(String path) throws DeploymentException {
            if (path == null || path.length() == 0 || !path.startsWith("/") || path.contains("/../") || path.contains("/./") || path.contains("//")) {
                throw new DeploymentException(String.format("The path [%s] is not valid.", path));
            }
            StringBuilder normalized = new StringBuilder(path.length());
            Set<String> paramNames = new HashSet<>();

            // Include empty segments.
            String[] segments = path.split("/", -1);
            int paramCount = 0;

            for (int i = 0; i < segments.length; i++) {
                String segment = segments[i];
                if (segment.length() == 0) {
                    if (i == 0 || (i == segments.length - 1 && paramCount == 0)) {
                        // Ignore the first empty segment as the path must always
                        // start with '/'
                        // Ending with a '/' is also OK for instances used for
                        // matches but not for parameterised templates.
                        continue;
                    } else {
                        // As per EG discussion, all other empty segments are
                        // invalid
                        throw new DeploymentException(String.format("The path [%s] contains one or more empty segments which is not permitted", path));
                    }
                }
                normalized.append('/');
                if (segment.startsWith("{") && segment.endsWith("}")) {
                    segment = segment.substring(1, segment.length() - 1);
                    normalized.append('{');
                    normalized.append(paramCount++);
                    normalized.append('}');
                    if (!paramNames.add(segment)) {
                        throw new DeploymentException(String.format("The parameter [%s] appears more than once in the path which is not permitted", segment));
                    }
                    paramMap.put(segment, paramCount - 1);
                } else {
                    if (segment.contains("{") || segment.contains("}")) {
                        throw new DeploymentException(String.format("The segment [%s] is not valid in the provided path [%s]", segment, path));
                    }
                    normalized.append(segment);
                }
            }
            this.path = normalized.toString();
        }

        public String getPath() {
            return path;
        }

        public Map<String, Integer> getParamMap() {
            return paramMap;
        }
    }

}
