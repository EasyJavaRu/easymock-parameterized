package ru.easyjava.easymock.service;

/**
 * Provides application registration facilities.
 */
public interface ApplicationRegistry {
    /**
     * Checks, if application id is valid.
     * @param applicationId Application id to check.
     * @return check result.
     */
    Boolean validateApplicationId(String applicationId);
}
