package com.yermaalexx.socialnetwork.controller;

import com.yermaalexx.socialnetwork.model.Interest;
import com.yermaalexx.socialnetwork.model.InterestCategory;
import com.yermaalexx.socialnetwork.model.User;
import com.yermaalexx.socialnetwork.service.UserLoginService;
import com.yermaalexx.socialnetwork.service.UserService;
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

        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping
    public String registerUser(User user,
                               @RequestParam("userPhoto") MultipartFile userPhoto,
                               @RequestParam String confirm,
                               Model model) {

        log.info("Received registration attempt: login={}, name={}, birthYear={}, location={}",
                user.getLogin(), user.getName(), user.getBirthYear(), user.getLocation());

        try {
            if(userPhoto!=null && !userPhoto.isEmpty()) {
                photoBytes = userPhoto.getBytes();
                log.debug("Uploaded photo received: size={} Kb", photoBytes.length/1024);
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            }
        } catch (IOException e) {
            log.error("Failed to read uploaded photo for login={}: {}", user.getLogin(), e.getMessage());
            model.addAttribute("error", "Error reading uploaded file.");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }

        if (userLoginService.existsByLogin(user.getLogin())) {
            log.warn("Attempt to register with already existing login: {}", user.getLogin());
            model.addAttribute("error", "A user with this login already exists");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }

        if (user.getBirthYear() < 1900 || user.getBirthYear() > 2020) {
            log.warn("Invalid birth year provided: {}", user.getBirthYear());
            model.addAttribute("error", "Year of birth must be between 1900 and 2020");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }

        List<String> selectedInterests = user.getInterests();
        if (selectedInterests.isEmpty()) {
            log.warn("Registration attempt without selected interests for login={}", user.getLogin());
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
                        user.getLogin(), category.name(), count);
                model.addAttribute("error", "Cannot select more than 3 interests from " + category.getDescription());
                if (photoBytes != null)
                    model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
                return "register";
            }
        }

        if (!user.getPassword().equals(confirm)) {
            log.warn("Password confirmation mismatch for login={}", user.getLogin());
            model.addAttribute("error", "Fields 'Password' and 'Confirm password' are not the same");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }

        User savedUser = userService.saveNewUser(user, photoBytes);
        log.info("User successfully registered: id={}, login={}", savedUser.getId(), savedUser.getLogin());

        return "redirect:/login";
    }

}
