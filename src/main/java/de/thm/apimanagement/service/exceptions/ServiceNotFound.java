package de.thm.apimanagement.service.exceptions;

public class ServiceNotFound extends RuntimeException {
    public ServiceNotFound(String message) {
        super(message);
    }
}
