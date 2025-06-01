package com.yermaalexx.socialnetwork.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reject {
    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    private UUID rejectedUserId;

}
