package com.example.bingospring.bot;

import com.example.bingospring.bot.commands.CommandHandler;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class BotListener extends ListenerAdapter {
    private static final Logger log = LoggerFactory.getLogger(BotListener.class);

    private final Map<String, CommandHandler> commandHandlers;

    public BotListener(List<CommandHandler> handlers) {
        // Spring auto-injects all CommandHandler implementations!
        this.commandHandlers = handlers.stream()
                .collect(Collectors.toMap(CommandHandler::getCommandName, Function.identity()));

        log.info("Registered {} command handlers: {}",
                commandHandlers.size(),
                commandHandlers.keySet());
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        CommandHandler handler = commandHandlers.get(commandName);

        if (handler == null) {
            log.warn("No handler found for command: {}", commandName);
            event.reply("Unknown command!").setEphemeral(true).queue();
            return;
        }

        try {
            log.debug("Handling command {} from user {} in guild {}",
                    commandName,
                    event.getUser().getId(),
                    event.getGuild().getId());

            handler.handle(event);

        } catch (Exception e) {
            log.error("Error handling command {}: {}", commandName, e.getMessage(), e);
            event.reply("❌ An error occurred while processing your command. Please try again.")
                    .setEphemeral(true)
                    .queue();
        }
    }
}