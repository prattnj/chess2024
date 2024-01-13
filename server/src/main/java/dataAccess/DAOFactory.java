package dataAccess;

//import dataAccess.mysql.MySQLAuthTokenDAO;
//import dataAccess.mysql.MySQLGameDAO;
//import dataAccess.mysql.MySQLTransaction;
//import dataAccess.mysql.MySQLUserDAO;
import dataAccess.ram.RAMAuthTokenDAO;
import dataAccess.ram.RAMGameDAO;
import dataAccess.ram.RAMTransaction;
import dataAccess.ram.RAMUserDAO;
import util.Util;

public class DAOFactory {

    /*public static UserDAO getNewUserDAO(Transaction transaction) throws DataAccessException {
        if (Util.CURRENT_DAO_TYPE.equals("mysql")) return new MySQLUserDAO(((MySQLTransaction) transaction).getMySQLConnection());
        else return RAMUserDAO.getInstance();
    }

    public static GameDAO getNewGameDAO(Transaction transaction) throws DataAccessException {
        if (Util.CURRENT_DAO_TYPE.equals("mysql")) return new MySQLGameDAO(((MySQLTransaction) transaction).getMySQLConnection());
        else return RAMGameDAO.getInstance();
    }

    public static AuthTokenDAO getNewAuthTokenDAO(Transaction transaction) throws DataAccessException {
        if (Util.CURRENT_DAO_TYPE.equals("mysql")) return new MySQLAuthTokenDAO(((MySQLTransaction) transaction).getMySQLConnection());
        else return RAMAuthTokenDAO.getInstance();
    }

    public static Transaction getNewTransaction() {
        if (Util.CURRENT_DAO_TYPE.equals("mysql")) return new MySQLTransaction();
        else return RAMTransaction.getInstance();
    }*/

    public static UserDAO getNewUserDAO(Transaction transaction) throws DataAccessException {
        return RAMUserDAO.getInstance();
    }

    public static GameDAO getNewGameDAO(Transaction transaction) throws DataAccessException {
        return RAMGameDAO.getInstance();
    }

    public static AuthTokenDAO getNewAuthTokenDAO(Transaction transaction) throws DataAccessException {
        return RAMAuthTokenDAO.getInstance();
    }

    public static Transaction getNewTransaction() {
        return RAMTransaction.getInstance();
    }
}
