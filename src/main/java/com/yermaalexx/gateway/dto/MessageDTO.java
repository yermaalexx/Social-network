package com.yermaalexx.gateway.dto;

import com.yermaalexx.gateway.model.Message;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Data
@AllArgsConstructor
public class MessageDTO {
    private UUID messageId;
    private UUID senderId;
    private UUID receiverId;
    private String content;
    private LocalDateTime sentAt;
    private String formattedSentAt;

    public static MessageDTO from(Message message) {
        if (message == null)
            return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        String formatted = (message.getSentAt() != null) ? message.getSentAt().format(formatter) : null;
        return new MessageDTO(
                message.getId(),
                message.getSenderId(),
                message.getReceiverId(),
                message.getContent(),
                message.getSentAt(),
                formatted
        );
    }

    public void setFormattedSentAt() {
        if (sentAt == null)
            formattedSentAt = "";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");
        formattedSentAt = sentAt.format(formatter);
    }
}
