package server;

import model.request.*;
import service.*;
import spark.*;
import util.Util;

import java.util.HashSet;
import java.util.Set;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.delete("/db", new Handler(new ClearService(), null, false));
        Spark.post("/user", new Handler(new RegisterService(), RegisterRequest.class, false));
        Spark.post("/session", new Handler(new LoginService(), LoginRequest.class, false));
        Spark.delete("/session", new Handler(new LogoutService(), null, true));
        Spark.post("/game", new Handler(new CreateGameService(), CreateGameRequest.class, true));
        Spark.put("/game", new Handler(new JoinGameService(), JoinGameRequest.class, true));
        //Spark.get("/game", new Handler(new ListGamesService(), null, true));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
    }

    public static void main(String[] args) {

        Set<String> validDbTypes = new HashSet<>();
        validDbTypes.add("ram");
        validDbTypes.add("mysql");

        String dbType;
        String usage = "Usage: java server.Server.main <port> [database]";

        if (args == null || args.length == 0) {
            System.out.println(usage);
            return;
        }
        try {
            // look for and assign command line arguments
            int port = Integer.parseInt(args[0]);

            if (args.length > 1) {
                dbType = args[1];
                if (!validDbTypes.contains(dbType)) throw new RuntimeException("Invalid database type");
            } else dbType = "ram";
            Util.CURRENT_DAO_TYPE = dbType;

            // run server
            int chosenPort = new Server().run(port);
            System.out.println("Server listening on port " + chosenPort + "...");

        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(usage);
        }
    }
}
