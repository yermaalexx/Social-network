package com.yermaalexx.gate.service;

import com.yermaalexx.gate.model.UserPhoto;
import com.yermaalexx.gate.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;

    public void saveNewPhoto(UUID userId, byte[] photoBytes) {
        photoRepository.save(new UserPhoto(userId, photoBytes));
    }

    public void deletePhoto(UUID userId) {
        photoRepository.deleteById(userId);
    }

    public void updatePhoto(UUID userId, byte[] photoBytes) {
        deletePhoto(userId);
        if (photoBytes != null)
            photoRepository.save(new UserPhoto(userId, photoBytes));
    }

    public byte[] findPhotoById(UUID userId) {
        return photoRepository.findByUserId(userId)
                .map(UserPhoto::getUserPhoto)
                .orElse(null);
    }

}
