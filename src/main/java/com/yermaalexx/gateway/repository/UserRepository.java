package com.yermaalexx.gateway.repository;

import com.yermaalexx.gateway.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    @Query(value = """
        SELECT CAST(ui2.user_id AS VARCHAR) AS uid
        FROM user_interests ui1
        JOIN user_interests ui2 ON ui1.interest = ui2.interest
        WHERE ui1.user_id = :userId AND ui2.user_id <> :userId
        GROUP BY ui2.user_id
        ORDER BY COUNT(*) DESC
    """, nativeQuery = true)
    List<String> findUsersSortedByInterestMatch(@Param("userId") UUID userId);

    @Query("""
        select u
        from User u
        left join fetch u.interests
        where u.id = :id
    """)
    Optional<User> findById(@Param("id") UUID id);
}
