package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import model.request.BaseRequest;
import model.response.BaseResponse;
import service.Service;
import spark.Request;
import spark.Response;
import spark.Route;
import util.Util;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * The object used to handle every single dynamic request
 */
public class Handler implements Route {

    private final Service service;
    private final Type requestType;
    private final boolean authRequired;

    /**
     * Basic constructor
     * @param service The type of service that this instance will be executing
     * @param requestType The type of request that this service requires
     * @param authRequired Whether this endpoint requires authorization
     */
    public Handler(Service service, Type requestType, boolean authRequired) {
        this.service = service;
        this.requestType = requestType;
        this.authRequired = authRequired;
    }

    @Override
    public Object handle(Request request, Response response) {

        Gson gson = new Gson();

        // Handle authorization
        String authToken = request.headers("Authorization");
        if (authRequired && (authToken == null || authToken.isEmpty())) {
            response.status(401);
            return gson.toJson(new BaseResponse("Error: " + Util.INVALID_TOKEN));
        } else if (!authRequired) authToken = null;

        // Parse request and first-level validation
        BaseRequest serviceRequest = null;
        try {
            if (!Objects.equals(request.body(), "")) serviceRequest = gson.fromJson(request.body(), requestType);
        } catch (JsonSyntaxException e) {
            response.status(500);
            return gson.toJson(new BaseResponse("Error: bad json"));
        }
        if (serviceRequest != null) {
            if (!serviceRequest.isComplete()) {
                response.status(400);
                return gson.toJson(new BaseResponse("Error: " + Util.BAD_REQUEST));
            }
        }

        // Execute the service and determine the response code
        BaseResponse resp;
        try {
            resp = service.execute(serviceRequest, authToken);
        } catch (Exception e) {
            switch (e) {
                case BadRequestException badRequestException -> response.status(400);
                case UnauthorizedException unauthorizedException -> response.status(401);
                case ForbiddenException forbiddenException -> response.status(403);
                default -> response.status(500);
            }
            resp = new BaseResponse(e.getMessage());
        }

        return gson.toJson(resp);
    }
}
