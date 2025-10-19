package com.yermaalexx.gateway.model;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.yermaalexx.gateway.dto.UserDTO;
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
@JsonIgnoreProperties(ignoreUnknown = true)
public class User {
    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    @JsonAlias("userId")
    private UUID id;

    private String name;
/*
    @Transient
    private String login;

    @Transient
    private String password;
*/
    private int birthYear;

    private String location;

    private LocalDate registrationDate;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id",
    referencedColumnName = "id", columnDefinition = "uuid"))
    @Column(name = "interest")
    private List<String> interests = new ArrayList<>();

    public static User from(UserDTO dto) {
        if (dto == null)
            return null;
        return new User(dto.getId(),
                dto.getName(),
                dto.getBirthYear(),
                dto.getLocation(),
                dto.getRegistrationDate(),
                dto.getMatchingInterests());
    }
}
