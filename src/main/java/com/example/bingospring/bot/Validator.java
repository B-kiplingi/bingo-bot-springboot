package com.example.bingospring.bot;

import com.example.bingospring.model.Card;
import com.example.bingospring.model.Round;
import com.example.bingospring.model.Server;
import com.example.bingospring.service.CardService;
import com.example.bingospring.service.ServerService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class Validator {
    private final ServerService serverService;
    private final CardService cardService;

    public Validator(ServerService serverService, CardService cardService) {
        this.serverService = serverService;
        this.cardService = cardService;
    }

    public Optional<Server> validateServer(SlashCommandInteractionEvent event) {
        Server server = serverService.getServer(event.getGuild().getIdLong());
        if (server == null) {
            event.reply("Bingo hasn't been initiated yet!").setEphemeral(true).queue();
            return Optional.empty();
        }

        return Optional.of(server);
    }

    public Optional<ServerRoundContext> validateRound(SlashCommandInteractionEvent event) {
        Optional<Server> opt = validateServer(event);
        if (opt.isEmpty()) return Optional.empty();
        Server server = opt.get();

        Round round = server.getCurrentRound();
        if (round == null) {
            event.reply("No active round.").setEphemeral(true).queue();
            return Optional.empty();
        }

        if (!server.isActive()) {
            event.reply("This round already ended!").setEphemeral(true).queue();
            return Optional.empty();
        }

        return Optional.of(new ServerRoundContext(server, round));
    }

    public Optional<FullContext> validateCard(SlashCommandInteractionEvent event) {
        Optional<ServerRoundContext> opt = validateRound(event);
        if (opt.isEmpty()) return Optional.empty();
        ServerRoundContext context = opt.get();

        List<Card> cards = cardService.findByRoundAndUserId(context.round, event.getUser().getIdLong());
        if (cards.isEmpty()) {
            event.reply("You haven’t joined this round yet.").setEphemeral(true).queue();
            return Optional.empty();
        }

        Card card = cards.getFirst();

        return Optional.of(new FullContext(context.server, context.round, card));
    }

    public record ServerRoundContext(Server server, Round round) {}
    public record FullContext(Server server, Round round, Card card) {}
}
