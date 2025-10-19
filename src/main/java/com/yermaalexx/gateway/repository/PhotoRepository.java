package com.yermaalexx.gateway.repository;

import com.yermaalexx.gateway.model.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PhotoRepository extends JpaRepository<UserPhoto, UUID> {
    Optional<UserPhoto> findByUserId(UUID userId);
}
