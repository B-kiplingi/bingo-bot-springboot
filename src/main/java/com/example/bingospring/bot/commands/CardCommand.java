package com.example.bingospring.bot.commands;

import com.example.bingospring.bot.Validator;
import com.example.bingospring.model.Card;
import com.example.bingospring.model.Round;
import com.example.bingospring.model.Server;
import com.example.bingospring.service.CardService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.Optional;

@Component
public class CardCommand implements CommandHandler {
    Validator validator;
    CardService cardService;

    @Autowired
    public CardCommand(Validator validator,  CardService cardService) {
        this.validator = validator;
        this.cardService = cardService;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        Optional<Validator.FullContext> opt = validator.validateCard(event);
        if (opt.isEmpty()) return;
        Validator.FullContext context = opt.get();

        File image = cardService.getCardImage(context.card());
        event.replyFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(image, event.getUser().getIdLong() + ".png"))
                .setContent("Here’s your Bingo card!")
                .queue(response -> {
                    // Store the message ID after sending
                    response.retrieveOriginal().queue(message -> {
                        context.card().setMessageId(message.getId());
                        context.card().setChannelId(message.getChannel().getId());
                        cardService.save(context.card());
                    });
                });
    }

    @Override
    public String getCommandName() {
        return "bingo-card";
    }
}
