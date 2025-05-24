package com.yermaalexx.gate.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    private String name;

    @Transient
    private String login;

    @Transient
    private String password;

    private int birthYear;

    private String location;

    private LocalDate registrationDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id",
    referencedColumnName = "id", columnDefinition = "uuid"))
    @Column(name = "interest")
    private List<String> interests = new ArrayList<>();
}
