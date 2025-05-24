package com.yermaalexx.gate.repository;

import com.yermaalexx.gate.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MatchRepository extends JpaRepository<Match, UUID> {
    List<Match> findAllByUserId(UUID userId);

    boolean existsByUserIdAndMatchedUserId(UUID userId, UUID matchedUserId);

    void deleteByUserIdAndMatchedUserId(UUID userId, UUID matchedUserId);

    void deleteAllByUserId(UUID userId);
    void deleteAllByMatchedUserId(UUID matchedUserId);
}
