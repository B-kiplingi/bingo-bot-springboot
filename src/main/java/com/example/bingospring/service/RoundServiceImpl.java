package com.example.bingospring.service;

import com.example.bingospring.model.Card;
import com.example.bingospring.model.Round;
import com.example.bingospring.model.Server;
import com.example.bingospring.repository.RoundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class RoundServiceImpl implements RoundService {
    private final RoundRepository roundRepository;

    @Autowired
    public RoundServiceImpl(RoundRepository roundRepository) {
        this.roundRepository = roundRepository;
    }

    @Override
    public Round createRound(Server server, List<String> pool) {
        Round round = new Round();
        round.setItems(pick(pool, 25));
        round.setServer(server);
        roundRepository.save(round);
        return round;
    }

    @Override
    public void save(Round round) {
        roundRepository.save(round);
    }

    private static List<String> pick(List<String> pool, int amount) {
        if (pool.size() < amount) {
            throw new IllegalArgumentException("Pool too small");
        }
        List<String> result = new ArrayList<>();

        List<String> shuffledPool = new ArrayList<>(pool);

        Collections.shuffle(shuffledPool);

        for (int i = 0; i < amount; i++) {
            result.add(shuffledPool.get(i));
        }
        return result;
    }
}