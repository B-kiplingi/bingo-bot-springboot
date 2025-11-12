package com.example.bingospring.bot.commands;

import com.example.bingospring.bot.Pool;
import com.example.bingospring.model.Round;
import com.example.bingospring.model.Server;
import com.example.bingospring.service.RoundService;
import com.example.bingospring.service.ServerService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class StartCommand implements CommandHandler {
    private final ServerService serverService;
    private final RoundService roundService;

    @Autowired
    public StartCommand(ServerService serverService, RoundService roundService) {
        this.serverService = serverService;
        this.roundService = roundService;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        Server server = serverService.getOrCreateServer(event.getGuild().getIdLong());
        String name = server.getPoolChannel();

        System.out.println(name);

        var channels = event.getGuild().getTextChannelsByName(name, true);
        if (channels.isEmpty()) {
            event.reply("❌ No channel named " + name + " found!").setEphemeral(true).queue();
            return;
        }

        List<String> pool = Pool.loadItemsFromChannel(channels.getFirst());

        // Check if enough items loaded
        if (pool.size() < 25) {
            event.reply("❌ Not enough items in pool (need at least 25, found " + pool.size() + ")").setEphemeral(true).queue();
            return;
        }

        Round round = roundService.createRound(server, pool);

        server.setActive(true);
        server.setCurrentRound(round);
        serverService.save(server);

        event.reply("✅ New bingo round started with " + pool.size() + " items! Use `/bingo-join` to get your card!").queue();
    }

    @Override
    public String getCommandName() {
        return "bingo-start";
    }
}
