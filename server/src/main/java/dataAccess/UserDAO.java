package dataAccess;

import model.bean.UserBean;

public interface UserDAO {

    /**
     * Inserts a user into the database
     * @param bean The user to insert
     * @throws DataAccessException
     */
    void insert(UserBean bean) throws DataAccessException;

    /**
     * Finds a user from the database
     * @param username The username of the user to find
     * @return The user in question, or null if it doesn't exist
     * @throws DataAccessException
     */
    UserBean find(String username) throws DataAccessException;

    /**
     * Updates a given user in the database
     * @param bean The user to update
     * @throws DataAccessException
     */
    void update(UserBean bean) throws DataAccessException;

    /**
     * Removes a user from the database
     * @param username The username of the user to remove
     * @throws DataAccessException
     */
    void delete(String username) throws DataAccessException;

    /**
     * Removes all users from the database
     * @throws DataAccessException
     */
    void clear() throws DataAccessException;
}
