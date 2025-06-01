package com.yermaalexx.socialnetwork.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class UserDTO {
    private UUID id;
    private String name;
    private int birthYear;
    private String location;
    private LocalDate registrationDate;
    private String photoBase64;
    private List<String> matchingInterests;
    private List<String> otherInterests;
}
