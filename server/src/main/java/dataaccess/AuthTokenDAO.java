package dataaccess;

import model.bean.AuthTokenBean;

public interface AuthTokenDAO {

    /**
     * Inserts an authToken into the database
     * @param bean The authToken to insert
     * @throws DataAccessException
     */
    void insert(AuthTokenBean bean) throws DataAccessException;

    /**
     * Finds an authToken from the database
     * @param username The username of the authToken to find
     * @return The authToken in question, or null if it doesn't exist
     * @throws DataAccessException
     */
    AuthTokenBean find(String username) throws DataAccessException;

    /**
     * Updates a given authToken in the database
     * @param bean The authToken to update
     * @throws DataAccessException
     */
    void update(AuthTokenBean bean) throws DataAccessException;

    /**
     * Removes an authToken from the database
     * @param authToken The authToken String of the object to remove
     * @throws DataAccessException
     */
    void delete(String authToken) throws DataAccessException;

    /**
     * Removes all authTokens from the database
     * @throws DataAccessException
     */
    void clear() throws DataAccessException;
}
