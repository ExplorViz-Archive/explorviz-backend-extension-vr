package net.explorviz.extension.vr;

import javax.enterprise.context.ApplicationScoped;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ServerEndpoint("/v2/vr")
@ApplicationScoped
public class VRSocket {
    
    private final Logger LOGGER = LoggerFactory.getLogger(VRSocket.class);
    
    @OnOpen
    public void onOpen(Session session) {
        LOGGER.debug("opened websocket");
    }

    @OnClose
    public void onClose(Session session) {
        LOGGER.debug("closed websocket");
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        LOGGER.error("websocket error: {}", throwable.getMessage());
    }

    @OnMessage
    public void onMessage(String message) {
        LOGGER.debug("received message: {}", message);
    }
}
