package com.yermaalexx.gate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "user_matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMatch {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "matched_user_id", nullable = false)
    private UUID matchedUserId;

}
