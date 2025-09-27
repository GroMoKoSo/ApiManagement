package de.thm.apimanagement.service.exceptions;

public class ServiceError extends RuntimeException {
    public ServiceError(String message) {
        super(message);
    }
}
