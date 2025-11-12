package com.example.bingospring.model;

import com.example.bingospring.util.BooleanArrayConverter;
import com.example.bingospring.util.StringArrayConverter;
import com.google.gson.Gson;
import jakarta.persistence.*;

@Entity
@Table
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Round round;

    private long userId;
    private String messageId;
    private String channelId;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = StringArrayConverter.class)
    private String[][] layout;

    @Column(columnDefinition = "TEXT")
    @Convert(converter = BooleanArrayConverter.class)
    private boolean[][] checked;

    protected Card() {}

    public Card(Round round, long userId, String[][] layout, boolean[][] checked) {
        this.round = round;
        this.userId = userId;
        this.layout = layout;
        this.checked = checked;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public Round getRound() {
        return round;
    }

    public void setRound(Round round) {
        this.round = round;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getChannelId() {
        return channelId;
    }

    public void setChannelId(String channelId) {
        this.channelId = channelId;
    }

    public String[][] getLayout() {
        return layout;
    }

    public void setLayout(String[][] layout) {
        this.layout = layout;
    }

    public boolean[][] getChecked() {
        return checked;
    }

    public void setChecked(int x, int y, boolean checked) {
        this.checked[x][y] = checked;
    }

    public boolean isChecked(int x, int y) {
        return this.checked[x][y];
    }
}