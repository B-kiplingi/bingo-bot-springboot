package com.example.bingospring.bot.commands;

import com.example.bingospring.bot.Validator;
import com.example.bingospring.model.Server;
import com.example.bingospring.service.ServerService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PoolSizeCommand implements CommandHandler {
    Validator validator;
    ServerService serverService;

    @Autowired
    public PoolSizeCommand(Validator validator,  ServerService serverService) {
        this.validator = validator;
        this.serverService = serverService;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        int size = event.getOption("pool-size").getAsInt();
        Server server = serverService.getOrCreateServer(event.getGuild().getIdLong());

        if (size < 25) {
            event.reply("The pool size must be at least 25").setEphemeral(true).queue();
            return;
        }

        server.setPoolSize(size);
        serverService.save(server);
        event.reply(String.format("The pool size has been set to: %d", size)).queue();
    }

    @Override
    public String getCommandName() {
        return "bingo-pool-size";
    }
}