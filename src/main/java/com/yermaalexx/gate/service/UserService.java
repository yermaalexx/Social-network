package com.yermaalexx.gate.service;

import com.yermaalexx.gate.model.NewMessage;
import com.yermaalexx.gate.model.User;
import com.yermaalexx.gate.model.UserDTO;
import com.yermaalexx.gate.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final UserLoginService userLoginService;
    private final PhotoService photoService;
    private final RejectService rejectService;
    private final NewMessageService newMessageService;


    @Transactional
    public User saveNewUser(User user, byte[] photoBytes) {
        user.setRegistrationDate(LocalDate.now());
        log.info("Registering new user: name={}, birthYear={}, location={}", user.getName(), user.getBirthYear(), user.getLocation());

        User savedUser = userRepository.save(user);
        log.debug("User saved with id={}", savedUser.getId());

        if(photoBytes != null) {
            log.info("Saving profile photo for userId={}, size={} Kb", savedUser.getId(), photoBytes.length/1024);
            photoService.saveNewPhoto(savedUser.getId(), photoBytes);
        }

        userLoginService.saveLogin(user.getLogin(), user.getPassword(), savedUser.getId());
        log.info("Login credentials saved for userId={}", savedUser.getId());

        return savedUser;
    }

    @Transactional
    public void updateUser(UserDTO userAfter, HttpSession session) {
        UUID id = userAfter.getId();
        log.info("Updating user profile: userId={}", id);

        User user = userRepository.findById(id).orElseThrow();
        boolean interestsChanged = !userAfter.getMatchingInterests().equals(user.getInterests());

        user.setName(userAfter.getName());
        user.setBirthYear(userAfter.getBirthYear());
        user.setLocation(userAfter.getLocation());

        if (interestsChanged) {
            log.info("User interests changed for userId={}", id);
            user.setInterests(userAfter.getMatchingInterests());
            session.setAttribute("matchIds", null);
        }

        userRepository.save(user);
        log.debug("User profile updated in DB for userId={}", id);

        if (userAfter.getPhotoBase64() != null) {
            byte[] decodedPhoto = Base64.getDecoder().decode(userAfter.getPhotoBase64());
            log.info("Updating profile photo for userId={}, size={} Kb", id, decodedPhoto.length/1024);
            photoService.updatePhoto(id, decodedPhoto);
        }
        else {
            log.info("Deleting profile photo for userId={}", id);
            photoService.deletePhoto(id);
        }

    }

    public List<UUID>[] getUsersSortedByInterestMatch(UUID userId) {
        log.info("Getting sorted user matches for userId={}", userId);

        List<UUID> rejectedUsers = rejectService.findAllRejectedUsers(userId);
        log.debug("{} rejected users for userId={}", rejectedUsers.size(), userId);

        List<UUID> usersWithNewMessages = newMessageService.findAllByUserId(userId)
                .stream()
                .map(NewMessage::getWhoSentMessageId)
                .toList();
        log.debug("{} users with new messages for userId={}", usersWithNewMessages.size(), userId);

        List<UUID> usersSortedByInterestMatch = userRepository.findUsersSortedByInterestMatch(userId)
                .stream()
                .map(UUID::fromString)
                .filter(id -> !rejectedUsers.contains(id))
                .filter(id -> !usersWithNewMessages.contains(id))
                .toList();
        List<UUID> allSortedUsers = new ArrayList<>(usersWithNewMessages);
        allSortedUsers.addAll(usersSortedByInterestMatch);

        log.info("Matched users (sorted, with no rejects) found: {}", allSortedUsers.size());

        return new List[]{usersWithNewMessages, allSortedUsers};
    }

    public List<UserDTO> getUserMatchedDTOs(List<UUID> ids, UUID userId) {
        log.info("Generating matched user DTOs for userId={} and {} matched IDs", userId, ids.size());

        List<User> users = userRepository.findAllById(ids);
        Map<UUID, User> map = users.stream()
                .collect(Collectors.toMap(User::getId, u -> u));
        List<String> interests = userRepository.findById(userId)
                .orElseThrow().getInterests();

        return ids.stream()
                .map(id -> {
                    User u = map.get(id);
                    byte[] photo = photoService.findPhotoById(id);
                    String photoBase64 = (photo == null) ? null : Base64.getEncoder().encodeToString(photo);
                    List<String> common = u.getInterests().stream()
                            .filter(interests::contains)
                            .toList();
                    List<String> other = u.getInterests().stream()
                            .filter(i -> !interests.contains(i))
                            .toList();

                    log.debug("For userId={} and matchedUserId={}, commonInterests={}, otherInterests={}",
                            userId, u.getId(), common.size(), other.size());

                    return new UserDTO(u.getId(), u.getName(), u.getBirthYear(),
                            u.getLocation(), u.getRegistrationDate(),
                            photoBase64, common, other);
                })
                .toList();
    }

    public UserDTO getUserProfile(UUID userId) {
        log.info("Fetching profile for userId={}", userId);

        User user = userRepository.findById(userId).orElseThrow();
        byte[] photo = photoService.findPhotoById(userId);
        String photoBase64 = (photo == null) ? null : Base64.getEncoder().encodeToString(photo);

        log.debug("Profile retrieved for userId={}, hasPhoto={}", userId, photo != null);

        return new UserDTO(userId, user.getName(), user.getBirthYear(), user.getLocation(),
                user.getRegistrationDate(), photoBase64, user.getInterests(), null);
    }

}
