package com.example.bingospring.service;

import com.example.bingospring.model.Round;
import com.example.bingospring.model.Server;

import java.util.List;

public interface RoundService {
    Round createRound(Server server, List<String> pool);
    void save(Round round);
}