package com.example.bingospring.bot;

import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class CommandRegistrar extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        var jda = event.getJDA();

        jda.updateCommands()    // GLOBAL
                .addCommands(
                        Commands.slash("bingo-start", "Start a new bingo round"),
                        Commands.slash("bingo-join", "Join the current bingo round"),
                        Commands.slash("bingo-card", "Show your bingo card"),
                        Commands.slash("bingo-check", "Mark an item on your bingo card")
                                .addOption(OptionType.STRING, "item", "The item coordinates to mark, e.g. B3", true),
                        Commands.slash("bingo-uncheck", "Undo marking a field, e.g. B3")
                                .addOption(OptionType.STRING, "item", "The item coordinates to unmark", true),
                        Commands.slash("bingo-source", "Choose which channel to load the pool from")
                                .addOption(OptionType.STRING, "channel", "The channel to load the pool from", true),
                        Commands.slash("bingo-pool-size", "Set the round pool size.")
                                .addOption(OptionType.INTEGER, "pool-size", "The number of items to pick from the pool channel when creating a round, must be at least 25.", true)
                )
                .queue(
                        success -> System.out.println("Global commands updated."),
                        error -> System.err.println("Error updating commands: " + error.getMessage())
                );
    }
}