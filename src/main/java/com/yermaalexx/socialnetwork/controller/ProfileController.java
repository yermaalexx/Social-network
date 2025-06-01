package com.yermaalexx.socialnetwork.controller;

import com.yermaalexx.socialnetwork.model.Interest;
import com.yermaalexx.socialnetwork.model.InterestCategory;
import com.yermaalexx.socialnetwork.model.UserDTO;
import com.yermaalexx.socialnetwork.model.UserLogin;
import com.yermaalexx.socialnetwork.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
        log.info("User {} accessed profile page", userId);

        UserDTO userAfter = userService.getUserProfile(userId);
        model.addAttribute("userAfter", userAfter);

        return "profile";
    }

    @PostMapping
    public String saveProfile(@ModelAttribute("userAfter") UserDTO userAfter,
                              @RequestParam(value = "userPhoto", required = false) MultipartFile userPhoto,
                              @RequestParam(value = "removePhoto", required = false) String removePhoto,
                              Model model,
                              HttpSession session) throws Exception {

        UUID userId = userAfter.getId();
        log.info("User {} submitted profile update", userId);

        if (userPhoto!=null && !userPhoto.isEmpty()) {
            String base64 = Base64.getEncoder().encodeToString(userPhoto.getBytes());
            userAfter.setPhotoBase64(base64);
            log.debug("User {} uploaded new profile photo, size={} Kb", userId, userPhoto.getSize()/1024);
        }

        if ("true".equals(removePhoto)) {
            userAfter.setPhotoBase64(null);
            log.info("User {} requested to remove profile photo", userId);
        }

        if (userAfter.getBirthYear() < 1900 || userAfter.getBirthYear() > 2020) {
            log.warn("User {} entered invalid birth year: {}", userId, userAfter.getBirthYear());
            model.addAttribute("error", "Year of birth must be between 1900 and 2020");
            return "profile";
        }

        List<String> selectedInterests = userAfter.getMatchingInterests();
        if (selectedInterests.isEmpty()) {
            log.warn("User {} submitted profile update with no interests selected", userId);
            model.addAttribute("error", "You must choose at least one interest");
            return "profile";
        }

        for(InterestCategory category : InterestCategory.values()) {
            long count = Interest.getByCategory(category)
                    .stream()
                    .filter(i -> selectedInterests.contains(i.getName()))
                    .count();
            if(count > 3) {
                log.warn("User {} selected {} interests from category '{}'", userId, count, category.name());
                model.addAttribute("error", "Cannot select more than 3 interests from " + category.getDescription());
                return "profile";
            }
        }

        userService.updateUser(userAfter, session);
        log.info("User {} successfully updated their profile", userId);

        return "redirect:/main";
    }

}
