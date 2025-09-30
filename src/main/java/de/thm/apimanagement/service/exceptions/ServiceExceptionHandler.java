package de.thm.apimanagement.service.exceptions;

import de.thm.apimanagement.client.exceptions.ClientAuthenticationException;
import de.thm.apimanagement.client.exceptions.ClientNotFoundException;

/**
 * ServiceExceptionHandler is responsible for converting exceptions which occur
 * in services to service-specific exceptions
 *
 * @author Justin Wolek
 */
public class ServiceExceptionHandler {

    /**
     * Converts exceptions which occur in services to service-specific exceptions
     *
     * @param e The exception which has been thrown
     * @return  The corresponding service-specific exception
     */
    public static RuntimeException handleException(Exception e) {
        if (e instanceof ClientNotFoundException) {
            return new ServiceNotFound("Resource not found");

        } else if (e instanceof ClientAuthenticationException) {
            return new ServiceNotAllowed("Authentication failed!");

        } else {
            return new ServiceError(e.getMessage());
        }
    }
}
