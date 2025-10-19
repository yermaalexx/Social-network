package com.yermaalexx.gateway.controller;

import com.yermaalexx.gateway.dto.MessageDTO;
import com.yermaalexx.gateway.dto.UserDTO;
import com.yermaalexx.gateway.model.UserLogin;
import com.yermaalexx.gateway.service.MessageService;
import com.yermaalexx.gateway.service.NewMessageService;
import com.yermaalexx.gateway.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final UserService userService;
    private final MessageService messageService;
    private final NewMessageService newMessageService;

    @GetMapping
    public String showChat(
            @RequestParam UUID userId,
            @RequestParam UUID otherId,
            @AuthenticationPrincipal UserLogin userLogin,
            HttpSession session,
            Model model
            ) {
        UUID current = userLogin.getUserId();
        if (!current.equals(userId)) {
            log.warn("Unauthorized chat access attempt: session user={} != param user={}", current, userId);
            return "redirect:/main";
        }

        log.info("User {} opened chat with user {}", userId, otherId);

        @SuppressWarnings("unchecked")
        List<UUID> newMessage = (List<UUID>) session.getAttribute("newMsg");
        if (newMessage != null && newMessage.remove(otherId)) {
            log.debug("Removed user {} from newMsg list for user {}", otherId, userId);
        }

        UserDTO user = userService.getUserProfile(userId);
        UserDTO other = userService.getUserProfile(otherId);
        model.addAttribute("user", user);
        model.addAttribute("other", other);
        model.addAttribute("year", Calendar.getInstance().get(Calendar.YEAR));

        List<MessageDTO> messages = messageService.getChat(userId, otherId);

        log.debug("Loaded {} messages in chat between {} and {}", messages.size(), userId, otherId);

        model.addAttribute("messages", messages);

        newMessageService.deleteNewMessage(userId,otherId);
        log.info("Marked messages as read from {} to {}", otherId, userId);

        return "chat";
    }

    @PostMapping("/send")
    public String sendMessage(
            @RequestParam UUID userId,
            @RequestParam UUID otherId,
            @RequestParam String content
    ) {

        messageService.sendMessage(userId, otherId, content);
        log.info("User {} sending message to {}",
                userId, otherId);

        return String.format("redirect:/chat?userId=%s&otherId=%s",userId,otherId);
    }

}
