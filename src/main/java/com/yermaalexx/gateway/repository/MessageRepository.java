package com.yermaalexx.gateway.repository;

import com.yermaalexx.gateway.model.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {

    @Query("""
        SELECT m
        FROM Message m
        WHERE (m.senderId   = :userA AND m.receiverId = :userB)
           OR (m.senderId   = :userB AND m.receiverId = :userA)
        ORDER BY m.sentAt
    """)
    List<Message> findChatHistory(@Param("userA") UUID userA,
                                  @Param("userB") UUID userB);

    @Modifying
    @Transactional
    @Query("""
        DELETE
        FROM Message m
        WHERE (m.senderId   = :userA AND m.receiverId = :userB)
           OR (m.senderId   = :userB AND m.receiverId = :userA)
    """)
    void deleteChatHistory(@Param("userA") UUID userA,
                           @Param("userB") UUID userB);

}
