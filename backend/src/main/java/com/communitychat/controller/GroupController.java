package com.communitychat.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.communitychat.model.entity.Group;
import com.communitychat.model.entity.Message;
import com.communitychat.repository.UserRepository;
import com.communitychat.service.GroupService;
import com.communitychat.service.MessageService;

@RestController
@RequestMapping("/api/groups")
@CrossOrigin(origins = "*")
public class GroupController {

    private final GroupService groupService;
    private final MessageService messageService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public GroupController(GroupService groupService, MessageService messageService) {
        this.groupService = groupService;
        this.messageService = messageService;
    }

    @GetMapping
    public ResponseEntity<?> getAllGroups() {
        try {
            Long userId = userRepository.findAll().get(0).getId();
            List<Group> groups = groupService.getUserGroups(userId);
            
            List<Map<String, Object>> groupList = groups.stream().map(g -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", g.getId());
                map.put("name", g.getName());
                map.put("memberCount", g.getMembers().size());
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(groupList);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createGroup(@RequestBody Map<String, Object> payload) {
        try {
            String name = payload.get("name").toString();
            Long ownerId = Long.valueOf(payload.get("ownerId").toString());
            
            List<Integer> memberIds = (List<Integer>) payload.getOrDefault("memberIds", List.of());
            Set<Long> memberIdSet = memberIds.stream()
                .map(Integer::longValue)
                .collect(Collectors.toSet());
            
            Group group = groupService.createGroup(ownerId, name, memberIdSet);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", group.getId());
            response.put("name", group.getName());
            response.put("memberCount", group.getMembers().size());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserGroups(@PathVariable Long userId) {
        try {
            List<Group> groups = groupService.getUserGroups(userId);
            
            List<Map<String, Object>> groupList = groups.stream().map(g -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", g.getId());
                map.put("name", g.getName());
                map.put("memberCount", g.getMembers().size());
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(groupList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getGroup(@PathVariable Long id) {
        try {
            Group group = groupService.getGroupById(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("id", group.getId());
            response.put("name", group.getName());
            response.put("ownerId", group.getOwner().getId());
            response.put("memberCount", group.getMembers().size());
            response.put("events", List.of());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{id}/messages")
    public ResponseEntity<?> getGroupMessages(@PathVariable Long id) {
        try {
            List<Message> messages = messageService.getGroupMessages(id);
            
            List<Map<String, Object>> messageList = messages.stream().map(m -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", m.getId());
                map.put("content", m.getContent());
                map.put("senderId", m.getSender().getId());
                map.put("senderUsername", m.getSender().getUsername());
                map.put("sentAt", m.getSentAt().toString());
                return map;
            }).collect(Collectors.toList());
            
            return ResponseEntity.ok(messageList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{groupId}/add")
    public ResponseEntity<?> addMember(@PathVariable Long groupId, @RequestParam Long userId) {
        try {
            Group group = groupService.addMember(groupId, userId);
            return ResponseEntity.ok(Map.of("success", true, "memberCount", group.getMembers().size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/{groupId}/remove")
    public ResponseEntity<?> removeMember(@PathVariable Long groupId, @RequestParam Long userId) {
        try {
            Group group = groupService.removeMember(groupId, userId);
            return ResponseEntity.ok(Map.of("success", true, "memberCount", group.getMembers().size()));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}