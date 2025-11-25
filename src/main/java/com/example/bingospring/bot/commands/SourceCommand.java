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
    ServerService serverService;

    @Autowired
    public SourceCommand(Validator validator,  ServerService serverService) {
        this.serverService = serverService;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String source = event.getOption("channel").getAsString();

        Server server = serverService.getOrCreateServer(event.getGuild().getIdLong());

        event.reply("Setting the channel " + source + " as bingo pool source.").queue();

        server.setPoolChannel(source);
        serverService.save(server);
    }

    @Override
    public String getCommandName() {
        return "bingo-source";
    }
}
