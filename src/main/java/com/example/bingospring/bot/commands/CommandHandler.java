package com.example.bingospring.bot.commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public interface CommandHandler {
    void handle(SlashCommandInteractionEvent event);
    String getCommandName();
}