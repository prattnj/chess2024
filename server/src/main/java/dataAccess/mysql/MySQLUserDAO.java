package dataAccess.mysql;

import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.bean.UserBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLUserDAO implements UserDAO {

    @Override
    public void insert(UserBean bean) throws DataAccessException {
        String sql = "INSERT INTO user (userID, username, password, email) VALUES (?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bean.getUserID());
            stmt.setString(2, bean.getUsername());
            stmt.setString(3, bean.getPassword());
            stmt.setString(4, bean.getEmail());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public UserBean find(int userID) throws DataAccessException {
        String sql = "SELECT * FROM user WHERE userID = ?;";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return null;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return new UserBean(rs.getInt("userID"), rs.getString("username"), rs.getString("password"), rs.getString("email"));
            else return null;
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
            if (rs.next()) return new UserBean(rs.getInt("userID"), rs.getString("username"), rs.getString("password"), rs.getString("email"));
            else return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public boolean emailExists(String email) throws DataAccessException {
        String sql = "SELECT * FROM user WHERE email = ?;";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return false;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            return rs.next();
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
    public void delete(int userID) throws DataAccessException {
        String sql = "DELETE FROM user WHERE userID = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userID);
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
