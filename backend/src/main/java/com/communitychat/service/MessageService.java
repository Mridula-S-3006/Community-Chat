package com.communitychat.service;

import com.communitychat.model.entity.Message;
import com.communitychat.model.entity.User;
import com.communitychat.model.entity.Group;
import com.communitychat.repository.MessageRepository;
import com.communitychat.repository.UserRepository;
import com.communitychat.repository.GroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;

    @Autowired
    public MessageService(MessageRepository messageRepository, UserRepository userRepository, GroupRepository groupRepository) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.groupRepository = groupRepository;
    }

    public Message sendDirectMessage(Long senderId, Long receiverId, String content) throws Exception {
        Optional<User> sender = userRepository.findById(senderId);
        Optional<User> receiver = userRepository.findById(receiverId);

        if (!sender.isPresent() || !receiver.isPresent()) {
            throw new Exception("Sender or receiver not found");
        }

        Message message = new Message();
        message.setSender(sender.get());
        message.setReceiver(receiver.get());
        message.setContent(content);
        message.setSentAt(java.time.LocalDateTime.now());

        return messageRepository.save(message);
    }

    public Message sendGroupMessage(Long senderId, Long groupId, String content) throws Exception {
        Optional<User> sender = userRepository.findById(senderId);
        Optional<Group> group = groupRepository.findById(groupId);

        if (!sender.isPresent() || !group.isPresent()) {
            throw new Exception("Sender or group not found");
        }

        Message message = new Message();
        message.setSender(sender.get());
        message.setGroup(group.get());
        message.setContent(content);
        message.setSentAt(java.time.LocalDateTime.now());

        return messageRepository.save(message);
    }

    public List<Message> getDirectMessages(Long user1Id, Long user2Id) throws Exception {
        Optional<User> user1 = userRepository.findById(user1Id);
        Optional<User> user2 = userRepository.findById(user2Id);

        if (!user1.isPresent() || !user2.isPresent()) {
            throw new Exception("User not found");
        }

        List<Message> messages = messageRepository.findBySender(user1.get());
        messages.addAll(messageRepository.findBySender(user2.get()));

        messages.removeIf(m -> (m.getReceiver() == null) || 
                !(m.getReceiver().getId().equals(user1Id) || m.getReceiver().getId().equals(user2Id)));

        messages.sort((a, b) -> a.getSentAt().compareTo(b.getSentAt()));

        return messages;
    }

    public List<Message> getGroupMessages(Long groupId) throws Exception {
        Optional<Group> group = groupRepository.findById(groupId);
        if (!group.isPresent()) {
            throw new Exception("Group not found");
        }
        List<Message> messages = messageRepository.findByGroup(group.get());
        messages.sort((a, b) -> a.getSentAt().compareTo(b.getSentAt()));
        return messages;
    }
}
