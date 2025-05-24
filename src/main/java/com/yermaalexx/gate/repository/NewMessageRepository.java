package com.yermaalexx.gate.repository;

import com.yermaalexx.gate.model.NewMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface NewMessageRepository extends JpaRepository<NewMessage, UUID> {

    List<NewMessage> findAllByUserId(UUID userId);
    void deleteByUserIdAndWhoSentMessageId(UUID userId, UUID whoSentMessageId);
    void deleteByUserId(UUID userId);
    void deleteByWhoSentMessageId(UUID whoSentMessageId);
    boolean existsByUserIdAndWhoSentMessageId(UUID userId, UUID whoSentMessageId);
}
