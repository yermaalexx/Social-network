package com.yermaalexx.socialnetwork.repository;

import com.yermaalexx.socialnetwork.model.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {
    Optional<Chat> findByUserAAndUserB(UUID userA, UUID userB);
    Chat getChatById(UUID chatId);
    void deleteByUserAAndUserB(UUID userA, UUID userB);
}
