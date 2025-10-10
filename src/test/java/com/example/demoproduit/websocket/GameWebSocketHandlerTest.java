package com.example.demoproduit.websocket;

import org.junit.jupiter.api.Test;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GameWebSocketHandlerTest {

    @Test
    public void testWebSocketGameFlow() throws Exception {
        StandardWebSocketClient client1 = new StandardWebSocketClient();
        StandardWebSocketClient client2 = new StandardWebSocketClient();

        List<String> messages1 = new ArrayList<>();
        List<String> messages2 = new ArrayList<>();

        // 🔹 Client1
        WebSocketSession session1 = client1.execute(
                new AbstractWebSocketHandler() {
                    @Override
                    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                        messages1.add(message.getPayload());
                    }
                },
                URI.create("ws://localhost:8080/gameSocket?gameId=1").toString()
        ).get();

        // 🔹 Client2
        WebSocketSession session2 = client2.execute(
                new AbstractWebSocketHandler() {
                    @Override
                    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
                        messages2.add(message.getPayload());
                    }
                },
                URI.create("ws://localhost:8080/gameSocket?gameId=1").toString()
        ).get();

        // 🔹 Jouer un coup
        String moveJson = """
                {
                  "type": "move",
                  "gameId": 1,
                  "fromCell": "E2",
                  "toCell": "E4",
                  "player": "Alice"
                }
                """;

        session1.sendMessage(new TextMessage(moveJson));

        Thread.sleep(500);

        // 🔹 Vérifier que session2 a reçu le coup
        assertTrue(messages2.stream().anyMatch(msg -> msg.contains("E4")));

        session1.close();
        session2.close();
    }
}
