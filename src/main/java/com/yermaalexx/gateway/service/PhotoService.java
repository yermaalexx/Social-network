package com.yermaalexx.gateway.service;

import com.yermaalexx.gateway.model.UserPhoto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {

    private final PhotoServiceCache photoServiceCache;

    public void saveNewPhoto(UUID userId, byte[] photoBytes) {
        log.info("Saving new photo for userId={}, size={} Kb", userId, photoBytes != null ? photoBytes.length/1024 : 0);
        photoServiceCache.savePhoto(new UserPhoto(userId, photoBytes));
    }

    public void updatePhoto(UUID id, byte[] photoBytes) {
        log.info("Updating photo for userId={}", id);
        photoServiceCache.deletePhoto(id);
        if (photoBytes != null) {
            log.debug("New photo size: {} Kb", photoBytes.length/1024);
            photoServiceCache.savePhoto(new UserPhoto(id, photoBytes));
            log.info("Photo updated successfully for userId={}", id);
        } else {
            log.warn("Skipped saving photo: provided photoBytes is null for userId={}", id);
        }
    }

    public byte[] findPhotoById(UUID userId) {
        log.debug("Searching for photo by userId={}", userId);
        UserPhoto userPhoto = photoServiceCache.getPhoto(userId);
        return (userPhoto != null) ? userPhoto.getUserPhoto() : null;
    }

}
