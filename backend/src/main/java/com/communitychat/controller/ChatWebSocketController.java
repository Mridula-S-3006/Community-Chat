package com.communitychat.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import com.communitychat.model.entity.Message;
import com.communitychat.service.MessageService;

@Controller
public class ChatWebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private MessageService messageService;

    @MessageMapping("/chat")
    public void handleChatMessage(@Payload Map<String, Object> payload) {
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        Long receiverId = Long.valueOf(payload.get("receiverId").toString());
        String content = payload.get("content").toString();

        try {
            Message message = messageService.sendDirectMessage(senderId, receiverId, content);
            
            // Send to BOTH users so both UIs update
            // Topic pattern: /topic/messages/{userId1}.{userId2}
            String toRecipientTopic = "/topic/messages/" + receiverId + "." + senderId;
            String toSenderTopic = "/topic/messages/" + senderId + "." + receiverId;
            
            messagingTemplate.convertAndSend(toRecipientTopic, payload);
            messagingTemplate.convertAndSend(toSenderTopic, payload);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MessageMapping("/group/{groupId}")
    public void handleGroupMessage(@Payload Map<String, Object> payload) {
        Long senderId = Long.valueOf(payload.get("senderId").toString());
        Long groupId = Long.valueOf(payload.get("groupId").toString());
        String content = payload.get("content").toString();

        try {
            Message message = messageService.sendGroupMessage(senderId, groupId, content);
            messagingTemplate.convertAndSend(
                "/topic/group/" + groupId,
                payload
            );
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}