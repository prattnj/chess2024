package dataAccess.mysql;

import dataAccess.DataAccessException;

import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static final String databaseName;
    private static final String user;
    private static final String password;
    private static final String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) throw new Exception("Unable to load db.properties");
                Properties props = new Properties();
                props.load(propStream);
                databaseName = props.getProperty("db.name");
                user = props.getProperty("db.user");
                password = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
            }
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    public static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        createTables();
    }

    private static void createTables() throws DataAccessException {
        String userTable = """
                CREATE TABLE IF NOT EXISTS user (
                    username varchar(255) NOT NULL UNIQUE,
                    password varchar(255) NOT NULL,
                    email varchar(255) NOT NULL,
                    PRIMARY KEY (username)
                );
                """;
        String gameTable = """
                CREATE TABLE IF NOT EXISTS game (
                    gameID int NOT NULL,
                    whiteUsername varchar(255),
                    blackUsername varchar(255),
                    gameName varchar(255),
                    game text NOT NULL,
                    PRIMARY KEY (gameID)
                );
                """;
        String authTable = """
                CREATE TABLE IF NOT EXISTS auth (
                    authToken varchar(255) NOT NULL,
                    username varchar(255) NOT NULL,
                    PRIMARY KEY (authToken)
                );
                """;
        try (var conn = getConnection()) {
            PreparedStatement userStmt = conn.prepareStatement(userTable);
            PreparedStatement gameStmt = conn.prepareStatement(gameTable);
            PreparedStatement authStmt = conn.prepareStatement(authTable);
            userStmt.executeUpdate();
            gameStmt.executeUpdate();
            authStmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(connectionUrl, user, password);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
