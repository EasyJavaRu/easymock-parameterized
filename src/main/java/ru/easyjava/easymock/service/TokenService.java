package ru.easyjava.easymock.service;

import ru.easyjava.easymock.entity.User;

/**
 * User authentication token support service.
 */
public interface TokenService {
    /**
     * Check, if supplied token is valid
     * and returns related User object.
     * @param token Token string to validate.
     * @return Related user object or null if token is not valid.
     */
    User validateToken(String token);

    /**
     * Extracts application id part from the token.
     * @param token Token string to process
     * @return ApplicationId string
     */
    String getApplicationId(String token);
}
