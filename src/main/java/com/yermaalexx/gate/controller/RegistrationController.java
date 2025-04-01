package com.yermaalexx.gate.controller;

import com.yermaalexx.gate.model.Interest;
import com.yermaalexx.gate.model.InterestCategory;
import com.yermaalexx.gate.model.User;
import com.yermaalexx.gate.repository.UserRepository;
import com.yermaalexx.gate.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegistrationController {

    private final UserService userService;

    @GetMapping
    public String showRegistrationForm(Model model) {
        model.addAttribute("categoryMap", InterestCategory.getCategoryMap());
        return "register";
    }

    @PostMapping
    public String registerUser(@RequestParam String name,
                               @RequestParam int birthYear,
                               @RequestParam String location,
                               @RequestParam("userPhoto") MultipartFile userPhoto,
                               @RequestParam Map<String, String> params,
                               Model model) {
        if (birthYear < 1900 || birthYear > 2020) {
            model.addAttribute("error", "Year of birth must be between 1900 and 2020");
            model.addAttribute("categoryMap", InterestCategory.getCategoryMap());
            return "register";
        }

        byte[] photoBytes = null;
        try {
            if(!userPhoto.isEmpty()) {
                photoBytes = userPhoto.getBytes();
            }
        } catch (IOException e) {
            model.addAttribute("error", "Error reading uploaded file.");
            model.addAttribute("categoryMap", InterestCategory.getCategoryMap());
            return "register";
        }

        List<String> selectedInterests = new ArrayList<>();

        for(InterestCategory category : InterestCategory.values()) {
            List<Interest> interestsInCategory = Interest.getByCategory(category);
            List<Interest> selected = interestsInCategory.stream()
                    .filter(i -> params.containsKey(i.name()))
                    .toList();
            if(selected.size() > 3) {
                model.addAttribute("error", "Cannot select more than 3 interests from " + category.getDescription());
                model.addAttribute("categoryMap", InterestCategory.getCategoryMap());
                return "register";
            }
            selectedInterests.addAll(selected.stream().map(Interest::getName).toList());
        }

        if (selectedInterests.isEmpty()) {
            model.addAttribute("error", "You must choose at least one interest");
            model.addAttribute("categoryMap", InterestCategory.getCategoryMap());
            return "register";
        }

        User user = new User();
        user.setName(name);
        user.setBirthYear(birthYear);
        user.setLocation(location);
        user.setInterests(selectedInterests);

        User savedUser = userService.saveUser(user, photoBytes);
        userService.processUserMatches(savedUser.getId());
        return "redirect:/register/success";
    }

    @GetMapping("/success")
    public String registrationSuccess() {
        return "success";
    }
}
