package com.yermaalexx.gate.controller;

import com.yermaalexx.gate.model.Chat;
import com.yermaalexx.gate.model.Message;
import com.yermaalexx.gate.model.UserDTO;
import com.yermaalexx.gate.service.ChatService;
import com.yermaalexx.gate.service.NewMessageService;
import com.yermaalexx.gate.service.UserService;
import lombok.RequiredArgsConstructor;
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
public class ChatController {
    private final UserService userService;
    private final ChatService chatService;
    private final NewMessageService newMessageService;

    @GetMapping
    public String showChat(
            @RequestParam UUID userId,
            @RequestParam UUID otherId,
            Model model
            ) {

        UserDTO user = userService.getUserProfile(userId);
        model.addAttribute("user", user);

        UserDTO other = userService.getUserProfile(otherId);
        model.addAttribute("other", other);

        model.addAttribute("year", Calendar.getInstance().get(Calendar.YEAR));

        Chat chat = chatService.getChat(userId, otherId);
        List<Message> messages = chat.getMessages();
        Collections.reverse(messages);
        model.addAttribute("chatId", chat.getId());
        model.addAttribute("messages", messages);

        newMessageService.deleteNewMessage(userId,otherId);

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

        return "redirect:/chat?userId=" + userId + "&otherId=" + otherId;
    }

}
