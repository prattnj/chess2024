package service;

import model.bean.AuthTokenBean;
import model.bean.UserBean;
import model.request.BaseRequest;
import model.request.LoginRequest;
import model.response.BaseResponse;
import model.response.LoginResponse;
import org.mindrot.jbcrypt.BCrypt;
import server.UnauthorizedException;
import util.Util;

/**
 * Logs the user in and returns a new authToken
 */
public class LoginService extends Service {

    @Override
    protected BaseResponse doService(BaseRequest request, String authToken) throws Exception {
        // authToken is null

        LoginRequest req = (LoginRequest) request;

        UserBean user = udao.find(req.getUsername());
        if (user == null) throw new UnauthorizedException("invalid username");
        if (!BCrypt.checkpw(req.getPassword(), user.getPassword())) throw new UnauthorizedException("invalid password");

        // Login success
        String newToken = Util.getNewAuthToken();
        adao.insert(new AuthTokenBean(newToken, user.getUsername()));

        return new LoginResponse(newToken, req.getUsername());
    }
}
