package dataAccess.mysql;

import dataAccess.AuthTokenDAO;
import dataAccess.DataAccessException;
import model.bean.AuthTokenBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLAuthTokenDAO implements AuthTokenDAO {

    @Override
    public void insert(AuthTokenBean bean) throws DataAccessException {
        String sql = "INSERT INTO auth (authtoken, username) VALUES (?, ?);";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bean.getAuthToken());
            stmt.setString(2, bean.getUsername());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public AuthTokenBean find(String authToken) throws DataAccessException {
        String sql = "SELECT * FROM auth WHERE authtoken = ?;";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return null;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, authToken);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return new AuthTokenBean(rs.getString("authtoken"), rs.getString("username"));
            else return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void update(AuthTokenBean bean) throws DataAccessException {
        String sql = "UPDATE auth SET authToken = ? WHERE username = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            // If this authToken does not exist, insert it
            if (find(bean.getUsername()) == null) {
                insert(bean);
                return;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bean.getAuthToken());
            stmt.setString(2, bean.getUsername());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void delete(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth WHERE authtoken = ?;";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth;";
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
