package com.example.bingospring.service;

import com.example.bingospring.model.Card;
import com.example.bingospring.model.Round;

import java.io.File;
import java.util.List;

public interface CardService {
    File getCardImage(Card card);
    List<Card> findByRoundAndUserId(Round round, long userId);
    Card createCard(Round round, long userId);
    boolean winning(Card card);
    void save(Card card);
    boolean setChecked(Card card, String coords, boolean checked);
}
