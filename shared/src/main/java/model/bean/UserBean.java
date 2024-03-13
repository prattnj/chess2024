package model.bean;

/**
 * Represents a User entry in the database
 */
public class UserBean {

    private final String username;
    private final String password;
    private final String email;

    /**
     * Basic constructor
     * @param username A unique username for this user
     * @param password This user's password
     * @param email This user's email
     */
    public UserBean(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getEmail() {
        return email;
    }
}
