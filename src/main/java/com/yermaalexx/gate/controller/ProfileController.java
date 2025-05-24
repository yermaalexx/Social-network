package com.yermaalexx.gate.controller;

import com.yermaalexx.gate.model.Interest;
import com.yermaalexx.gate.model.InterestCategory;
import com.yermaalexx.gate.model.UserDTO;
import com.yermaalexx.gate.model.UserLogin;
import com.yermaalexx.gate.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;

    @ModelAttribute
    public void addInterestsToModel(Model model) {
        model.addAttribute("categoryMap", InterestCategory.getCategoryMap());
    }

    @GetMapping
    public String showProfile(
            @AuthenticationPrincipal UserLogin userLogin,
            Model model
    ) {
        UUID userId = userLogin.getUserId();
        UserDTO userAfter = userService.getUserProfile(userId);
        model.addAttribute("userAfter", userAfter);

        return "profile";
    }

    @PostMapping
    public String saveProfile(@ModelAttribute("userAfter") UserDTO userAfter,
                              @RequestParam(value = "userPhoto", required = false) MultipartFile userPhoto,
                              @RequestParam(value = "removePhoto", required = false) String removePhoto,
                              Model model) throws Exception {

        if (userPhoto!=null && !userPhoto.isEmpty()) {
            String base64 = Base64.getEncoder().encodeToString(userPhoto.getBytes());
            userAfter.setPhotoBase64(base64);
        }

        if (removePhoto!=null && removePhoto.equals("true"))
            userAfter.setPhotoBase64(null);

        if (userAfter.getBirthYear() < 1900 || userAfter.getBirthYear() > 2020) {
            model.addAttribute("error", "Year of birth must be between 1900 and 2020");
            return "profile";
        }

        List<String> selectedInterests = userAfter.getMatchingInterests();
        if (selectedInterests.isEmpty()) {
            model.addAttribute("error", "You must choose at least one interest");
            return "profile";
        }

        for(InterestCategory category : InterestCategory.values()) {
            long count = Interest.getByCategory(category)
                    .stream()
                    .filter(i -> selectedInterests.contains(i.getName()))
                    .count();
            if(count > 3) {
                model.addAttribute("error", "Cannot select more than 3 interests from " + category.getDescription());
                return "profile";
            }
        }

        userService.updateUser(userAfter);

        return "redirect:/main";
    }

}
