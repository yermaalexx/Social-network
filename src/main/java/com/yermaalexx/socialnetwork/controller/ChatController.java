package com.yermaalexx.socialnetwork.controller;

import com.yermaalexx.socialnetwork.model.Chat;
import com.yermaalexx.socialnetwork.model.Message;
import com.yermaalexx.socialnetwork.model.UserDTO;
import com.yermaalexx.socialnetwork.model.UserLogin;
import com.yermaalexx.socialnetwork.service.ChatService;
import com.yermaalexx.socialnetwork.service.NewMessageService;
import com.yermaalexx.socialnetwork.service.UserService;
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

import java.util.*;

@Controller
@RequestMapping("/chat")
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final UserService userService;
    private final ChatService chatService;
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

        Chat chat = chatService.getChat(userId, otherId);
        List<Message> messages = chat.getMessages();
        Collections.reverse(messages);

        log.debug("Loaded {} messages in chat {} between {} and {}", messages.size(), chat.getId(), userId, otherId);

        model.addAttribute("chatId", chat.getId());
        model.addAttribute("messages", messages);

        newMessageService.deleteNewMessage(userId,otherId);
        log.info("Marked messages as read from {} to {}", otherId, userId);

        return "chat";
    }

    @PostMapping("/send")
    public String sendMessage(
            @RequestParam UUID chatId,
            @RequestParam UUID userId,
            @RequestParam UUID otherId,
            @RequestParam String content
    ) {

        chatService.sendMessage(chatId, userId, otherId, content);
        log.info("User {} sending message to {} in chat {}",
                userId, otherId, chatId);

        return String.format("redirect:/chat?userId=%s&otherId=%s",userId,otherId);
    }

}
