package service;

import dataAccess.*;
import dataAccess.DataAccessException;
import model.request.BaseRequest;
import model.response.BaseResponse;
import server.ServerErrorException;
import server.UnauthorizedException;
import util.Util;

/**
 * The starting point for all Service classes
 */
public abstract class Service {

    protected UserDAO udao;
    protected GameDAO gdao;
    protected AuthTokenDAO adao;

    /**
     * Handles all common service functionality including DAOs, validating tokens, and committing changes if necessary
     * @param request The request to be processed by each service
     * @param authToken The user's authToken, can be null
     * @return A response object, different for each service
     * @throws Exception Corresponds to the exceptions in this package so the Handler can set the HTTP status code
     */
    public BaseResponse execute(BaseRequest request, String authToken) throws Exception {

        try {
            // Initialize DAOs and DB transaction
            udao = DAOFactory.getNewUserDAO();
            gdao = DAOFactory.getNewGameDAO();
            adao = DAOFactory.getNewAuthTokenDAO();

            // Validate authToken
            if (authToken != null) if (adao.find(authToken) == null) throw new UnauthorizedException(Util.INVALID_TOKEN);

            // Execute service logic

            return doService(request, authToken);
        } catch (ServerErrorException | DataAccessException e) {
            e.printStackTrace();
            throw new ServerErrorException();
        }
    }

    /**
     * The specific implementations of each service so all common code can be abstracted out
     * @param request The request to be processed by each service
     * @param authToken The user's authToken, can be null
     * @return A response object, different for each service
     * @throws Exception
     */
    protected abstract BaseResponse doService(BaseRequest request, String authToken) throws Exception;
}
