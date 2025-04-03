package com.yermaalexx.gate.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
    private UUID id;

    @NotNull(message = "Enter your name")
    @NotBlank(message = "Enter your name")
    private String name;

    @Min(value = 1900, message = "Year of birth must be between 1900 and 2020")
    @Max(value = 2020, message = "Year of birth must be between 1900 and 2020")
    private int birthYear;

    @NotNull(message = "Enter your location")
    @NotBlank(message = "Enter your location")
    private String location;

    private LocalDate registrationDate;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "user_interests", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "interest")
    private List<String> interests = new ArrayList<>();
}
