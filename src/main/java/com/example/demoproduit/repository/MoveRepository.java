package com.example.demoproduit.repository;

import com.example.demoproduit.entities.Game;
import com.example.demoproduit.entities.Move;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MoveRepository  extends JpaRepository<Move,Long> {
    List<Move> findByGameId(Long gameId);
}
