package com.yermaalexx.gateway.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@Data
@ConfigurationProperties(prefix = "app")
public class AppConfig {
    /* Скільки користувачів зі співпадаючими інтересами відображається на головній сторінці.
    * Стільки ж додається кожного разу при натисканні кнопки Add more */
    private int cardsOnPage;

    /* Максимальна кількість повідомлень в чаті. Якщо більше - то найстаріше повідомлення видаляється */
    private int maxMessagesInChat;
}
