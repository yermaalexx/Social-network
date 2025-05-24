package com.yermaalexx.gate.controller;

import com.yermaalexx.gate.config.AppConfig;
import com.yermaalexx.gate.model.UserDTO;
import com.yermaalexx.gate.model.UserLogin;
import com.yermaalexx.gate.service.MatchService;
import com.yermaalexx.gate.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
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
public class MainPageController {

    private final UserService userService;
    private final MatchService matchService;
    private final AppConfig appConfig;

    @GetMapping
    public String showMatches(
            HttpSession session,
            @AuthenticationPrincipal UserLogin userLogin,
            Model model
            ) {
        UUID userId = userLogin.getUserId();
        UserDTO user = userService.getUserProfile(userId);
        model.addAttribute("user", user);

        if(session.getAttribute("matchIds") == null) {
            List<UUID>[] usersWithNewMsgAndAll = userService.getUsersSortedByInterestMatch(userId);
            List<UUID> usersWithNewMsg = usersWithNewMsgAndAll[0];
            List<UUID> all = usersWithNewMsgAndAll[1];
            session.setAttribute("matchIds", new ArrayList<>(all));
            session.setAttribute("newMsg", new ArrayList<>(usersWithNewMsg));
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
        return "main";
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
        if (!current.equals(userId))
            return new ArrayList<>();

        @SuppressWarnings("unchecked")
        List<UUID> ids = (List<UUID>) session.getAttribute("matchIds");

        List<UUID> slice = ids.stream()
                .skip(offset)
                .limit(appConfig.getCardsOnPage())
                .toList();

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
        if (!current.equals(userId))
            return;

        matchService.reject(userId, matchedId);

        @SuppressWarnings("unchecked")
        List<UUID> ids = (List<UUID>) session.getAttribute("matchIds");

        ids.remove(matchedId);
    }



}
