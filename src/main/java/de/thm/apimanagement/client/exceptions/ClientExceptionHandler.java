package de.thm.apimanagement.client.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.web.client.HttpClientErrorException;

/**
 * ClientExceptionHandler is responsible for converting exceptions which occur
 * in clients to client-specific exceptions
 *
 * @author Justin Wolek
 */
public class ClientExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(ClientExceptionHandler.class);

    /**
     * Converts exceptions which occur in clients to client-specific exceptions
     *
     * @param e The exception which has been thrown
     * @return  The corresponding client-specific exception
     */
    public static RuntimeException handleException(Exception e) {
        if (e instanceof OAuth2AuthenticationException) {
            logger.error("Authentication Exception: ", e);
            return new ClientAuthenticationException("Authentication Failed");

        } else if (e instanceof HttpClientErrorException) {
            logger.error("Client Error Exception: ", e);
            HttpStatusCode status = ((HttpClientErrorException) e).getStatusCode();

            if (status == HttpStatus.UNAUTHORIZED) return new ClientAuthenticationException("Authentication Failed");
            else if (status == HttpStatus.NOT_FOUND) return new ClientNotFoundException("Resource not found");
            else return new ClientErrorException(e.getMessage());

        } else {
            logger.error("Other Exception: ", e);
            return new ClientErrorException(e.getMessage());
        }
    }
}
