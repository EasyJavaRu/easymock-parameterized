package ru.easyjava.easymock.service;

import ru.easyjava.easymock.entity.User;

/**
 * Meters rate of users requests per user.
 * Provides mechanisms to check, whether user
 * are overusing their request limit or not.
 */
public interface RequestLimitingService {
    /**
     * Checks, if user is overusing his limit
     * with specific app or not.
     * @param user User to check.
     * @param applicationId Id of request application.
     * @return result check. True is for positive answer.
     */
    Boolean checkLimit(User user, String applicationId);

    /**
     * Some users are allowed to overuse resources,
     * so we have to check it.
     * @param user User to check.
     * @return result check. True is for positive answer.
     */
    Boolean checkExtendedLimit(User user);
}
