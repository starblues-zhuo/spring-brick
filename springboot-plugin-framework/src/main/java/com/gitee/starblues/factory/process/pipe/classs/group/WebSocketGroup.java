package com.gitee.starblues.factory.process.pipe.classs.group;

import com.gitee.starblues.factory.process.pipe.classs.PluginClassGroup;
import com.gitee.starblues.realize.BasePlugin;
import com.gitee.starblues.utils.AnnotationsUtils;
import javax.websocket.server.ServerEndpoint;

/**
 * 分组存在注解: @ServerEndpoint
 *
 * @author sousouki
 */
public class WebSocketGroup implements PluginClassGroup {

    public static final String GROUP_ID = "websocket";

    @Override
    public String groupId() {
        return GROUP_ID;
    }

    @Override
    public void initialize(BasePlugin basePlugin) {

    }

    @Override
    public boolean filter(Class<?> aClass) {
        return AnnotationsUtils.haveAnnotations(aClass, false, ServerEndpoint.class);
    }
}
