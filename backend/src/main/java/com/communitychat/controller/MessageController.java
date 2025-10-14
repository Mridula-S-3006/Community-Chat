package com.communitychat.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.communitychat.model.entity.Message;
import com.communitychat.service.MessageService;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    @GetMapping("/messages/{otherUserId}")
    public ResponseEntity<List<Message>> getDirectMessages(
            @PathVariable Long otherUserId,
            @RequestParam Long currentUserId) {
        try {
            List<Message> messages = messageService.getDirectMessages(currentUserId, otherUserId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/group/{groupId}/messages")
    public ResponseEntity<List<Message>> getGroupMessages(@PathVariable Long groupId) {
        try {
            List<Message> messages = messageService.getGroupMessages(groupId);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}