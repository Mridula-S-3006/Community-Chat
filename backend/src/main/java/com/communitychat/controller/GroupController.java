package com.communitychat.controller;

import com.communitychat.model.entity.Group;
import com.communitychat.service.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/groups")
public class GroupController {

    private final GroupService groupService;

    @Autowired
    public GroupController(GroupService groupService) {
        this.groupService = groupService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createGroup(@RequestParam Long ownerId,
                                         @RequestParam String name,
                                         @RequestParam(required = false) Set<Long> memberIds) {
        try {
            if (memberIds == null) memberIds = Set.of();
            Group group = groupService.createGroup(ownerId, name, memberIds);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getUserGroups(@PathVariable Long userId) {
        try {
            List<Group> groups = groupService.getUserGroups(userId);
            return ResponseEntity.ok(groups);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{groupId}/add")
    public ResponseEntity<?> addMember(@PathVariable Long groupId,
                                       @RequestParam Long userId) {
        try {
            Group group = groupService.addMember(groupId, userId);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/{groupId}/remove")
    public ResponseEntity<?> removeMember(@PathVariable Long groupId,
                                          @RequestParam Long userId) {
        try {
            Group group = groupService.removeMember(groupId, userId);
            return ResponseEntity.ok(group);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}