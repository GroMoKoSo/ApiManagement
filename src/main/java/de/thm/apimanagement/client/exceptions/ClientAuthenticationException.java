package de.thm.apimanagement.client.exceptions;

/**
 * ClientAuthenticationException is thrown when clients encounter authentication related exceptions
 *
 * @author Justin Wolek
 */
public class ClientAuthenticationException extends RuntimeException {
    public ClientAuthenticationException(String message) {
        super(message);
    }
}
