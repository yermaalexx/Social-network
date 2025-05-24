package com.yermaalexx.gate.controller;

import com.yermaalexx.gate.model.Interest;
import com.yermaalexx.gate.model.InterestCategory;
import com.yermaalexx.gate.model.User;
import com.yermaalexx.gate.service.UserLoginService;
import com.yermaalexx.gate.service.UserService;
import lombok.RequiredArgsConstructor;
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
public class RegistrationController {

    private final UserService userService;
    private final UserLoginService userLoginService;
    private byte[] photoBytes;

    @ModelAttribute
    public void addInterestsToModel(Model model) {
        model.addAttribute("categoryMap", InterestCategory.getCategoryMap());
    }

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping
    public String registerUser(User user,
                               @RequestParam("userPhoto") MultipartFile userPhoto,
                               @RequestParam String confirm,
                               Model model) {

        try {
            if(userPhoto!=null && !userPhoto.isEmpty()) {
                photoBytes = userPhoto.getBytes();
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            }
        } catch (IOException e) {
            model.addAttribute("error", "Error reading uploaded file.");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }

        if (userLoginService.existsByLogin(user.getLogin())) {
            model.addAttribute("error", "A user with this login already exists");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }

        if (user.getBirthYear() < 1900 || user.getBirthYear() > 2020) {
            model.addAttribute("error", "Year of birth must be between 1900 and 2020");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }

        List<String> selectedInterests = user.getInterests();
        if (selectedInterests.isEmpty()) {
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
                model.addAttribute("error", "Cannot select more than 3 interests from " + category.getDescription());
                if (photoBytes != null)
                    model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
                return "register";
            }
        }

        if (!user.getPassword().equals(confirm)) {
            model.addAttribute("error", "Fields 'Password' and 'Confirm password' are not the same");
            if (photoBytes != null)
                model.addAttribute("photoBase64", Base64.getEncoder().encodeToString(photoBytes));
            return "register";
        }

        User savedUser = userService.saveNewUser(user, photoBytes);

        userService.processUserMatches(savedUser);

        return "redirect:/login";
    }

}
