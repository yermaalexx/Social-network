package com.yermaalexx.socialnetwork.service;

import com.yermaalexx.socialnetwork.model.UserPhoto;
import com.yermaalexx.socialnetwork.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoServiceCache {

    private final PhotoRepository photoRepository;

    @Cacheable(value = "photos", key = "#id")
    public UserPhoto getPhoto(UUID id) {
        log.info("Fetching photo from DB for userId={}", id);
        return photoRepository.findById(id).orElse(null);
    }

    @CachePut(value = "photos", key = "#userPhoto.userId")
    public UserPhoto savePhoto(UserPhoto userPhoto) {
        return photoRepository.save(userPhoto);
    }

    @CacheEvict(value = "photos", key = "#id")
    public void deletePhoto(UUID id) {
        photoRepository.deleteById(id);
    }

}
