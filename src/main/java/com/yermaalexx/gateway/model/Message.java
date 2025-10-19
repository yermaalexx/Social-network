package com.yermaalexx.gateway.model;

import com.yermaalexx.gateway.dto.MessageDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "messages", indexes = {
        @Index(name = "idx_sender_receiver_sent", columnList = "sender_id, receiver_id, sent_at"),
        @Index(name = "idx_receiver_sender_sent", columnList = "receiver_id, sender_id, sent_at")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "sender_id", nullable = false)
    private UUID senderId;

    @Column(name = "receiver_id", nullable = false)
    private UUID receiverId;

    @Column(nullable = false, length = 200)
    @Size(max = 200)
    private String content;

    @Column(name = "sent_at", nullable = false)
    private LocalDateTime sentAt = LocalDateTime.now();

    public static Message from(MessageDTO messageDTO) {
        if (messageDTO == null)
            return null;
        return new Message(
                null,
                messageDTO.getSenderId(),
                messageDTO.getReceiverId(),
                messageDTO.getContent(),
                messageDTO.getSentAt()
        );
    }
}
