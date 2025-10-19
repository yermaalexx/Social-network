package com.yermaalexx.gateway.controller;

import com.yermaalexx.gateway.dto.UserDTO;
import com.yermaalexx.gateway.model.Interest;
import com.yermaalexx.gateway.model.InterestCategory;
import com.yermaalexx.gateway.service.UserLoginService;
import com.yermaalexx.gateway.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
@Slf4j
public class RegistrationController {

    private final UserService userService;
    private final UserLoginService userLoginService;
    private byte[] photoBytes;

    @ModelAttribute
    public void addInterestsToModel(Model model) {
        model.addAttribute("categoryMap", InterestCategory.getCategoryMap());
    }

    @GetMapping
    public String showRegistrationForm(Model model, HttpServletRequest request) {
        log.info("Registration form requested from IP={}", request.getRemoteAddr());

        model.addAttribute("user", new UserDTO());
        return "register";
    }

    @PostMapping
    public String registerUser(UserDTO userDTO,
                               @RequestParam("userPhoto") MultipartFile userPhoto,
                               @RequestParam String confirm,
                               Model model) {

        log.info("Received registration attempt: login={}, name={}, birthYear={}, location={}",
                userDTO.getLogin(), userDTO.getName(), userDTO.getBirthYear(), userDTO.getLocation());

        try {
            if(userPhoto!=null && !userPhoto.isEmpty()) {
                photoBytes = userPhoto.getBytes();
                log.debug("Uploaded photo received: size={} Kb", photoBytes.length/1024);
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            }
        } catch (IOException e) {
            log.error("Failed to read uploaded photo for login={}: {}", userDTO.getLogin(), e.getMessage());
            model.addAttribute("error", "Error reading uploaded file.");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }

        if (userLoginService.existsByLogin(userDTO.getLogin())) {
            log.warn("Attempt to register with already existing login: {}", userDTO.getLogin());
            model.addAttribute("error", "A user with this login already exists");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }

        if (userDTO.getBirthYear() < 1900 || userDTO.getBirthYear() > 2020) {
            log.warn("Invalid birth year provided: {}", userDTO.getBirthYear());
            model.addAttribute("error", "Year of birth must be between 1900 and 2020");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }

        List<String> selectedInterests = userDTO.getMatchingInterests();
        if (selectedInterests.isEmpty()) {
            log.warn("Registration attempt without selected interests for login={}", userDTO.getLogin());
            model.addAttribute("error", "You must choose at least one interest");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }
        for(InterestCategory category : InterestCategory.values()) {
            long count = Interest.getByCategory(category)
                    .stream()
                    .filter(i -> selectedInterests.contains(i.getName()))
                    .count();
            if(count > 3) {
                log.warn("For login={} too many interests from category {}: {} selected (limit 3)",
                        userDTO.getLogin(), category.name(), count);
                model.addAttribute("error", "Cannot select more than 3 interests from " + category.getDescription());
                if (photoBytes != null)
                    model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
                return "register";
            }
        }

        if (!userDTO.getPassword().equals(confirm)) {
            log.warn("Password confirmation mismatch for login={}", userDTO.getLogin());
            model.addAttribute("error", "Fields 'Password' and 'Confirm password' are not the same");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }

        UserDTO savedUser = userService.saveNewUser(userDTO, photoBytes);
        log.info("User successfully registered: id={}, login={}", savedUser.getId(), savedUser.getLogin());

        return "redirect:/login";
    }

}
