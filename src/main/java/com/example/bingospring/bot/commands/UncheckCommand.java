package com.example.bingospring.bot.commands;


import com.example.bingospring.bot.Validator;
import com.example.bingospring.model.Card;
import com.example.bingospring.model.Round;
import com.example.bingospring.model.Server;
import com.example.bingospring.service.CardService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Optional;

@Component
public class UncheckCommand implements CommandHandler {
    Validator validator;
    CardService cardService;

    public UncheckCommand(Validator validator, CardService cardService) {
        this.validator = validator;
        this.cardService = cardService;
    }

    @Override
    public void handle(SlashCommandInteractionEvent event) {
        String item = event.getOption("item").getAsString();

        Optional<Validator.FullContext> opt = validator.validateCard(event);
        if (opt.isEmpty()) return;
        Validator.FullContext context = opt.get();

        Server server = context.server();
        Round round = context.round();
        Card card = context.card();

        boolean winner = cardService.winning(card);

        if (!cardService.setChecked(card, item, false)) {
            event.reply("`" + item + "` isn't on your card, or is already unchecked.").setEphemeral(true).queue();
            return;
        }

        if(winner && !cardService.winning(card)) {
            event.getChannel().sendMessage("Newer mind, looks like " + event.getUser().getAsMention() + " is about as reliable as České Dráhy's timetable").queue();

        }

        if (card.getMessageId() == null || card.getChannelId() == null) {
            System.out.println("no message found");
            return;
        }

        System.out.println("editing message");
        File image = cardService.getCardImage(card);

        event.getChannel().retrieveMessageById(card.getMessageId())
                .queue(
                        msg -> msg.editMessage("Here's your Bingo card!")
                                .setFiles(net.dv8tion.jda.api.utils.FileUpload.fromData(image, event.getUser().getIdLong() + ".png"))
                                .queue(
                                        success -> System.out.println("Message edited!"),
                                        error -> System.out.println("Edit failed: " + error.getMessage())
                                ),
                        error -> System.out.println("Retrieve failed") // Ignore if message was deleted
                );

        event.reply("Looks like your check arrived at the wrong platform, we've corrected the issue.").setEphemeral(true).queue();

        cardService.save(card);
    }

    @Override
    public String getCommandName() {
        return "bingo-uncheck";
    }
}
