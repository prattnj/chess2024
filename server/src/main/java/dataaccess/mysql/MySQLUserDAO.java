package dataaccess.mysql;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.bean.UserBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO {

    @Override
    public void insert(UserBean bean) throws DataAccessException {
        String sql = "INSERT INTO user (username, password, email) VALUES (?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bean.getUsername());
            stmt.setString(2, bean.getPassword());
            stmt.setString(3, bean.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserBean find(String username) throws DataAccessException {
        String sql = "SELECT * FROM user WHERE username = ?;";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return null;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return new UserBean(rs.getString("username"), rs.getString("password"), rs.getString("email"));
            else return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void update(UserBean bean) throws DataAccessException {
        insert(bean);
    }

    @Override
    public void delete(String username) throws DataAccessException {
        String sql = "DELETE FROM user WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM user;";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }
}
