package ui;

import com.google.gson.Gson;
import net.ServerFacade;
import net.WSConnection;

import java.io.PrintStream;
import java.net.URI;
import java.util.Scanner;

public class Client {

    protected static String host = "localhost";
    protected static String port = "8080";
    protected static String authToken;
    protected static ServerFacade server;
    protected static WSConnection connection = null;
    protected static final Scanner in = new Scanner(System.in);
    protected static final PrintStream out = System.out;
    protected final Gson gson = new Gson();
    protected static final String HELP = "Enter \"h\" or \"help\" for options";
    protected static final String EXIT_MESSAGE = "Happy trails!";

    public static void main(String[] args) {

        // Check for host and port arguments
        if (args.length >= 2){
            host = args[0];
            port = args[1];
        }
        server = new ServerFacade(host, port);

        // Connect to server
        try {
            connection = new WSConnection(new URI("ws://" + host + ":" + port + "/connect"));
        } catch (Exception e) {
            printError("Unable to connect to server. Try again later.");
            quit();
        }

        new PreLoginUI().start();
        quit();
    }

    protected static String prompt(String message) {
        out.print(message);
        return in.nextLine();
    }

    protected static void printError(String error) {
        out.println(EscapeSequences.SET_TEXT_COLOR_RED + error + EscapeSequences.SET_TEXT_COLOR_WHITE);
    }

    protected static void quit() {
        //if (connection != null) connection.close();
        out.println(EXIT_MESSAGE);
        System.exit(0);
    }
}
