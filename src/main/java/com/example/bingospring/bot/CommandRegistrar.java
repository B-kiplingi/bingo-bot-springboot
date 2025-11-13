package com.example.bingospring.bot;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class CommandRegistrar extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        var jda = event.getJDA();

        // Global commands
        jda.upsertCommand("bingo-start", "Start a new bingo round").queue();
        jda.upsertCommand("bingo-join", "Join the current bingo round").queue();
        jda.upsertCommand("bingo-card", "Show your bingo card").queue();
        jda.upsertCommand("bingo-check", "Mark an item on your bingo card")
                .addOptions(new OptionData(OptionType.STRING, "item", "The item coordinates to mark", true))
                .queue();
        jda.upsertCommand("bingo-uncheck", "Made a mistake? you can uncheck a field with this command")
                .addOptions(new OptionData(OptionType.STRING, "item", "The item coordinates to unmark", true))
                .queue();
        jda.upsertCommand("bingo-source", "Choose which channel to load the pool from")
                .addOptions(new OptionData(OptionType.STRING, "channel", "The channel to load the pool from", true))
                .queue(
                        success -> System.out.println("bingo-source command registered successfully"),
                        error -> System.out.println("Error registering bingo-source command: " + error.getMessage())
                );

        System.out.println("Commands registered!");
    }
}