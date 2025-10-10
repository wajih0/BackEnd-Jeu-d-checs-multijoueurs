package com.example.demoproduit.websocket;



import com.example.demoproduit.entities.Move;
import com.example.demoproduit.repository.MoveRepository;
import com.example.demoproduit.services.GameService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class GameWebSocketHandler extends TextWebSocketHandler {

    private final ObjectMapper mapper = new ObjectMapper();
    private final MoveRepository moveRepository;
    private final GameService gameService;

    // Map<gameId, List of sessions>
    private final Map<Long, List<WebSocketSession>> gameSessions = new ConcurrentHashMap<>();

    public GameWebSocketHandler(MoveRepository moveRepository, GameService gameService) {
        this.moveRepository = moveRepository;
        this.gameService = gameService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long gameId = (Long) session.getAttributes().get("gameId");
        if (gameId != null) {
            gameSessions.computeIfAbsent(gameId, k -> new ArrayList<>()).add(session);

            // 🔹 Envoyer le plateau initial 8x8
            String[][] board = gameService.initializeBoard();
            session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                    "type", "board",
                    "board", board
            ))));

            // 🔹 Envoyer les coups existants pour reprise
            List<Move> moves = gameService.resumeGame(gameId);
            for (Move move : moves) {
                session.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                        "type", "move",
                        "gameId", move.getGameId(),
                        "fromCell", move.getFromCell(),
                        "toCell", move.getToCell(),
                        "player", move.getPlayer()
                ))));
            }
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> payload = mapper.readValue(message.getPayload(), Map.class);
        String type = (String) payload.get("type");

        if ("move".equals(type)) {
            handleMove(payload, session);
        }
    }

    private void handleMove(Map<String, Object> payload, WebSocketSession session) throws Exception {
        Long gameId = ((Number) payload.get("gameId")).longValue();
        String fromCell = (String) payload.get("fromCell");
        String toCell = (String) payload.get("toCell");
        String player = (String) payload.get("player");

        // 🔹 Sauvegarder le coup
        Move move = new Move();
        move.setGameId(gameId);
        move.setFromCell(fromCell);
        move.setToCell(toCell);
        move.setPlayer(player);
        moveRepository.save(move);

        // 🔹 Envoyer le coup à toutes les sessions de la partie
        List<WebSocketSession> sessions = gameSessions.getOrDefault(gameId, Collections.emptyList());
        for (WebSocketSession s : sessions) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                        "type", "move",
                        "gameId", gameId,
                        "fromCell", fromCell,
                        "toCell", toCell,
                        "player", player
                ))));
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        // 🔹 Supprimer la session de toutes les parties
        for (List<WebSocketSession> sessions : gameSessions.values()) {
            sessions.remove(session);
        }
    }
}

