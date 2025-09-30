package de.thm.apimanagement.client.exceptions;

/**
 * ClientNotFoundException is thrown by clients when the requested resource does not exist
 *
 * @author Justin Wolek
 */
public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(String message) {
        super(message);
    }
}
