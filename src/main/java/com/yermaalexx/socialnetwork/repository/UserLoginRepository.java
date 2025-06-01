package com.yermaalexx.socialnetwork.repository;

import com.yermaalexx.socialnetwork.model.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserLoginRepository extends JpaRepository<UserLogin, String> {
    UserLogin findByLogin(String login);
    boolean existsByLogin(String login);
}
