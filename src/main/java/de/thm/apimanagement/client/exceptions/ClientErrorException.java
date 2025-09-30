package de.thm.apimanagement.client.exceptions;

/**
 * ClientErrorException is thrown by clients as a general exception
 *
 * @author Justin Wolek
 */
public class ClientErrorException extends RuntimeException {
    public ClientErrorException(String message) {
        super(message);
    }
}
