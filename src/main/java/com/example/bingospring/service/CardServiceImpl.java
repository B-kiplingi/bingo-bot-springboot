package com.example.bingospring.service;

import com.example.bingospring.model.Card;
import com.example.bingospring.model.Round;
import com.example.bingospring.repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.example.bingospring.util.CardImageGenerator.generateCardImage;
import static com.example.bingospring.util.ItemPicker.pick;

@Service
public class CardServiceImpl implements CardService {
    private final CardRepository cardRepository;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Override
    public File getCardImage(Card card) {
        return generateCardImage(card.getChecked(), card.getLayout(), Long.toString(card.getUserId()));
    }

    @Override
    public List<Card> findByRoundAndUserId(Round round, long userId) {
        return cardRepository.findByRoundAndUserId(round, userId);
    }

    @Override
    public Card createCard(Round round, long userId) {
        List<String> items = new ArrayList<>(round.getItems());
        String[][] layout = new String[5][5];
        Collections.shuffle(items);
        for (int i = 0; i < 25; i++) {
            layout[i / 5][i % 5] = items.get(i);
        }

        Card card = new Card(round, userId, layout, new boolean[5][5]);
        cardRepository.save(card);
        return card;
    }

    @Override
    public void save(Card card) {
        cardRepository.save(card);
    }

    @Override
    public boolean setChecked(Card card, String coords, boolean checked) {
        int[] xy = parseCoords(coords);

        if (xy == null) {
            return false;
        }

        int x  = xy[0];
        int y = xy[1];

        boolean old = card.isChecked(x,y);
        card.setChecked(x, y, checked);
        return old != checked;
    }

    @Override
    public boolean winning(Card card) {
        boolean[][] checked = card.getChecked();
        for (int i = 0; i < 5; i++) {
            boolean hasWon = true;
            for (int j = 0; j < 5; j++) {
                if (!checked[i][j]) {
                    hasWon = false;
                    break;
                }
            }
            if (hasWon) {return true;}
        }

        for (int i = 0; i < 5; i++) {
            boolean hasWon = true;
            for (int j = 0; j < 5; j++) {
                if (!checked[j][i]) {
                    hasWon = false;
                    break;
                }
            }
            if (hasWon) {return true;}
        }

        boolean hasWon = true;
        for (int i = 0; i < 5; i++) {
            if(!checked[i][i]) {
                hasWon = false;
                break;
            }
        }
        if (hasWon) {return true;}

        hasWon = true;
        for (int i = 0; i < 5; i++) {
            if(!checked[i][4-i]) {
                hasWon = false;
                break;
            }
        }
        return hasWon;
    }

    private static int[] parseCoords(String coord) {
        if (coord == null || coord.length() != 2) {
            return null; // Invalid format
        }

        char letter = coord.toUpperCase().charAt(0);
        char number = coord.charAt(1);

        // Convert A-E to 0-4
        int col = letter - 'A';
        // Convert 1-5 to 0-4
        int row = number - '1';

        // Validate range
        if (col < 0 || col > 4 || row < 0 || row > 4) {
            return null; // Out of bounds
        }

        return new int[]{row, col};
    }
}
