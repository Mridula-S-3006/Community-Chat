package com.communitychat.controller;

import com.communitychat.model.entity.Message;
import com.communitychat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
public class MessageController {

    private final MessageService messageService;

    @Autowired
    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/direct")
    public ResponseEntity<?> sendDirectMessage(@RequestParam Long senderId,
                                               @RequestParam Long receiverId,
                                               @RequestParam String content) {
        try {
            Message message = messageService.sendDirectMessage(senderId, receiverId, content);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/group")
    public ResponseEntity<?> sendGroupMessage(@RequestParam Long senderId,
                                              @RequestParam Long groupId,
                                              @RequestParam String content) {
        try {
            Message message = messageService.sendGroupMessage(senderId, groupId, content);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/direct")
    public ResponseEntity<?> getDirectMessages(@RequestParam Long user1Id,
                                               @RequestParam Long user2Id) {
        try {
            List<Message> messages = messageService.getDirectMessages(user1Id, user2Id);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/group")
    public ResponseEntity<?> getGroupMessages(@RequestParam Long groupId) {
        try {
            List<Message> messages = messageService.getGroupMessages(groupId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
