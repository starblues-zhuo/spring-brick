package com.basic.example.plugin3;

import java.util.concurrent.atomic.AtomicInteger;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint(value = "/test/has_path_param/{testName}")
public class WebSocket2 {

    private static final Logger log = LoggerFactory.getLogger(WebSocket2.class);

    private static AtomicInteger onlineCount = new AtomicInteger(0);

    @OnOpen
    public void onOpen(Session session, @PathParam("testName") String testName) {
        onlineCount.incrementAndGet(); // 在线数加1
        log.info("有新连接加入：{}，当前在线人数为：{}，用户名：{}", session.getId(), onlineCount.get(), testName);
    }

    @OnClose
    public void onClose(Session session) {
        onlineCount.decrementAndGet(); // 在线数减1
        log.info("有一连接关闭：{}，当前在线人数为：{}", session.getId(), onlineCount.get());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("服务端收到客户端[{}]的消息:{}", session.getId(), message);
        this.sendMessage("Hello, " + message, session);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    private void sendMessage(String message, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }
}
