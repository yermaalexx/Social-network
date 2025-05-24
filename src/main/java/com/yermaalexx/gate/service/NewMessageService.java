package com.yermaalexx.gate.service;

import com.yermaalexx.gate.model.NewMessage;
import com.yermaalexx.gate.repository.NewMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NewMessageService {
    private final NewMessageRepository newMessageRepository;

    @Transactional
    public void deleteNewMessage(UUID userId, UUID whoSent) {
        newMessageRepository.deleteByUserIdAndWhoSentMessageId(userId, whoSent);
    }

    public List<NewMessage> findAllByUserId(UUID userId) {
        return newMessageRepository.findAllByUserId(userId);
    }

    public void save(UUID userId, UUID whoSent) {
        if (!newMessageRepository.existsByUserIdAndWhoSentMessageId(userId, whoSent))
            newMessageRepository.save(new NewMessage(null, userId, whoSent));
    }
}
