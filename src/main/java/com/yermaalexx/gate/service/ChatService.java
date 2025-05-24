package com.yermaalexx.gate.service;

import com.yermaalexx.gate.config.AppConfig;
import com.yermaalexx.gate.model.Chat;
import com.yermaalexx.gate.model.Message;
import com.yermaalexx.gate.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final NewMessageService newMessageService;
    private final AppConfig appConfig;

    public Chat getChat(UUID userA, UUID userB) {
        UUID a = userA.compareTo(userB)<0 ? userA : userB;
        UUID b = userA.compareTo(userB)<0 ? userB : userA;
        return chatRepository.findByUserAAndUserB(a, b)
                .orElseGet(() -> {
                    Chat c = new Chat();
                    c.setUserA(a);
                    c.setUserB(b);
                    return chatRepository.save(c);
                });
    }

    @Transactional
    public void deleteChat(UUID userA, UUID userB) {
        UUID a = userA.compareTo(userB)<0 ? userA : userB;
        UUID b = userA.compareTo(userB)<0 ? userB : userA;
        chatRepository.deleteByUserAAndUserB(a, b);
    }

    @Transactional
    public void sendMessage(UUID chatId, UUID senderId, UUID otherId, String content) {
        Chat chat = chatRepository.getChatById(chatId);
        Message msg = new Message();
        msg.setSenderId(senderId);
        msg.setContent(content);
        chat.addMessage(msg, appConfig.getMaxMessagesInChat());
        chatRepository.save(chat);
        newMessageService.save(otherId, senderId);
    }

}
