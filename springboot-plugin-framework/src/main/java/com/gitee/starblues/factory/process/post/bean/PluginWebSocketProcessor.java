package com.gitee.starblues.factory.process.post.bean;

import com.gitee.starblues.factory.PluginRegistryInfo;
import com.gitee.starblues.factory.process.pipe.classs.group.WebSocketGroup;
import com.gitee.starblues.factory.process.post.PluginPostProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import javax.servlet.ServletContext;
import javax.websocket.DeploymentException;
import javax.websocket.EndpointConfig;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerContainer;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.server.ServerEndpointConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.asm.AnnotationVisitor;
import org.springframework.asm.ClassReader;
import org.springframework.asm.ClassVisitor;
import org.springframework.asm.ClassWriter;
import org.springframework.asm.MethodVisitor;
import org.springframework.asm.Opcodes;
import org.springframework.asm.Type;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * 插件中websocket处理者
 *
 * @author sousouki
 */
public class PluginWebSocketProcessor implements PluginPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(PluginWebSocketProcessor.class);

    public static final String KEY = "PluginWsConfigProcessor";

    private static final int ASM_API_VERSION = Opcodes.ASM7;

    private final ApplicationContext applicationContext;

    public PluginWebSocketProcessor(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public void initialize() throws Exception {

    }

    @Override
    public void registry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        try {
            applicationContext.getBean(ServerEndpointExporter.class);
        } catch (BeansException e) {
            log.debug("The required bean of {} not found, if you want to use plugin websocket, please create it.", ServerEndpointExporter.class.getName());
            return;
        }
        pluginRegistryInfos.forEach(pluginRegistryInfo -> {
            String pluginId = pluginRegistryInfo.getPluginWrapper().getPluginId();
            if (applicationContext instanceof WebApplicationContext) {
                WebApplicationContext webApplicationContext = (WebApplicationContext) applicationContext;
                ServletContext servletContext = webApplicationContext.getServletContext();
                if (servletContext == null) {
                    log.warn("Servlet context is null.");
                    return;
                }
                Object obj = servletContext.getAttribute("javax.websocket.server.ServerContainer");
                if (obj instanceof ServerContainer) {
                    ServerContainer serverContainer = (ServerContainer) obj;
                    List<Class<?>> websocketClasses = pluginRegistryInfo.getGroupClasses(WebSocketGroup.GROUP_ID);
                    websocketClasses.forEach(websocketClass -> {
                        ServerEndpoint serverEndpoint = websocketClass.getDeclaredAnnotation(ServerEndpoint.class);
                        if (serverEndpoint == null) {
                            log.warn("WebSocket class {} doesn't has annotation {}", websocketClass.getName(), ServerEndpoint.class.getName());
                            return;
                        }
                        String websocketPath = serverEndpoint.value();
                        UriTemplate uriTemplate;
                        try {
                            uriTemplate = new UriTemplate(websocketPath);
                        } catch (DeploymentException e) {
                            log.error("Websocket path validate failed.", e);
                            return;
                        }
                        String websocketTemplatePath = uriTemplate.getPath();
                        Map<String, Integer> pathParam = uriTemplate.getParamMap();
                        String newWebsocketPath = "/".concat(pluginId).concat(websocketTemplatePath);
                        String pluginPath = pluginRegistryInfo.getPluginWrapper().getPluginPath().toString();
                        Class<?> proxyServerEndpoint = createProxyClass(pluginRegistryInfo, pluginPath, websocketClass, newWebsocketPath, pathParam);
                        if (proxyServerEndpoint == null) {
                            log.warn("Proxy class for websocket class {} is null.", websocketClass.getName());
                            return;
                        }
                        try {
                            serverContainer.addEndpoint(proxyServerEndpoint);
                            pluginRegistryInfo.addWebsocketPath(newWebsocketPath);
                            log.info("Succeed to create websocket service for path {}", newWebsocketPath);
                        } catch (DeploymentException e) {
                            log.error("Create websocket service for websocket class " + websocketClass.getName() + " failed.", e);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void unRegistry(List<PluginRegistryInfo> pluginRegistryInfos) throws Exception {
        pluginRegistryInfos.forEach(pluginRegistryInfo -> {
            List<String> websocketPaths = pluginRegistryInfo.getWebsocketPaths();
            websocketPaths.forEach(websocketPath -> {
                try {
                    if (applicationContext instanceof WebApplicationContext) {
                        WebApplicationContext webApplicationContext = (WebApplicationContext) applicationContext;
                        ServletContext servletContext = webApplicationContext.getServletContext();
                        if (servletContext == null) {
                            log.warn("Servlet context is null.");
                            return;
                        }
                        Object obj = servletContext.getAttribute("javax.websocket.server.ServerContainer");
                        if (obj instanceof ServerContainer) {
                            ServerContainer serverContainer = (ServerContainer) obj;
                            Map<String, Object> configExactMatchMap = (Map<String, Object>) reflectFieldValue(serverContainer, "configExactMatchMap");
                            configExactMatchMap.remove(websocketPath);
                            log.debug("Removed websocket config for path {}", websocketPath);

                            Map<Integer, ConcurrentSkipListMap<String, Object>> configTemplateMatchMap = (Map<Integer, ConcurrentSkipListMap<String, Object>>) reflectFieldValue(serverContainer, "configTemplateMatchMap");
                            configTemplateMatchMap.forEach((key, value) -> {
                                value.remove(websocketPath);
                            });

                            Map<String, Object> endpointSessionMap = (Map<String, Object>) reflectParentFieldValue(serverContainer, "endpointSessionMap");
                            endpointSessionMap.remove(websocketPath);
                            log.debug("Removed websocket session for path {}", websocketPath);

                            Map<Session, Session> sessions = (Map<Session, Session>) reflectParentFieldValue(serverContainer, "sessions");
                            for (Map.Entry<Session, Session> entry : sessions.entrySet()) {
                                Session session = entry.getKey();
                                EndpointConfig endpointConfig = (EndpointConfig) reflectFieldValue(session, "endpointConfig");
                                ServerEndpointConfig perEndpointConfig = (ServerEndpointConfig) reflectFieldValue(endpointConfig, "perEndpointConfig");
                                String path = (String) reflectFieldValue(perEndpointConfig, "path");
                                if (path.equals(websocketPath)) {
                                    session.close();
                                    log.debug("Closed websocket session {} for path {}", session.getId(), websocketPath);
                                    sessions.remove(session);
                                    log.debug("Removed websocket session {} for path {}", session.getId(), websocketPath);
                                }
                            }
                            log.info("Remove websocket for path {} success.", websocketPath);
                        }
                    }
                } catch (IllegalAccessException | NoSuchFieldException | IOException e) {
                    log.error("Remove websocket failed for path " + websocketPath, e);
                }
            });
        });
    }

    private Object reflectFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        return field.get(obj);
    }

    private Object reflectParentFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getSuperclass().getDeclaredField(fieldName);
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        return field.get(obj);
    }

    private Class<?> createProxyClass(PluginRegistryInfo pluginRegistryInfo, String pluginPath, Class<?> websocketClass, String newWebsocketPath, Map<String, Integer> pathParam) {
        String simpleName = websocketClass.getSimpleName();
        String className = websocketClass.getName();
        String basePackage = className.substring(0, className.lastIndexOf(simpleName) - 1);
        try (JarFile jarFile = new JarFile(pluginPath)) {
            Enumeration<JarEntry> jarEntries = jarFile.entries();
            while (jarEntries.hasMoreElements()) {
                JarEntry entry = jarEntries.nextElement();
                String jarEntryName = entry.getName();
                if (jarEntryName.endsWith(simpleName.concat(".class")) && jarEntryName.replaceAll("/", ".").startsWith(basePackage)) {
                    InputStream inputStream = jarFile.getInputStream(entry);
                    if (inputStream == null) {
                        log.warn("Class stream for {} is null.", websocketClass.getName());
                        return null;
                    }
                    Class<?> proxyClass = createProxyClass(pluginRegistryInfo, inputStream, websocketClass, newWebsocketPath, pathParam);
                    log.debug("Created proxy class {} for websocket class {}", proxyClass.getName(), className);
                    return proxyClass;
                }
            }
        } catch (IOException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            log.error("Create proxy class for websocket class " + className + "error", e);
            return null;
        }
        return null;
    }

    private Class<?> createProxyClass(PluginRegistryInfo pluginRegistryInfo, InputStream inputStream, Class<?> websocketClass, String newWebsocketPath, Map<String, Integer> pathParam) throws IllegalAccessException, NoSuchMethodException, InvocationTargetException, IOException {
        ClassReader cr = new ClassReader(inputStream);
        ClassWriter cw = new ClassWriter(cr, ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
        String className = websocketClass.getName();
        String proxyClassName = className.concat("$PluginProxyServerEndpoint$".concat(String.valueOf(System.currentTimeMillis())));
        ClassVisitor cv = new ServerEndpointProxyClassVisitor(ASM_API_VERSION, cw, websocketClass, proxyClassName, newWebsocketPath, pathParam);
        cr.accept(cv, 0);
        byte[] classData = cw.toByteArray();
        return defineClass(pluginRegistryInfo, proxyClassName, classData);
    }

    private Class<?> defineClass(PluginRegistryInfo pluginRegistryInfo, String proxyClassName, byte[] classData) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        ClassLoader classLoader = pluginRegistryInfo.getPluginClassLoader();
        Method defineClassMethod = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
        if (!defineClassMethod.isAccessible()) {
            defineClassMethod.setAccessible(true);
        }
        return (Class<?>) defineClassMethod.invoke(classLoader, proxyClassName, classData, 0, classData.length);
    }

    /**
     * 修改类名及类注解的值
     */
    private static class ServerEndpointProxyClassVisitor extends ClassVisitor {

        private final String proxyClassName;
        private final String proxyClassInternalName;
        private final String classInternalName;
        private final String newPath;
        private final Class<?> websocketClass;
        private final Map<String, Integer> pathParam;

        private ServerEndpointProxyClassVisitor(int api, ClassVisitor classVisitor, Class<?> websocketClass, String proxyClassName, String newPath, Map<String, Integer> pathParam) {
            super(api, classVisitor);
            String classInternalName = Type.getInternalName(websocketClass);
            this.proxyClassName = proxyClassName;
            this.proxyClassInternalName = proxyClassName.replaceAll("\\.", "/");
            this.classInternalName = classInternalName;
            this.newPath = newPath;
            this.websocketClass = websocketClass;
            this.pathParam = pathParam;
        }

        @Override
        public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
            super.visit(version, access, proxyClassInternalName, signature, superName, interfaces);
            log.debug("Changed class name from {} to {}", classInternalName, proxyClassInternalName);
        }
        @Override
        public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
            AnnotationVisitor superAnnotationVisitor = super.visitAnnotation(descriptor, visible);
            if (descriptor.equals(Type.getDescriptor(ServerEndpoint.class))) {
                return new AnnotationVisitor(ASM_API_VERSION, superAnnotationVisitor) {
                    @Override
                    public void visit(String name, Object value) {
                        if ("value".equals(name)) {
                            value = newPath;
                            log.debug("Changed websocket path from {} to {} in for annotation {}", value, newPath, ServerEndpoint.class.getName());
                        }
                        super.visit(name, value);
                    }
                };
            }
            return superAnnotationVisitor;
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor superMethodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            return new ServerEndpointProxyMethodVisitor(ASM_API_VERSION, superMethodVisitor, websocketClass, proxyClassName, pathParam);
        }

    }

    /**
     * 替换成员变量及方法所属
     */
    private static class ServerEndpointProxyMethodVisitor extends MethodVisitor {

        private final String classInternalName;
        private final String proxyClassInternalName;
        private final Map<String, Integer> pathParam;

        private ServerEndpointProxyMethodVisitor(int api, MethodVisitor methodVisitor, Class<?> websocketClass, String proxyClassName, Map<String, Integer> pathParam) {
            super(api, methodVisitor);
            String classInternalName = Type.getInternalName(websocketClass);
            this.proxyClassInternalName = proxyClassName.replaceAll("\\.", "/");
            this.classInternalName = classInternalName;
            this.pathParam = pathParam;
        }

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
            // 替换成员变量所属
            if (owner.equals(classInternalName)) {
                super.visitFieldInsn(opcode, proxyClassInternalName, name, descriptor);
                log.debug("Changed owner from {} to {} for field {}", classInternalName, proxyClassInternalName, name);
            } else {
                super.visitFieldInsn(opcode, owner, name, descriptor);
            }
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
            // 替换方法所属
            if (owner.equals(classInternalName)) {
                super.visitMethodInsn(opcode, proxyClassInternalName, name, descriptor, isInterface);
                log.debug("Changed owner from {} to {} in method {}", classInternalName, proxyClassInternalName, name);
            } else {
                super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
            }
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
            AnnotationVisitor superAnnotationVisitor = super.visitParameterAnnotation(parameter, descriptor, visible);
            // 替换@PathParam注解中value的值
            if (descriptor.equals(Type.getDescriptor(PathParam.class))) {
                return new ServerEndpointProxyParameterAnnotationVisitor(ASM_API_VERSION, superAnnotationVisitor, pathParam);
            }
            return super.visitParameterAnnotation(parameter, descriptor, visible);
        }

    }

    /**
     * 将@PathParam("name")替换为@PathParam("0")，使其与uri对应
     */
    private static class ServerEndpointProxyParameterAnnotationVisitor extends AnnotationVisitor {

        private final Map<String, Integer> pathParam;

        private ServerEndpointProxyParameterAnnotationVisitor(int api, AnnotationVisitor annotationVisitor, Map<String, Integer> pathParam) {
            super(api, annotationVisitor);
            this.pathParam = pathParam;
        }

        @Override
        public void visit(String name, Object value) {
            if ("value".equals(name)) {
                Integer index = pathParam.get(String.valueOf(value));
                if (index != null) {
                    value = String.valueOf(index);
                    log.debug("Changed path parameter {} to {}", value, index);
                }
            }
            super.visit(name, value);
        }
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
