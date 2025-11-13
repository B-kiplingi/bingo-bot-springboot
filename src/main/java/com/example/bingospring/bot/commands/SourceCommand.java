package com.example.bingospring.bot.commands;

import com.example.bingospring.bot.Validator;
import com.example.bingospring.model.Server;
import com.example.bingospring.service.ServerService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class SourceCommand implements CommandHandler {
    Validator validator;
    ServerService serverService;

    @Autowired
    public SourceCommand(Validator validator,  ServerService serverService) {
        this.validator = validator;
        this.serverService = serverService;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String source = event.getOption("channel").getAsString();

        Optional<Server> opt = validator.validateServer(event);
        if (opt.isEmpty()) return;
        Server server = opt.get();

        event.reply("Setting the channel " + source + " as bingo pool source.").queue();

        server.setPoolChannel(source);
        serverService.save(server);
    }

    @Override
    public String getCommandName() {
        return "bingo-source";
    }
}
