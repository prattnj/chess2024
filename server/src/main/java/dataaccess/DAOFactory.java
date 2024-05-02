package dataaccess;

import dataaccess.mysql.MySQLAuthTokenDAO;
import dataaccess.mysql.MySQLGameDAO;
import dataaccess.mysql.MySQLUserDAO;
import dataaccess.ram.RAMAuthTokenDAO;
import dataaccess.ram.RAMGameDAO;
import dataaccess.ram.RAMUserDAO;
import util.Util;

public class DAOFactory {

    public static UserDAO getNewUserDAO() throws DataAccessException {
        if (Util.dbType.equals("mysql")) return new MySQLUserDAO();
        else return RAMUserDAO.getInstance();
    }

    public static GameDAO getNewGameDAO() throws DataAccessException {
        if (Util.dbType.equals("mysql")) return new MySQLGameDAO();
        else return RAMGameDAO.getInstance();
    }

    public static AuthTokenDAO getNewAuthTokenDAO() throws DataAccessException {
        if (Util.dbType.equals("mysql")) return new MySQLAuthTokenDAO();
        else return RAMAuthTokenDAO.getInstance();
    }
}
