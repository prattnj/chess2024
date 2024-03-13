package util;

import chess.*;

import java.util.Random;
import java.util.UUID;

/**
 * A class containing static methods for use across the project
 */
public class Util {

    /**
     * Represents the current database being used. Currently, supports "ram" and "mysql".
     * Can be overridden when running server.Server
     */
    public static String dbType = "mysql";

    public static final String INVALID_TOKEN = "invalid authToken";

    public static final String BAD_REQUEST = "invalid or incomplete request";

    public static final String SERVER_ERROR = "Internal server error";

    /**
     * Generates a random n-digit integer
     * @param digits The number of digits in this random ID
     * @return An integer from 10^n-1 to 10^n
     */
    public static int getRandomID(int digits) {
        return new Random().nextInt((int) Math.pow(10, digits - 1), (int) Math.pow(10, digits));
    }

    /**
     * Generates a new authToken using Java's UUID class
     * @return The new authToken as a String
     */
    public static String getNewAuthToken() {
        return UUID.randomUUID().toString();
    }

    /**
     * Given a String, returns the corresponding TeamColor. Returns null if the String is not a valid color
     * @param str The String to be converted
     * @return The corresponding TeamColor
     */
    public static ChessGame.TeamColor getColorForString(String str) {
        if (str.equalsIgnoreCase("white") || str.equalsIgnoreCase("w")) return ChessGame.TeamColor.WHITE;
        else if (str.equalsIgnoreCase("black") || str.equalsIgnoreCase("b")) return ChessGame.TeamColor.BLACK;
        else return null;
    }

    /**
     * Given a TeamColor, returns the corresponding String. Returns null if the TeamColor is not valid
     * @param color The TeamColor to be converted
     * @return The corresponding String
     */
    public static String getStringForColor(ChessGame.TeamColor color) {
        if (color == ChessGame.TeamColor.WHITE) return "white";
        else if (color == ChessGame.TeamColor.BLACK) return "black";
        else return null;
    }
}
