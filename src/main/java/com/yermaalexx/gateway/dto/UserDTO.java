package com.yermaalexx.gateway.dto;

import com.yermaalexx.gateway.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private UUID id;
    private String name;
    private String login;
    private String password;
    private int birthYear;
    private String location;
    private LocalDate registrationDate;
    private String photoBase64;
    private List<String> matchingInterests;
    private List<String> otherInterests;
/*
    public static UserDTO from(User user, byte[] photo) {
        if (user == null)
            return null;
        return new UserDTO(user.getId(),
                user.getName(),
                user.getBirthYear(),
                user.getLocation(),
                user.getRegistrationDate(),
                photo,
                user.getInterests());
    }*/
}
