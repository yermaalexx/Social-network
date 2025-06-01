package com.yermaalexx.socialnetwork.controller;

import com.yermaalexx.socialnetwork.config.AppConfig;
import com.yermaalexx.socialnetwork.model.UserDTO;
import com.yermaalexx.socialnetwork.model.UserLogin;
import com.yermaalexx.socialnetwork.service.RejectService;
import com.yermaalexx.socialnetwork.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/main")
@RequiredArgsConstructor
@Slf4j
public class MainPageController {

    private final UserService userService;
    private final RejectService rejectService;
    private final AppConfig appConfig;

    @GetMapping
    public String showMatches(
            HttpSession session,
            @AuthenticationPrincipal UserLogin userLogin,
            Model model
            ) {
        UUID userId = userLogin.getUserId();
        log.info("User {} accessed main page", userId);

        UserDTO user = userService.getUserProfile(userId);
        model.addAttribute("user", user);

        if(session.getAttribute("matchIds") == null) {
            log.debug("Session does not contain match list for user {}. Generating matches...", userId);
            List<UUID>[] usersWithNewMsgAndAll = userService.getUsersSortedByInterestMatch(userId);
            List<UUID> usersWithNewMsg = usersWithNewMsgAndAll[0];
            List<UUID> all = usersWithNewMsgAndAll[1];
            session.setAttribute("matchIds", new ArrayList<>(all));
            session.setAttribute("newMsg", new ArrayList<>(usersWithNewMsg));
            log.info("Generated {} total matches ({} with new messages) for user {}", all.size(), usersWithNewMsg.size(), userId);
        }

        @SuppressWarnings("unchecked")
        List<UUID> ids = (List<UUID>) session.getAttribute("matchIds");

        @SuppressWarnings("unchecked")
        List<UUID> newMessage = (List<UUID>) session.getAttribute("newMsg");

        List<UUID> start = ids.stream().limit(appConfig.getCardsOnPage()).toList();

        List<UserDTO> dtos = userService.getUserMatchedDTOs(start, userId);

        model.addAttribute("matches", dtos);
        model.addAttribute("newMessage", newMessage);
        model.addAttribute("userId", userId);
        model.addAttribute("offset", dtos.size());
        model.addAttribute("year", Calendar.getInstance().get(Calendar.YEAR));

        log.debug("Loaded {} user cards for user {}", dtos.size(), userId);
        return "main";
    }

    @GetMapping("/renew")
    public String renew(HttpSession session,
                        @AuthenticationPrincipal UserLogin userLogin) {
        session.setAttribute("matchIds", null);
        log.info("Match list for userId={} was reset via /main/renew", userLogin.getUserId());

        return "redirect:/main";
    }

    @GetMapping("/more")
    @ResponseBody
    public List<UserDTO> loadMore(
            @RequestParam UUID userId,
            @RequestParam int offset,
            @AuthenticationPrincipal UserLogin userLogin,
            HttpSession session
    ) {
        UUID current = userLogin.getUserId();
        if (!current.equals(userId)) {
            log.warn("Unauthorized loadMore attempt: session user {} != param user {}",
                    current, userId);
            return new ArrayList<>();
        }

        @SuppressWarnings("unchecked")
        List<UUID> ids = (List<UUID>) session.getAttribute("matchIds");

        List<UUID> slice = ids.stream()
                .skip(offset)
                .limit(appConfig.getCardsOnPage())
                .toList();

        log.debug("User {} is loading more matches from offset {} ({} items)",
                userId, offset, slice.size());
        return userService.getUserMatchedDTOs(slice, userId);
    }

    @PostMapping("/reject")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void reject(
            @RequestParam UUID userId,
            @RequestParam UUID matchedId,
            @AuthenticationPrincipal UserLogin userLogin,
            HttpSession session
    ) {

        UUID current = userLogin.getUserId();
        if (!current.equals(userId)) {
            log.warn("Unauthorized reject attempt: session user {} != param user {}", current, userId);
            return;
        }

        log.info("User {} rejected user {}", userId, matchedId);
        rejectService.reject(userId, matchedId);

        @SuppressWarnings("unchecked")
        List<UUID> ids = (List<UUID>) session.getAttribute("matchIds");
        ids.remove(matchedId);
        log.debug("Removed user {} from session match list for user {}", matchedId, userId);
    }



}
