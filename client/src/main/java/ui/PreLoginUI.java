package ui;

import model.request.LoginRequest;
import model.request.RegisterRequest;
import model.response.BaseResponse;
import model.response.LoginResponse;

public class PreLoginUI extends Client {

    public void start() {

        OUT.println(EscapeSequences.SET_TEXT_COLOR_WHITE + EscapeSequences.RESET_BG_COLOR);
        OUT.println("Welcome to Chess 1.0!");
        OUT.println("(" + HELP + ")");

        while (true) {

            OUT.print(EscapeSequences.SET_TEXT_COLOR_YELLOW + "\nchess> " + EscapeSequences.SET_TEXT_COLOR_WHITE);
            String input = IN.nextLine().toLowerCase();
            switch (input) {
                case "h", "help" -> help();
                case "l", "login" -> {if (login()) new PostLoginUI().start();}
                case "r", "register" -> {if (register()) new PostLoginUI().start();}
                case "q", "quit" -> {return;}
                case "cleardb" -> clear();
                case "auto" -> {if (auto()) new PostLoginUI().start();}
                default -> OUT.println("Unknown command. " + HELP);
            }
        }
    }

    private void help() {
        OUT.println("Options:");
        OUT.println("\"h\", \"help\": See options");
        OUT.println("\"l\", \"login\": Login as an existing user");
        OUT.println("\"r\", \"register\": Register a new user");
        OUT.println("\"q\", \"quit\": Exit the program");
    }

    private boolean login() {

        // build login request
        String username = prompt("Enter your username: ");
        String password = prompt("Enter your password: ");
        OUT.print("\n");
        LoginRequest request = new LoginRequest(username, password);

        // send login request to server
        BaseResponse response = server.login(request);
        if (response.getMessage() == null) authToken = ((LoginResponse) response).getAuthToken();
        else {
            OUT.println("Login failed:");
            printError(response.getMessage());
        }
        return response.getMessage() == null;
    }

    private boolean register() {

        // build register request
        String username = prompt("Enter your username: ");
        String password = prompt("Enter your password: ");
        String email = prompt("Enter your email: ");
        OUT.print("\n");
        RegisterRequest request = new RegisterRequest(username, password, email);

        // send register request to server
        BaseResponse response = server.register(request);
        if (response.getMessage() == null) authToken = ((LoginResponse) response).getAuthToken();
        else {
            OUT.println("Register failed:");
            printError(response.getMessage());
        }
        return response.getMessage() == null;
    }

    private void clear() {

        // send clear request to server
        BaseResponse response = server.clear();
        if (response.getMessage() == null) OUT.println("Database cleared.");
        else {
            OUT.println("Clear failed:");
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
                OUT.println("Login failed:");
                printError(response.getMessage());
            }
            return response1.getMessage() == null;
        }
        return response.getMessage() == null;
    }
}
