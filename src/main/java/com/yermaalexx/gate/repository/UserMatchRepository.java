package com.yermaalexx.gate.repository;

import com.yermaalexx.gate.model.UserMatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserMatchRepository extends JpaRepository<UserMatch, UUID> {
    List<UserMatch> findByUserId(UUID userId);

    List<UserMatch> findByMatchedUserId(UUID matchedUserId);

    boolean existsByUserIdAndMatchedUserId(UUID userId, UUID matchedUserId);

    void deleteByUserIdAndMatchedUserId(UUID userId, UUID matchedUserId);
}
