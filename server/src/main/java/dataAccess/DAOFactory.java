package dataAccess;

import dataAccess.mysql.MySQLAuthTokenDAO;
import dataAccess.mysql.MySQLGameDAO;
import dataAccess.mysql.MySQLUserDAO;
import dataAccess.ram.RAMAuthTokenDAO;
import dataAccess.ram.RAMGameDAO;
import dataAccess.ram.RAMUserDAO;
import util.Util;

public class DAOFactory {

    public static UserDAO getNewUserDAO() throws DataAccessException {
        if (Util.DB_TYPE.equals("mysql")) return new MySQLUserDAO();
        else return RAMUserDAO.getInstance();
    }

    public static GameDAO getNewGameDAO() throws DataAccessException {
        if (Util.DB_TYPE.equals("mysql")) return new MySQLGameDAO();
        else return RAMGameDAO.getInstance();
    }

    public static AuthTokenDAO getNewAuthTokenDAO() throws DataAccessException {
        if (Util.DB_TYPE.equals("mysql")) return new MySQLAuthTokenDAO();
        else return RAMAuthTokenDAO.getInstance();
    }
}
