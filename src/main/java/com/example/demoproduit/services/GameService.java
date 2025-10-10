package com.example.demoproduit.services;

import com.example.demoproduit.entities.Game;
import com.example.demoproduit.entities.Move;
import com.example.demoproduit.repository.GameRepository;
import com.example.demoproduit.repository.MoveRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final MoveRepository moveRepository;

    public GameService(GameRepository gameRepository, MoveRepository moveRepository) {
        this.gameRepository = gameRepository;
        this.moveRepository = moveRepository;
    }

    // 🔹 Vérifie si les deux joueurs ont accepté et démarre la partie
    public Game startGame(Game game) {
        if (game.getPlayer1() != null && game.getPlayer2() != null) {
            game.setStatus("ONGOING");
            return gameRepository.save(game);
        }
        throw new RuntimeException("Both players must be set to start the game");
    }

    // 🔹 Initialisation du plateau 8x8 (vide)
    public String[][] initializeBoard() {
        String[][] board = new String[8][8];
        for (int i = 0; i < 8; i++)
            for (int j = 0; j < 8; j++)
                board[i][j] = ".";
        return board;
    }

    // 🔹 Sauvegarde un coup
    public Move playMove(Move move) {
        move.setPlayedAt(LocalDateTime.now());
        return moveRepository.save(move);
    }

    // 🔹 Récupère tous les coups d’une partie pour reprise
    public List<Move> resumeGame(Long gameId) {
        return moveRepository.findByGameId(gameId);
    }
}
