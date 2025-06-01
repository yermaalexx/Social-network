package com.yermaalexx.socialnetwork.service;

import com.yermaalexx.socialnetwork.model.NewMessage;
import com.yermaalexx.socialnetwork.repository.NewMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class NewMessageService {
    private final NewMessageRepository newMessageRepository;

    @Transactional
    public void deleteNewMessage(UUID userId, UUID whoSent) {
        log.info("Deleting new message record: userId={}, whoSent={}", userId, whoSent);
        newMessageRepository.deleteByUserIdAndWhoSentMessageId(userId, whoSent);
        log.debug("New message record deleted successfully: userId={}, whoSent={}", userId, whoSent);
    }

    public List<NewMessage> findAllByUserId(UUID userId) {
        log.debug("Finding all new message records for userId={}", userId);
        List<NewMessage> newMessages = newMessageRepository.findAllByUserId(userId);
        log.info("Found {} new message record(s) for userId={}", newMessages.size(), userId);
        return newMessages;
    }

    public void save(UUID userId, UUID whoSent) {
        log.debug("Checking if new message record from {} to {} already exists", whoSent, userId);
        if (!newMessageRepository.existsByUserIdAndWhoSentMessageId(userId, whoSent)) {
            log.info("Saving new message notification: to userId={}, from={}", userId, whoSent);
            newMessageRepository.save(new NewMessage(null, userId, whoSent));
        } else {
            log.debug("New message from {} to {} already exists. Skipping save.", whoSent, userId);
        }
    }
}
