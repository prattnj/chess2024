package dataAccess.mysql;

import chess.ChessGame;
import dataAccess.DataAccessException;
import dataAccess.GameDAO;
import model.bean.GameBean;

import java.sql.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class MySQLGameDAO implements GameDAO {

    @Override
    public void insert(GameBean bean) throws DataAccessException {
        String sql = "INSERT INTO game (gameID, whiteUsername, blackUsername, gameName, game) VALUES (?, ?, ?, ?, ?);";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, bean.getGameID());
            if (bean.getWhiteUsername() == null) stmt.setNull(2, Types.INTEGER);
            else stmt.setString(2, bean.getWhiteUsername());
            if (bean.getBlackUsername() == null) stmt.setNull(3, Types.INTEGER);
            else stmt.setString(3, bean.getBlackUsername());
            stmt.setString(4, bean.getGameName());
            stmt.setString(5, bean.getGame());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public GameBean find(int gameID) throws DataAccessException {
        String sql = "SELECT * FROM game WHERE gameID = ?;";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return null;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, gameID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String white = rs.getString(2);
                String black = rs.getString(3);
                return new GameBean(rs.getInt(1), white, black, rs.getString(4), rs.getString(5));
            }
            else return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public Collection<GameBean> findAll() throws DataAccessException {
        String sql = "SELECT * FROM game ORDER BY gameName;";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return null;
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();
            Set<GameBean> allGames = new HashSet<>();
            while (rs.next()) {
                String white = rs.getString(2);
                String black = rs.getString(3);
                allGames.add(new GameBean(rs.getInt(1), white, black, rs.getString(4), rs.getString(5)));
            }
            return allGames;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void update(GameBean bean) throws DataAccessException {
        String sql = "UPDATE game SET game = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            // If this game does not exist, insert it
            if (find(bean.getGameID()) == null) {
                insert(bean);
                return;
            }
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, bean.getGame());
            stmt.setInt(2, bean.getGameID());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void delete(int gameID) throws DataAccessException {
        String sql = "DELETE FROM game WHERE gameID = ?;";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, gameID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        String sql = "DELETE FROM game;";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public void claimSpot(int gameID, ChessGame.TeamColor color, String username) throws DataAccessException {
        if (color == null) return;
        String sql = color == ChessGame.TeamColor.WHITE ?
                "UPDATE game SET whiteUsername = ? WHERE gameID = ?" :
                "UPDATE game SET blackUsername = ? WHERE gameID = ?";
        try (Connection conn = DatabaseManager.getConnection()) {
            if (conn.isClosed()) return;
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setInt(2, gameID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new DataAccessException(e.getMessage());
        }
    }
}
