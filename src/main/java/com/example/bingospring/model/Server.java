package com.example.bingospring.model;

import jakarta.persistence.*;

@Entity
@Table
public class Server {
    @Id
    private Long id;

    private String poolChannel;

    @OneToOne
    private Round currentRound;
    private boolean active;
    private int poolSize;

    protected Server() {}

    public Server(Long id) {
        this.id = id;
        poolChannel = "pool";
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public String getPoolChannel() {
        return poolChannel;
    }

    public void setPoolChannel(String poolChannel) {
        this.poolChannel = poolChannel;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Round currentRound) {
        this.currentRound = currentRound;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
}
