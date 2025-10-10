package com.example.demoproduit.controller;


import com.example.demoproduit.entities.Game;
import com.example.demoproduit.entities.Move;
import com.example.demoproduit.repository.GameRepository;
import com.example.demoproduit.repository.MoveRepository;
import com.example.demoproduit.repository.UserRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {
    private final GameRepository gameRepo;
    private final MoveRepository moveRepo;
    private final UserRepository userRepo;

    public GameController(GameRepository gameRepo, MoveRepository moveRepo, UserRepository userRepo) {
        this.gameRepo = gameRepo; this.moveRepo = moveRepo; this.userRepo = userRepo;
    }

    @GetMapping("/{gameId}/moves")
    public List<Move> getMoves(@PathVariable Long gameId) {
        Game g = gameRepo.findById(gameId).orElseThrow();
        return moveRepo.findByGameId(g.getId());
    }

    // Optionnel : récupérer les parties d'un user
    @GetMapping("/by-user/{username}")
    public List<Game> getGamesForUser(@PathVariable String username) {
        return gameRepo.findByWhitePlayer_UsernameOrBlackPlayer_Username(username, username);
    }
}