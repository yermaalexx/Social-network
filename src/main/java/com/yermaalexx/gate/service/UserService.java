package com.yermaalexx.gate.service;

import com.yermaalexx.gate.model.Interest;
import com.yermaalexx.gate.model.User;
import com.yermaalexx.gate.model.UserMatch;
import com.yermaalexx.gate.model.UserPhoto;
import com.yermaalexx.gate.repository.UserMatchRepository;
import com.yermaalexx.gate.repository.UserPhotoRepository;
import com.yermaalexx.gate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserPhotoRepository userPhotoRepository;
    private final UserMatchRepository userMatchRepository;

    @Transactional
    public User saveUser(User user, byte[] photoBytes) {
        user.setRegistrationDate(LocalDate.now());
        User savedUser = userRepository.save(user);
        if(photoBytes != null) {
            userPhotoRepository.save(new UserPhoto(savedUser.getId(), photoBytes));
        }
        return savedUser;
    }

    @Transactional
    public void processUserMatches(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

        List<String> userInterests = user.getInterests();
        if (userInterests.isEmpty()) return;

        List<UUID> matchedIds = userRepository.findMatchingUserIdsByInterests(userInterests, userId);

        List<UserMatch> matches = new ArrayList<>();
        for (UUID matchedUserId : matchedIds) {
            if (!userMatchRepository.existsByUserIdAndMatchedUserId(userId, matchedUserId)) {
                matches.add(new UserMatch(null, userId, matchedUserId));
                matches.add(new UserMatch(null, matchedUserId, userId));
            }
        }

        userMatchRepository.saveAll(matches);
    }
}
