package net;

import chess.ChessGame;
import com.google.gson.Gson;
import websocket.messages.ServerMessage;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WSConnection extends Endpoint {

    private final Session session;
    private GameUI ui;
    private final Gson gson = new Gson();

    /**
     * Creates a new WebSocket connection to the server
     * @param serverUri the URI of the server
     */
    public WSConnection(URI serverUri) {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            this.session = container.connectToServer(this, serverUri);

            this.session.addMessageHandler(new MessageHandler.Whole<String>() {
                @Override
                public void onMessage(String s) {
                    ServerMessage message = gson.fromJson(s, ServerMessage.class);
                    if (message == null) return; // invalid message from server

                    switch (message.getServerMessageType()) {
                        case NOTIFICATION -> ui.notify(message.getMessage(), false);
                        case LOAD_GAME -> ui.setGame(gson.fromJson(message.getMessage(), ChessGame.class));
                        case ERROR -> ui.notify(message.getMessage(), true);
                    }
                }
            });
        } catch (DeploymentException | IOException e) {
            throw new RuntimeException(e);
        }

    }

    @Override
    public void onOpen(Session session, EndpointConfig endpointConfig) {}

    public void send(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Interface for the GameUI to implement so that the WSConnection can notify the UI
     */
    public interface GameUI {

        /**
         * Sets the game object in the UI
         * @param game
         */
        void setGame(ChessGame game);

        /**
         * Notifies the UI of a message from the server and prints it
         * @param message
         * @param isError
         */
        void notify(String message, boolean isError);
    }

    /**
     * Assigns the GameUI to the WSConnection
     * @param ui the GameUI to assign
     */
    public void assignGameUI(GameUI ui) {
        this.ui = ui;
    }
}
