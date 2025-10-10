package com.example.demoproduit.entities;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Move {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long gameId;
    private String fromCell; // ex: "E2"
    private String toCell;   // ex: "E4"
    private String player;
    private LocalDateTime playedAt = LocalDateTime.now();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId) {
        this.gameId = gameId;
    }

    public String getToCell() {
        return toCell;
    }

    public void setToCell(String toCell) {
        this.toCell = toCell;
    }

    public String getFromCell() {
        return fromCell;
    }

    public void setFromCell(String fromCell) {
        this.fromCell = fromCell;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    public void setPlayedAt(LocalDateTime playedAt) {
        this.playedAt = playedAt;
    }
}