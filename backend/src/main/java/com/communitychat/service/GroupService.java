package com.communitychat.service;

import com.communitychat.model.entity.Group;
import com.communitychat.model.entity.GroupMember;
import com.communitychat.model.entity.User;
import com.communitychat.repository.GroupRepository;
import com.communitychat.repository.GroupMemberRepository;
import com.communitychat.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Autowired
    public GroupService(GroupRepository groupRepository, GroupMemberRepository groupMemberRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
    }

    public Group createGroup(Long ownerId, String name, Set<Long> memberIds) throws Exception {
        Optional<User> owner = userRepository.findById(ownerId);
        if (!owner.isPresent()) {
            throw new Exception("Owner not found");
        }

        if (groupRepository.findByName(name).isPresent()) {
            throw new Exception("Group name already exists");
        }

        Group group = new Group();
        group.setName(name);
        group.setOwner(owner.get());
        Group savedGroup = groupRepository.save(group);

        GroupMember ownerMember = new GroupMember();
        ownerMember.setGroup(savedGroup);
        ownerMember.setUser(owner.get());
        groupMemberRepository.save(ownerMember);

        for (Long memberId : memberIds) {
            Optional<User> member = userRepository.findById(memberId);
            if (member.isPresent() && !member.get().getId().equals(ownerId)) {
                GroupMember gm = new GroupMember();
                gm.setGroup(savedGroup);
                gm.setUser(member.get());
                groupMemberRepository.save(gm);
            }
        }

        return savedGroup;
    }

    public List<Group> getUserGroups(Long userId) throws Exception {
        Optional<User> user = userRepository.findById(userId);
        if (!user.isPresent()) {
            throw new Exception("User not found");
        }

        List<GroupMember> memberships = groupMemberRepository.findByUser(user.get());
        return memberships.stream().map(GroupMember::getGroup).collect(Collectors.toList());
    }

    public Group addMember(Long groupId, Long userId) throws Exception {
        Optional<Group> group = groupRepository.findById(groupId);
        Optional<User> user = userRepository.findById(userId);
        if (!group.isPresent() || !user.isPresent()) {
            throw new Exception("Group or user not found");
        }

        if (groupMemberRepository.findByGroupAndUser(group.get(), user.get()).isPresent()) {
            throw new Exception("User already in group");
        }

        GroupMember gm = new GroupMember();
        gm.setGroup(group.get());
        gm.setUser(user.get());
        groupMemberRepository.save(gm);

        return group.get();
    }

    public Group removeMember(Long groupId, Long userId) throws Exception {
        Optional<Group> group = groupRepository.findById(groupId);
        Optional<User> user = userRepository.findById(userId);
        if (!group.isPresent() || !user.isPresent()) {
            throw new Exception("Group or user not found");
        }

        Optional<GroupMember> gm = groupMemberRepository.findByGroupAndUser(group.get(), user.get());
        if (!gm.isPresent()) {
            throw new Exception("User not in group");
        }

        groupMemberRepository.delete(gm.get());
        return group.get();
    }
}
