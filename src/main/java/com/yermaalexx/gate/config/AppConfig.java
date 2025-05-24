package com.yermaalexx.gate.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    private int cardsOnPage;
    private int maxMessagesInChat;
}
