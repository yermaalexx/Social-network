package com.yermaalexx.gateway.service;

import com.yermaalexx.gateway.config.AppConfig;
import com.yermaalexx.gateway.dto.MessageDTO;
import com.yermaalexx.gateway.model.Message;
import com.yermaalexx.gateway.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {
    private final MessageRepository messageRepository;
    private final NewMessageService newMessageService;
    private final AppConfig appConfig;

    public List<MessageDTO> getChat(UUID userA, UUID userB) {
        log.debug("getChat called with userA={}, userB={}", userA, userB);
        List<Message> allMessages = messageRepository.findChatHistory(userA, userB);
        while (allMessages.size() > appConfig.getMaxMessagesInChat()) {
            deleteMessage(allMessages.remove(0).getId());
        }
        log.info("Found {} messages between users {} and {}", allMessages.size(), userA, userB);
        Collections.reverse(allMessages);

        return allMessages.stream().map(MessageDTO::from).toList();
    }

    @Transactional
    public void deleteChat(UUID userA, UUID userB) {
        log.info("Deleting messages between {} and {}", userA, userB);
        messageRepository.deleteChatHistory(userA, userB);
        log.info("Messages successfully deleted between {} and {}", userA, userB);
    }

    @Transactional
    public void deleteMessage(UUID messageId) {
        log.info("Deleting message with messageId={}", messageId);
        messageRepository.deleteById(messageId);
    }

    @Transactional
    public void sendMessage(UUID senderId, UUID otherId, String content) {
        log.debug("sendMessage called: senderId={}, receiverId={}", senderId, otherId);
        Message message = messageRepository.save(new Message(null,senderId,otherId,content,LocalDateTime.now()));
        log.info("Message from {} to {} saved with id={}", message.getSenderId(), message.getReceiverId(), message.getId());
        newMessageService.save(otherId, senderId);
        log.info("New message notification saved for user {} from {}", otherId, senderId);
    }

}
