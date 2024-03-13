package ui;

import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.response.BaseResponse;
import model.response.LoginResponse;

public class PreLoginUI extends Client {

    public void start() {

        out.println(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_BG_COLOR);
        out.println("Welcome to Chess 1.0!");
        out.println("(" + HELP + ")");

        while (true) {

            out.print(EscapeSequences.SET_TEXT_COLOR_YELLOW + "\nchess> " + EscapeSequences.SET_TEXT_COLOR_WHITE);
            String input = in.nextLine().toLowerCase();
            switch (input) {
                case "h", "help" -> help();
                case "l", "login" -> {if (login()) new PostLoginUI().start();}
                case "r", "register" -> {if (register()) new PostLoginUI().start();}
                case "q", "quit" -> {return;}
                case "cleardb" -> clear();
                case "auto" -> {if (auto()) new PostLoginUI().start();}
                default -> out.println("Unknown command. " + HELP);
            }
        }
    }

    private void help() {
        out.println("Options:");
        out.println("\"h\", \"help\": See options");
        out.println("\"l\", \"login\": Login as an existing user");
        out.println("\"r\", \"register\": Register a new user");
        out.println("\"q\", \"quit\": Exit the program");
    }

    private boolean login() {

        // build login request
        String username = prompt("Enter your username: ");
        String password = prompt("Enter your password: ");
        out.print("\n");
        LoginRequest request = new LoginRequest(username, password);

        // send login request to server
        BaseResponse response = server.login(request);
        if (response.getMessage() == null) authToken = ((LoginResponse) response).getAuthToken();
        else {
            out.println("Login failed:");
            printError(response.getMessage());
        }
        return response.getMessage() == null;
    }

    private boolean register() {

        // build register request
        String username = prompt("Enter your username: ");
        String password = prompt("Enter your password: ");
        String email = prompt("Enter your email: ");
        out.print("\n");
        RegisterRequest request = new RegisterRequest(username, password, email);

        // send register request to server
        BaseResponse response = server.register(request);
        if (response.getMessage() == null) authToken = ((LoginResponse) response).getAuthToken();
        else {
            out.println("Register failed:");
            printError(response.getMessage());
        }
        return response.getMessage() == null;
    }

    private void clear() {

        // send clear request to server
        BaseResponse response = server.clear();
        if (response.getMessage() == null) out.println("Database cleared.");
        else {
            out.println("Clear failed:");
            printError(response.getMessage());
        }
    }

    // For testing purposes, registers or logs in a user named "test"
    private boolean auto() {
        LoginRequest request = new LoginRequest("test", "test");
        BaseResponse response = server.login(request);
        if (response.getMessage() == null) authToken = ((LoginResponse) response).getAuthToken();
        else {
            RegisterRequest request1 = new RegisterRequest("test", "test", "test");
            BaseResponse response1 = server.register(request1);
            if (response1.getMessage() == null) authToken = ((LoginResponse) response1).getAuthToken();
            else {
                out.println("Login failed:");
                printError(response.getMessage());
            }
            return response1.getMessage() == null;
        }
        return response.getMessage() == null;
    }
}
