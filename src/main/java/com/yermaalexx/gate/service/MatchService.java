package com.yermaalexx.gate.service;

import com.yermaalexx.gate.model.Match;
import com.yermaalexx.gate.repository.MatchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MatchService {

    private final MatchRepository matchRepository;
    private final ChatService chatService;
    private final NewMessageService newMessageService;

    @Transactional
    public void reject(UUID userId, UUID matchedId) {
        matchRepository.deleteByUserIdAndMatchedUserId(userId, matchedId);
        matchRepository.deleteByUserIdAndMatchedUserId(matchedId, userId);
        chatService.deleteChat(userId, matchedId);
        newMessageService.deleteNewMessage(userId, matchedId);
        newMessageService.deleteNewMessage(matchedId, userId);
    }

    public boolean existsByUserIdAndMatchedId(UUID userId, UUID matchedId) {
        return matchRepository.existsByUserIdAndMatchedUserId(userId, matchedId);
    }

    public void saveAll(List<Match> matches) {
        matchRepository.saveAll(matches);
    }

    public void deleteAllMatches(UUID UserId) {
        matchRepository.deleteAllByUserId(UserId);
        matchRepository.deleteAllByMatchedUserId(UserId);
    }

}
