package com.yermaalexx.gate.service;

import com.yermaalexx.gate.model.UserPhoto;
import com.yermaalexx.gate.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PhotoService {

    private final PhotoRepository photoRepository;

    public void saveNewPhoto(UUID userId, byte[] photoBytes) {
        log.info("Saving new photo for userId={}, size={} Kb", userId, photoBytes != null ? photoBytes.length/1024 : 0);
        photoRepository.save(new UserPhoto(userId, photoBytes));
    }

    public void deletePhoto(UUID userId) {
        log.info("Deleting photo for userId={}", userId);
        photoRepository.deleteById(userId);
    }

    public void updatePhoto(UUID userId, byte[] photoBytes) {
        log.info("Updating photo for userId={}", userId);
        deletePhoto(userId);
        if (photoBytes != null) {
            log.debug("New photo size: {} Kb", photoBytes.length/1024);
            photoRepository.save(new UserPhoto(userId, photoBytes));
            log.info("Photo updated successfully for userId={}", userId);
        } else {
            log.warn("Skipped saving photo: provided photoBytes is null for userId={}", userId);
        }
    }

    public byte[] findPhotoById(UUID userId) {
        log.debug("Searching for photo by userId={}", userId);
        return photoRepository.findByUserId(userId)
                .map(photo -> {
                    log.info("Photo found for userId={}, size={} Kb", userId, photo.getUserPhoto().length/1024);
                    return photo.getUserPhoto();
                })
                .orElseGet(() -> {
                    log.warn("No photo found for userId={}", userId);
                    return null;
                });
    }

}
