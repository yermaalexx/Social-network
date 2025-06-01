package com.yermaalexx.socialnetwork.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "chats",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_a", "user_b"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Chat {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_a", nullable = false)
    private UUID userA;

    @Column(name = "user_b", nullable = false)
    private UUID userB;

    @OneToMany(
            mappedBy = "chat",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<Message> messages = new ArrayList<>();

    public void addMessage(Message msg, int maxMessagesInChat) {
        msg.setChat(this);
        messages.add(msg);
        if (messages.size() > maxMessagesInChat) {
            messages.remove(0);
        }
    }
}
