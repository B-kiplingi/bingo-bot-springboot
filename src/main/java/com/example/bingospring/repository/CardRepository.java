package com.example.bingospring.repository;

import com.example.bingospring.model.Card;
import com.example.bingospring.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CardRepository extends JpaRepository<Card, Long> {
    List<Card> findByRoundAndUserId(Round round, long userId);
}
