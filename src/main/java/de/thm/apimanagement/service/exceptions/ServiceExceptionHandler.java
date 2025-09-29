package de.thm.apimanagement.service.exceptions;

import de.thm.apimanagement.client.exceptions.ClientAuthenticationException;
import de.thm.apimanagement.client.exceptions.ClientNotFoundException;

public class ServiceExceptionHandler {
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
