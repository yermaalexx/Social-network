package com.yermaalexx.gate.repository;

import com.yermaalexx.gate.model.Interest;
import com.yermaalexx.gate.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query("""
        SELECT DISTINCT u.id FROM User u
        JOIN u.interests i
        WHERE i IN :interests AND u.id <> :excludeUserId
    """)
    List<UUID> findMatchingUserIdsByInterests(
            @Param("interests") List<String> interests,
            @Param("excludeUserId") UUID excludeUserId
    );

    @Query(value = """
        SELECT ui2.user_id
        FROM user_interests ui1
        JOIN user_interests ui2 ON ui1.interest = ui2.interest
        WHERE ui1.user_id = :userId AND ui2.user_id != :userId
        GROUP BY ui2.user_id
        ORDER BY COUNT(*) DESC
    """, nativeQuery = true)
    List<UUID> findUsersSortedByInterestMatch(@Param("userId") UUID userId);


}
