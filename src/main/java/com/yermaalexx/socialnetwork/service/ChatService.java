package com.yermaalexx.socialnetwork.service;

import com.yermaalexx.socialnetwork.config.AppConfig;
import com.yermaalexx.socialnetwork.model.Chat;
import com.yermaalexx.socialnetwork.model.Message;
import com.yermaalexx.socialnetwork.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatService {
    private final ChatRepository chatRepository;
    private final NewMessageService newMessageService;
    private final AppConfig appConfig;

    public Chat getChat(UUID userA, UUID userB) {
        UUID a = userA.compareTo(userB)<0 ? userA : userB;
        UUID b = userA.compareTo(userB)<0 ? userB : userA;
        log.debug("getChat called with userA={}, userB={}", userA, userB);
        return chatRepository.findByUserAAndUserB(a, b)
                .orElseGet(() -> {
                    log.info("No existing chat found between {} and {}. Creating new chat.", a, b);
                    Chat c = new Chat();
                    c.setUserA(a);
                    c.setUserB(b);
                    Chat saved = chatRepository.save(c);
                    log.info("New chat created with id={} between {} and {}", saved.getId(), a, b);
                    return saved;
                });
    }

    @Transactional
    public void deleteChat(UUID userA, UUID userB) {
        UUID a = userA.compareTo(userB)<0 ? userA : userB;
        UUID b = userA.compareTo(userB)<0 ? userB : userA;
        log.info("Deleting chat between userA={} and userB={}", a, b);
        chatRepository.deleteByUserAAndUserB(a, b);
        log.info("Chat successfully deleted between {} and {}", a, b);

    }

    @Transactional
    public void sendMessage(UUID chatId, UUID senderId, UUID otherId, String content) {
        log.debug("sendMessage called: chatId={}, senderId={}, receiverId={}", chatId, senderId, otherId);
        Chat chat = chatRepository.getChatById(chatId);
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setContent(content);
        log.debug("Appending new message to chat. Chat id: {}", chatId);
        chat.addMessage(msg, appConfig.getMaxMessagesInChat());
        chatRepository.save(chat);
        log.info("Message from {} to {} saved in chat {}", senderId, otherId, chatId);
        newMessageService.save(otherId, senderId);
        log.info("New message notification saved for user {} from {}", otherId, senderId);
    }

}
