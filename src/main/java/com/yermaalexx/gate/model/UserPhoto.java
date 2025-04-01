package com.yermaalexx.gate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPhoto {
    @Id
    private UUID userId;

    @Lob
    @Column(name = "user_photo", columnDefinition = "BYTEA")
    private byte[] userPhoto;
}
