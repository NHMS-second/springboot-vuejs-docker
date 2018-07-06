package com.github.sumuzhou;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.google.common.base.Throwables;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {

	private static final Logger LOG = LoggerFactory.getLogger(WebSocketConfig.class);

	@Bean
    public GameMessageHandler gameMessageHandler() {
        return new GameMessageHandler();
    }

	@Override
	public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
		registry.addHandler(gameMessageHandler(), "/game-websocket-endpoint").setAllowedOrigins("*");
	}

	public class GameMessageHandler extends TextWebSocketHandler {

		private Map<String, WebSocketSession> clients = new HashMap<>();

		@Override
		public void afterConnectionEstablished(WebSocketSession session) throws Exception {
			LOG.info("sess{}建立了连接", session.getId());
			clients.putIfAbsent(session.getId(), session);
			super.afterConnectionEstablished(session);
		}

		@Override
		public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
			LOG.info("sess{}关闭了连接", session.getId());
			clients.remove(session.getId());
			super.afterConnectionClosed(session, status);
		}

		@Override
	    protected void handleTextMessage(WebSocketSession session, TextMessage textMessage) throws Exception {
			LOG.info("向sess{}发送消息{}", session.getId(), textMessage.getPayload());
			session.sendMessage(textMessage);
	    }

		public void sendBroadcastMessage(String message) {
			for (WebSocketSession sess : clients.values()) {
				try {
					handleTextMessage(sess, new TextMessage(message));
				} catch (Exception e) {
					LOG.error("发送WS广播消息时出错, ID为{}, 错误原因{}", sess.getId(), Throwables.getStackTraceAsString(e));
					continue;
				}
			}
		}

	}

}
