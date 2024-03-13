package model.bean;

/**
 * Represents an AuthToken entry in the database
 */
public class AuthTokenBean {

    private final String authToken;
    private final String username;

    /**
     * Basic constructor
     * @param authToken The token itself, as a String
     * @param username The username of the user whose authToken this is
     */
    public AuthTokenBean(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }
}
