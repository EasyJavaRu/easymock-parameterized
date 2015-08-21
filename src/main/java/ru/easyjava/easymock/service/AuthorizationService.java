package ru.easyjava.easymock.service;

import ru.easyjava.easymock.entity.User;

import javax.inject.Named;
import java.util.logging.Logger;

/**
 * Validates user's tokens and decides,
 * whether request is legitimate or not.
 */
@Named
public class AuthorizationService {
    /**
     * A logger.
     */
    private static final Logger LOG =
            Logger.getLogger(AuthorizationService.class.getName());

    /**
     * Dependency: token processing service.
     */
    @Named
    private TokenService tokenService;

    /**
     * Dependency: Application ids processing service.
     */
    @Named
    private ApplicationRegistry registry;


    /**
     * Dependency: Requiest limiting service.
     */
    @Named
    private RequestLimitingService limitingService;

    /**
     * Checks, wheterh tokes is assigned to a user and
     * that user is allowed to do that request.
     * @param token Token value to validate.
     * @return True for positive decision, false for negative.
     */
    public final Boolean authorize(final String token) {
        User user = tokenService.validateToken(token);
        if (user == null) {
            return false;
        }

        String applicationId = tokenService.getApplicationId(token);
        if (applicationId == null) {
            return false;
        }

        if (!registry.validateApplicationId(applicationId)) {
            return false;
        }

        // Limiting service is known to be buggy
        try {
            if (!limitingService.checkLimit(user, applicationId)) {
                if (!limitingService.checkExtendedLimit(user)) {
                    return false;
                }
            }
        } catch (NullPointerException ex) {
            LOG.warning("limiting service failed");
            return false;
        }

        return true;
    }
}
