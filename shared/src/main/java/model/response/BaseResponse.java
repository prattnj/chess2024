package model.response;

/**
 * A bare-bones response sent from the server to the client
 */
public class BaseResponse {

    private final String message;

    public BaseResponse() {
        this.message = null;
    }

    public BaseResponse(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
