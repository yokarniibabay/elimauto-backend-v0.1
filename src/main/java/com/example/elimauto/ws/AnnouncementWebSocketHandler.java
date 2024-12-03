package com.example.elimauto.ws;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class AnnouncementWebSocketHandler extends TextWebSocketHandler {
    private final SimpMessagingTemplate messagingTemplate;

    public AnnouncementWebSocketHandler(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // Логика обработки сообщений от клиентов (например, изменения объявления)
        // Здесь можно получить ID объявления и отправить уведомление всем
        String announcementId = message.getPayload();  // Получаем ID объявления
        messagingTemplate.convertAndSend("/topic/announcement/" + announcementId, "Объявление изменено");
    }
}