package com.yermaalexx.socialnetwork.repository;

import com.yermaalexx.socialnetwork.model.Reject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RejectRepository extends JpaRepository<Reject, UUID> {
    List<Reject> findAllByUserId(UUID userId);

}
