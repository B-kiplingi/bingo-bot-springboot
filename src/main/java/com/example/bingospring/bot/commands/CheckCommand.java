package com.example.bingospring.bot.commands;

import com.example.bingospring.bot.Validator;
import com.example.bingospring.model.Card;
import com.example.bingospring.model.Round;
import com.example.bingospring.model.Server;
import com.example.bingospring.service.CardService;
import com.example.bingospring.service.RoundService;
import com.example.bingospring.service.ServerService;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Optional;

@Component
public class CheckCommand implements CommandHandler {
    Validator validator;
    CardService cardService;
    RoundService roundService;
    ServerService serverService;

    @Autowired
    public CheckCommand(Validator validator, CardService cardService,  RoundService roundService, ServerService serverService) {
        this.validator = validator;
        this.cardService = cardService;
        this.roundService = roundService;
        this.serverService = serverService;
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


        if(!cardService.setChecked(card, item, true)) {
            event.reply("Already checked or invalid coordinates!").setEphemeral(true).queue();
            return;
        }

        System.out.println("card: " + card.getMessageId() + card.getChannelId() + card.getRound());

        //check for the card channel and message id
        if (card.getMessageId() == null || card.getChannelId() == null) {
            System.out.println("No message id or channel id provided!");
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

        //the user hasn't won yet
        if (!cardService.winning(card)) {
            event.reply("Marked **" + item + "**!").setEphemeral(true).queue();
            cardService.save(card);
            return;
        }

        //completed, end round if not ended already
        if(server.isActive()) {
            server.setActive(false);
            server.setCurrentRound(null);

            //announce
            File externalFile = new File("/app/data/winner.jpg");
            try {
                InputStream stream;

                if (externalFile.exists()) {
                    // Load from external directory
                    stream = new FileInputStream(externalFile);
                } else {
                    // Fall back to classpath resource
                    stream = getClass().getResourceAsStream("/res/winner.jpg");
                }

                if (stream == null) {
                    event.reply("Congratulation, you have been české-dráhyed the most!").queue();
                } else {
                    try (stream) {
                        byte[] bytes = stream.readAllBytes();
                        event.reply("congratulation, you have been české-dráhyed the most!")
                                .addFiles(FileUpload.fromData(bytes, "win.jpg"))
                                .queue();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //round inactive -> someone else already won
        else {
            try {
                String user = event.getJDA().getUserById(round.getWinnerId()).getAsMention();
                event.reply(user + " got české-dráhyed harder than you and already won.").setEphemeral(true).queue();
            } catch (Exception e) {
                e.printStackTrace();
            }
            event.reply("Somebody already beat you to it, but české dráhy is not sure who... we're still looking for him");
        }
        cardService.save(card);
        roundService.save(round);
        serverService.save(server);
    }

    @Override
    public String getCommandName() {
        return "bingo-check";
    }
}
