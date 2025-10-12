package com.communitychat.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.communitychat.model.entity.Group;
import com.communitychat.model.entity.Message;
import com.communitychat.model.entity.User;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySender(User sender);
    List<Message> findByReceiver(User receiver);
    List<Message> findByGroup(Group group);
}