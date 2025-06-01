package com.yermaalexx.socialnetwork.service;

import com.yermaalexx.socialnetwork.model.UserLogin;
import com.yermaalexx.socialnetwork.repository.UserLoginRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserLoginService {

    private final UserLoginRepository userLoginRepository;
    private final PasswordEncoder passwordEncoder;

    public void saveLogin(String login, String password, UUID userId) {
        log.info("Saving login for userId={}, login={}", userId, login);
        userLoginRepository.save(new UserLogin(login,
                passwordEncoder.encode(password), userId, "USER"));
        log.debug("User login saved successfully for userId={}", userId);
    }

    public boolean existsByLogin(String login) {
        log.debug("Checking if login '{}' exists", login);
        boolean exists = userLoginRepository.existsByLogin(login);
        log.info("Login '{}' exists: {}", login, exists);
        return exists;
    }

}
