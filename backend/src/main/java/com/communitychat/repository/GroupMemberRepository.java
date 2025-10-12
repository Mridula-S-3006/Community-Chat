package com.communitychat.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.communitychat.model.entity.Group;
import com.communitychat.model.entity.GroupMember;
import com.communitychat.model.entity.User;

@Repository
public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {
    List<GroupMember> findByGroup(Group group);
    List<GroupMember> findByUser(User user);
    Optional<GroupMember> findByGroupAndUser(Group group, User user);
}
