package com.example.demoproduit.repository;

import com.example.demoproduit.entities.Game;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByWhitePlayer_UsernameOrBlackPlayer_Username(String username1, String username2);

}
