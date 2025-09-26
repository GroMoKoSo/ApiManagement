package de.thm.apimanagement.service.exceptions;

public class ServiceNotAllowed extends RuntimeException {
    public ServiceNotAllowed(String message) {
        super(message);
    }
}
