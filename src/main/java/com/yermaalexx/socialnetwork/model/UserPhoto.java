package com.yermaalexx.socialnetwork.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.util.UUID;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserPhoto {
    @Id
    private UUID userId;

    @Basic
    @Column(name = "user_photo", columnDefinition = "BYTEA")
    private byte[] userPhoto;

}
