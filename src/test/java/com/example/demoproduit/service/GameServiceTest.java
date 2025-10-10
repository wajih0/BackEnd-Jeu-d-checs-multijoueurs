package com.example.demoproduit.service;

import com.example.demoproduit.entities.Game;
import com.example.demoproduit.entities.Move;
import com.example.demoproduit.repository.GameRepository;
import com.example.demoproduit.repository.MoveRepository;
import com.example.demoproduit.services.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

@SpringBootTest
public class GameServiceTest {

    @Autowired
    private GameService gameService;

    @Autowired
    private GameRepository gameRepo;

    @Autowired
    private MoveRepository moveRepo;

    @Test
    public void testStartGameAndBoard() {
        // 🔹 Créer une partie test
        Game game = new Game();
        game.setPlayer1("Alice");
        game.setPlayer2("Bob");
        game = gameRepo.save(game);

        // 🔹 Démarrer la partie
        Game started = gameService.startGame(game);
        assertEquals("ONGOING", started.getStatus());

        // 🔹 Initialiser le plateau
        String[][] board = gameService.initializeBoard();
        assertEquals(8, board.length);
        assertEquals(8, board[0].length);
        System.out.println("Plateau 8x8 initialisé :");
        for(String[] row : board) System.out.println(Arrays.toString(row));
    }

    @Test
    public void testMovePersistenceAndResume() {
        // 🔹 Jouer un coup
        Move move = new Move();
        move.setGameId(1L);
        move.setFromCell("E2");
        move.setToCell("E4");
        move.setPlayer("Alice");
        moveRepo.save(move);

        // 🔹 Vérifier que le coup est en DB
        List<Move> moves = gameService.resumeGame(1L);
        assertFalse(moves.isEmpty());
        assertEquals("E4", moves.get(0).getToCell());
    }
}
