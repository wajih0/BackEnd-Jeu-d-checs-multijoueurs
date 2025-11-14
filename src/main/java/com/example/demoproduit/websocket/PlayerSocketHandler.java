package com.example.demoproduit.websocket;


import com.example.demoproduit.entities.Game;
import com.example.demoproduit.entities.Move;
import com.example.demoproduit.repository.GameRepository;
import com.example.demoproduit.repository.MoveRepository;
import com.example.demoproduit.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.*;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class PlayerSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private final UserRepository userRepo;
    private final GameRepository gameRepo;
    private final MoveRepository moveRepo;

    public PlayerSocketHandler(UserRepository userRepo, GameRepository gameRepo, MoveRepository moveRepo) {
        this.userRepo = userRepo;
        this.gameRepo = gameRepo;
        this.moveRepo = moveRepo;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String username = getUsername(session);
        if (username != null) {
            sessions.put(username, session);
            System.out.println("✅ " + username + " connected");
            broadcastPlayers();
        }
    }

    // Gérer les messages entrants (type , invite , from , to)
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Map<String, Object> payload = mapper.readValue(message.getPayload(), Map.class);
        String type = (String) payload.get("type");

        switch (type) {
            case "invite" -> handleInvite(payload);
            case "inviteResponse" -> handleInviteResponse(payload);
            case "move" -> handleMove(payload);
        }
    }

    // envoyer un message d’invitation d’un utilisateur (from) vers un autre (to) en temps réel.
    private void handleInvite(Map<String, Object> payload) throws Exception {
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");

        WebSocketSession toSession = sessions.get(to);
        if (toSession != null && toSession.isOpen()) {
            toSession.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                    "type", "invite",
                    "from", from
            ))));
        }
    }

    // gérer la réponse à une invitation (acceptée ou refusée)
    private void handleInviteResponse(Map<String, Object> payload) throws Exception {
        String from = (String) payload.get("from");
        String to = (String) payload.get("to");
        boolean accepted = (Boolean) payload.get("accepted");
        // pour garder la session connectée de user
        WebSocketSession toSession = sessions.get(to);

        if (accepted) {
            // 🔹 Créer une nouvelle partie
            Game game = new Game();
            game.setPlayer1(from);
            game.setPlayer2(to);
            game.setStatus("ONGOING");
            gameRepo.save(game);

            // 🔹 Envoyer le message aux deux joueurs
            sendToBoth(from, to, Map.of(
                    "type", "gameStart",
                    "gameId", game.getId(),
                    "player1", from,
                    "player2", to
            ));
        } else {
            if (toSession != null)
                toSession.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                        "type", "inviteResponse",
                        "from", from,
                        "accepted", false
                ))));
        }
    }

    // gérer les mouvements de jeu
    private void handleMove(Map<String, Object> payload) throws Exception {
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
        moveRepo.save(move);

        // 🔹 Envoyer le coup à l’adversaire
        Game game = gameRepo.findById(gameId).orElse(null);
        if (game != null) {
            String opponent = game.getPlayer1().equals(player) ? game.getPlayer2() : game.getPlayer1();
            WebSocketSession oppSession = sessions.get(opponent);
            if (oppSession != null && oppSession.isOpen()) {
                oppSession.sendMessage(new TextMessage(mapper.writeValueAsString(Map.of(
                        "type", "move",
                        "gameId", gameId,
                        "fromCell", fromCell,
                        "toCell", toCell,
                        "player", player
                ))));
            }
        }
    }

    // Méthode utilitaire pour envoyer un message aux deux joueurs
    private void sendToBoth(String p1, String p2, Map<String, Object> msg) throws Exception {
        String json = mapper.writeValueAsString(msg);
        for (String p : new String[]{p1, p2}) {
            WebSocketSession s = sessions.get(p);
            if (s != null && s.isOpen()) {
                s.sendMessage(new TextMessage(json));
            }
        }
    }

    // Gérer la déconnexion
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String username = getUsername(session);
        if (username != null) {
            sessions.remove(username);
            broadcastPlayers();
            System.out.println("❌ " + username + " disconnected");
        }
    }


    // Méthode pour diffuser la liste des joueurs connectés à tous les clients
    private void broadcastPlayers() throws Exception {
        var msg = mapper.writeValueAsString(Map.of("type", "players", "players", sessions.keySet()));
        for (WebSocketSession s : sessions.values()) {
            if (s.isOpen()) {
                s.sendMessage(new TextMessage(msg));
            }
        }
    }

    private String getUsername(WebSocketSession session) {
        var query = session.getUri().getQuery();
        if (query != null && query.startsWith("username=")) {
            return query.split("=")[1];
        }
        return null;
    }
}
