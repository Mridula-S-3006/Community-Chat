package com.communitychat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.communitychat.model.entity.Message;
import com.communitychat.service.MessageService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/conversations")
    public ResponseEntity<?> getConversations() {
        return ResponseEntity.ok(List.of());
    }

    // REMOVED - this conflicts with MessageController
    // Use MessageController's /api/chat/messages/{otherUserId} instead

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody Map<String, Object> payload) {
        try {
            Long senderId = Long.valueOf(payload.get("senderId").toString());
            Long receiverId = Long.valueOf(payload.get("receiverId").toString());
            String content = payload.get("content").toString();
            
            Message message = messageService.sendDirectMessage(senderId, receiverId, content);
            
            Map<String, Object> response = new HashMap<>();
            response.put("content", message.getContent());
            response.put("sentAt", message.getSentAt().toString());
            
            Map<String, Object> senderMap = new HashMap<>();
            senderMap.put("id", message.getSender().getId());
            senderMap.put("username", message.getSender().getUsername());
            response.put("sender", senderMap);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Error: " + e.getMessage());
        }
    }
}