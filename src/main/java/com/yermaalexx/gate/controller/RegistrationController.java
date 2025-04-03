package com.yermaalexx.gate.controller;

import com.yermaalexx.gate.model.Interest;
import com.yermaalexx.gate.model.InterestCategory;
import com.yermaalexx.gate.model.User;
import com.yermaalexx.gate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @ModelAttribute
    public void addInterestsToModel(Model model) {
        model.addAttribute("categoryMap", InterestCategory.getCategoryMap());
    }

    @ModelAttribute(name = "user")
    public User userReturn() {
        return new User();
    }

    @GetMapping
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping
    public String registerUser(User user,
                               @RequestParam("userPhoto") MultipartFile userPhoto,
                               Model model) {
        if (user.getBirthYear() < 1900 || user.getBirthYear() > 2020) {
            model.addAttribute("error", "Year of birth must be between 1900 and 2020");
            return "register";
        }

        byte[] photoBytes = null;
        try {
            if(!userPhoto.isEmpty()) {
                photoBytes = userPhoto.getBytes();
            }
        } catch (IOException e) {
            model.addAttribute("error", "Error reading uploaded file.");
            return "register";
        }

        List<String> selectedInterests = user.getInterests();
        if (selectedInterests.isEmpty()) {
            model.addAttribute("error", "You must choose at least one interest");
            return "register";
        }
        for(InterestCategory category : InterestCategory.values()) {
            long count = Interest.getByCategory(category)
                    .stream()
                    .filter(i -> selectedInterests.contains(i.getName()))
                    .count();
            if(count > 3) {
                model.addAttribute("error", "Cannot select more than 3 interests from " + category.getDescription());
                return "register";
            }

        }

        user.setRegistrationDate(LocalDate.now());

        User savedUser = userService.saveUser(user, photoBytes);
        userService.processUserMatches(savedUser.getId());
        return "redirect:/register/success";
    }

    @GetMapping("/success")
    public String registrationSuccess() {
        return "success";
    }
}
