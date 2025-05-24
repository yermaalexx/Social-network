package com.yermaalexx.gate.service;

import com.yermaalexx.gate.model.UserLogin;
import com.yermaalexx.gate.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserLoginService {

    private final UserLoginRepository userLoginRepository;
    private final PasswordEncoder passwordEncoder;

    public void saveLogin(String login, String password, UUID userId) {
        userLoginRepository.save(new UserLogin(login,
                passwordEncoder.encode(password), userId, "USER"));
    }

    public boolean existsByLogin(String login) {
        return userLoginRepository.existsByLogin(login);
    }

}
