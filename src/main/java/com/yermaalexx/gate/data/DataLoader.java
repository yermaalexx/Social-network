package com.yermaalexx.gate.data;

import com.yermaalexx.gate.model.User;
import com.yermaalexx.gate.model.UserPhoto;
import com.yermaalexx.gate.repository.UserPhotoRepository;
import com.yermaalexx.gate.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Configuration
public class DataLoader {

    @Bean
    public CommandLineRunner dataLoaderUsers(UserRepository userRepository, UserPhotoRepository userPhotoRepository) {
        return (args) -> {
            if(userRepository.count() == 0) {
                User user = new User(null,"Bill",2000,"NY",LocalDate.now(),
                        List.of("SciFiM","Drama","Comedy","SciFiB","Thriller","Classic"));
                user = userRepository.save(user);
                try {
                    ClassPathResource imgFile = new ClassPathResource("static/images/data/man2.jpg");
                    byte[] photo = FileCopyUtils.copyToByteArray(imgFile.getInputStream());
                    userPhotoRepository.save(new UserPhoto(user.getId(),photo));
                } catch (IOException e) {}
                user = new User(null,"Dave",2005,"LA",LocalDate.now(),
                        List.of("SciFiM","Action","Docs","HorrorB","Rock"));
                user = userRepository.save(user);
                try {
                    ClassPathResource imgFile = new ClassPathResource("static/images/data/man3.jpg");
                    byte[] photo = FileCopyUtils.copyToByteArray(imgFile.getInputStream());
                    userPhotoRepository.save(new UserPhoto(user.getId(),photo));
                } catch (IOException e) {}
                user = new User(null,"Petya",1998,"Dnipro",LocalDate.now(),
                        List.of("SciFiM","Drama","SciFiB","Thriller","HipHop","CycleRun"));
                userRepository.save(user);
            }
        };
    }
}
