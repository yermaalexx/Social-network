package com.yermaalexx.gate.service;

import com.yermaalexx.gate.model.Match;
import com.yermaalexx.gate.model.NewMessage;
import com.yermaalexx.gate.model.User;
import com.yermaalexx.gate.model.UserDTO;
import com.yermaalexx.gate.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserLoginService userLoginService;
    private final PhotoService photoService;
    private final MatchService matchService;
    private final NewMessageService newMessageService;


    @Transactional
    public User saveNewUser(User user, byte[] photoBytes) {
        user.setRegistrationDate(LocalDate.now());
        User savedUser = userRepository.save(user);
        if(photoBytes != null) {
            photoService.saveNewPhoto(savedUser.getId(), photoBytes);
        }
        userLoginService.saveLogin(user.getLogin(), user.getPassword(), savedUser.getId());
        return savedUser;
    }

    @Transactional
    public void updateUser(UserDTO userAfter) {
        UUID id = userAfter.getId();
        User user = userRepository.findById(id).orElseThrow();
        user.setName(userAfter.getName());
        user.setBirthYear(userAfter.getBirthYear());
        user.setLocation(userAfter.getLocation());
        if (!userAfter.getMatchingInterests().equals(user.getInterests())) {
            matchService.deleteAllMatches(id);
            user.setInterests(userAfter.getMatchingInterests());
            processUserMatches(user);

        }


        userRepository.save(user);
        if (userAfter.getPhotoBase64() != null)
            photoService.updatePhoto(id,
                Base64.getDecoder().decode(userAfter.getPhotoBase64()));
        else photoService.deletePhoto(id);

    }

    @Transactional
    public void processUserMatches(User user) {

        List<String> userInterests = user.getInterests();
        if (userInterests.isEmpty()) return;

        UUID userId = user.getId();
        List<UUID> matchedIds = userRepository.findMatchingUserIdsByInterests(userInterests, userId);

        List<Match> matches = new ArrayList<>();
        for (UUID matchedUserId : matchedIds) {
            if (!matchService.existsByUserIdAndMatchedId(userId, matchedUserId)) {
                matches.add(new Match(null, userId, matchedUserId));
                matches.add(new Match(null, matchedUserId, userId));
            }
        }

        matchService.saveAll(matches);
    }

    public List<UUID>[] getUsersSortedByInterestMatch(UUID userId) {
        List<UUID> usersWithNewMessages = newMessageService.findAllByUserId(userId)
                .stream()
                .map(NewMessage::getWhoSentMessageId)
                .toList();
        List<UUID> usersSortedByInterestMatch = userRepository.findUsersSortedByInterestMatch(userId)
                .stream()
                .map(UUID::fromString)
                .filter(id -> !usersWithNewMessages.contains(id))
                .toList();
        List<UUID> allSortedUsers = new ArrayList<>(usersWithNewMessages);
        allSortedUsers.addAll(usersSortedByInterestMatch);

        return new List[]{usersWithNewMessages, allSortedUsers};
    }

    public List<UserDTO> getUserMatchedDTOs(List<UUID> ids, UUID userId) {
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
                    return new UserDTO(u.getId(), u.getName(), u.getBirthYear(),
                            u.getLocation(), u.getRegistrationDate(),
                            photoBase64, common, other);
                })
                .toList();
    }

    public UserDTO getUserProfile(UUID userId) {
        User user = userRepository.findById(userId).orElseThrow();
        byte[] photo = photoService.findPhotoById(userId);
        String photoBase64 = (photo == null) ? null : Base64.getEncoder().encodeToString(photo);
        return new UserDTO(userId, user.getName(), user.getBirthYear(), user.getLocation(),
                user.getRegistrationDate(), photoBase64, user.getInterests(), null);
    }

}
