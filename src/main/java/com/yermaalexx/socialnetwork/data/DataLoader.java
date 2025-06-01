package com.yermaalexx.socialnetwork.data;

import com.yermaalexx.socialnetwork.model.User;
import com.yermaalexx.socialnetwork.repository.UserRepository;
import com.yermaalexx.socialnetwork.service.PhotoService;
import com.yermaalexx.socialnetwork.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Configuration
@Slf4j
public class DataLoader {

    @Bean
    public CommandLineRunner dataLoaderUsers(UserService userService,
                                             UserRepository userRepository,
                                             PhotoService photoService) {
        return (args) -> {
            if(userRepository.count() == 0) {
                User user = userService.saveNewUser(new User(null,"Alexx",
                        "alexx","pass",1997,"Dnipro",LocalDate.now(),
                        List.of("Movies SciFi","Movies Drama","Movies Comedy")), null);
                try {
                    ClassPathResource imgFile = new ClassPathResource("static/images/data/woman3.jpg");
                    byte[] photo = FileCopyUtils.copyToByteArray(imgFile.getInputStream());
                    photoService.saveNewPhoto(user.getId(),photo);
                } catch (IOException e) {
                    log.warn("Cannot load photo for {} in DataLoader", user.getLogin());
                }
                user = userService.saveNewUser(new User(null,"Bill",
                        "bill","pass",2000,"NY",LocalDate.now(),
                        List.of("Movies SciFi","Movies Drama","Movies Comedy","Books SciFi","Books Thriller","Books Classic")), null);
                try {
                    ClassPathResource imgFile = new ClassPathResource("static/images/data/man2.jpg");
                    byte[] photo = FileCopyUtils.copyToByteArray(imgFile.getInputStream());
                    photoService.saveNewPhoto(user.getId(),photo);
                } catch (IOException e) {
                    log.warn("Cannot load photo for {} in DataLoader", user.getLogin());
                }
                user = userService.saveNewUser(new User(null,"Dave",
                        "dave","pass",2005,"LA",LocalDate.now(),
                        List.of("Movies SciFi","Movies Action","Movies Documentaries","Books Horror","Rock")), null);
                try {
                    ClassPathResource imgFile = new ClassPathResource("static/images/data/man3.jpg");
                    byte[] photo = FileCopyUtils.copyToByteArray(imgFile.getInputStream());
                    photoService.saveNewPhoto(user.getId(),photo);
                } catch (IOException e) {
                    log.warn("Cannot load photo for {} in DataLoader", user.getLogin());
                }
                userService.saveNewUser(new User(null,"Petya",
                        "petya","pass",1998,"Dnipro",LocalDate.now(),
                        List.of("Movies SciFi","Movies Drama","Books SciFi","Books Thriller","Hip-Hop","Cycling & Running")), null);
            }
        };
    }
}
