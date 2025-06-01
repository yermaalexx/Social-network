package com.yermaalexx.socialnetwork.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "new_messages",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "who_sent_message_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewMessage {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "who_sent_message_id", nullable = false)
    private UUID whoSentMessageId;
}
