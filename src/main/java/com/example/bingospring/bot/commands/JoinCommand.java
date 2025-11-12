package com.example.bingospring.bot.commands;

import com.example.bingospring.bot.Validator;
import com.example.bingospring.model.Card;
import com.example.bingospring.model.Round;
import com.example.bingospring.model.Server;
import com.example.bingospring.service.CardService;
import com.example.bingospring.service.ServerService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Component
public class JoinCommand implements CommandHandler {
    CardService cardService;
    Validator validator;

    @Autowired
    public JoinCommand(CardService cardService,  Validator validator) {
        this.cardService = cardService;
        this.validator = validator;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        Optional<Validator.ServerRoundContext> opt = validator.validateRound(event);
        if (opt.isEmpty()) return;

        Validator.ServerRoundContext context = opt.get();

        if (!cardService.findByRoundAndUserId(context.round(), event.getUser().getIdLong()).isEmpty()) {
            event.reply("You already joined this round!").setEphemeral(true).queue();
            return;
        }

        Card card = cardService.createCard(context.round(),event.getUser().getIdLong());

        File image = cardService.getCardImage(card);
        event.replyFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(image, event.getUser().getIdLong() + ".png"))
                .setContent("Here’s your Bingo card!")
                .queue(response -> {
                    // Store the message ID after sending
                    response.retrieveOriginal().queue(message -> {
                        card.setMessageId(message.getId());
                        card.setChannelId(message.getChannel().getId());
                        cardService.save(card);
                    });
                });
    }

    @Override
    public String getCommandName() {
        return "bingo-join";
    }
}
