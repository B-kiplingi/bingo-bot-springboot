package com.example.bingospring.bot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.requests.GatewayIntent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdaConfig {

    @Value("${discord.token}")
    private String token;

    private final BotListener listener;

    public JdaConfig(BotListener listener) {
        this.listener = listener;
    }

    @Bean
    public JDA jda() throws InterruptedException {
        System.out.println("Starting JDA...");

        JDA jda = JDABuilder.createDefault(token)
                .addEventListeners(listener)
                .enableIntents(GatewayIntent.MESSAGE_CONTENT)
                .build();

        jda.awaitReady();
        System.out.println("JDA is ready!");
        return jda;
    }
}
