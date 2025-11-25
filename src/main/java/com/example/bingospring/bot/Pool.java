package com.example.bingospring.bot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.util.List;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import java.util.Arrays;

public class Pool {

    private static final Gson gson = new Gson();

    public static List<String> loadItemsFromChannel(TextChannel channel) {
        return channel.getIterableHistory().complete().stream()
                .map(Message::getContentRaw)
                .filter(s -> !s.isBlank())
                .flatMap(message -> parseMessage(message).stream())
                .toList();
    }

    private static List<String> parseMessage(String message) {
        message = message.trim();

        // Try to parse JSON array
        if (message.startsWith("[") && message.endsWith("]")) {
            try {
                String[] items = gson.fromJson(message, String[].class);
                return Arrays.stream(items)
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .toList();
            } catch (JsonSyntaxException e) {
                // If JSON parsing fails, fallback to line break splitting
            }
        }

        // Fallback: split by line breaks
        return Arrays.stream(message.split("\\R"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }
}