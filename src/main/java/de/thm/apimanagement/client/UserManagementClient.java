package de.thm.apimanagement.client;

import de.thm.apimanagement.client.exceptions.ClientAuthenticationException;
import de.thm.apimanagement.client.exceptions.ClientErrorException;
import de.thm.apimanagement.client.exceptions.ClientNotFoundException;
import de.thm.apimanagement.entity.ApiWithActive;
import de.thm.apimanagement.entity.UserWithRole;
import de.thm.apimanagement.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.Objects;

/**
 * UserManagementClient is responsible for communicating with the UserManagementClient subsystem
 *
 * @author Justin Wolek
 */
@Component
public class UserManagementClient {
    private final Logger logger = LoggerFactory.getLogger(UserManagementClient.class);
    private final TokenProvider tokenProvider;
    private final RestClient client;

    public UserManagementClient(TokenProvider tokenProvider, @Value("${spring.subservices.user-management.url}") String baseUrl ) {
        this.tokenProvider = tokenProvider;
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Checks if a user is authenticated to perform actions for a certain user or group
     *
     * @param user  The user which is performing an action
     * @param group The group which an action MAY be performed on. Keep emtpy to perform the check on the user
     * @return      a {@code boolean} representing if actions are allowed or not.
     */
    public boolean isUserAuthorized(String user, String group) {
        if (!StringUtils.hasText(user)) {
            throw new ClientErrorException("User cannot be empty");
        }

        if (!doesUserExist(user)) throw new ClientNotFoundException("User does not exist");

        // Check if user and/or group does exist. Throw errors accordingly
        if (!StringUtils.hasText(group)) {
            return true;
        } else {
            if (!doesGroupExist(group)) throw new ClientNotFoundException("Group does not exist");
        }

        // First, get all users inside the group
        UserWithRole[] userRolesInGroup;
        try {
            userRolesInGroup = client.get()
                    .uri("/groups/{group}/users", group)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .retrieve()
                    .body(UserWithRole[].class);
        } catch (OAuth2AuthenticationException e) {
            logger.error("Authentication Exception: ", e);
            throw new ClientAuthenticationException("Authentication Failed");

        } catch (HttpClientErrorException e) {
            logger.error("Client Error Exception: ", e);
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.UNAUTHORIZED) throw new ClientAuthenticationException("Authentication Failed");
            else if (status == HttpStatus.NOT_FOUND) throw new ClientNotFoundException("Resource not found");
            else throw new ClientErrorException(e.getMessage());

        } catch (Exception e) {
            logger.error("Other Exception: ", e);
            throw new ClientErrorException(e.getMessage());
        }


        // Iterate through every user in the group.
        // Only when the given user has the Editor or Admin role APIs can be changed by the user in the group
        boolean isAuthorized = false;
        assert userRolesInGroup != null;
        for (UserWithRole userWithRole : userRolesInGroup) {
            if (Objects.equals(userWithRole.getUsername(), user)
                    && (Objects.equals(userWithRole.getGroupRole(), "Editor")
                    || Objects.equals(userWithRole.getGroupRole(), "Admin"))) {
                isAuthorized = true;
                break;
            }
        }
        return isAuthorized;
    }

    /**
     * Checks if a given user is theoretically allowed to invoke their own apis or apis in a group
     *
     * @param user  The user which tries to invoke an api
     * @param group The group where a user might try to invoke an api
     * @return A boolean representing if a user can invoke an api
     */
    public boolean canInvoke(String user, String group) {
        if (!StringUtils.hasText(user)) {
            throw new ClientErrorException("User cannot be empty");
        }

        if (!doesUserExist(user)) throw new ClientNotFoundException("User does not exist");
        if (!StringUtils.hasText(group)) {
            return true;
        } else {
            if (!doesGroupExist(group)) throw new HttpClientErrorException(
                    HttpStatus.NOT_FOUND, "Group does not exist");
        }

        // First, get all users inside the group
        UserWithRole[] userRolesInGroup;
        try {
            userRolesInGroup = client.get()
                    .uri("/groups/{group}/users", group)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .retrieve()
                    .body(UserWithRole[].class);

        } catch (OAuth2AuthenticationException e) {
            logger.error("Authentication Exception: ", e);
            throw new ClientAuthenticationException("Authentication Failed");

        } catch (HttpClientErrorException e) {
            logger.error("Client Error Exception: ", e);
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.UNAUTHORIZED) throw new ClientAuthenticationException("Authentication Failed");
            else if (status == HttpStatus.NOT_FOUND) throw new ClientNotFoundException("Resource not found");
            else throw new ClientErrorException(e.getMessage());

        } catch (Exception e) {
            logger.error("Other Exception: ", e);
            throw new ClientErrorException(e.getMessage());
        }


        // Iterate through every user in the group.
        // Only when the given user exists it can invoke apis in this group
        boolean isAuthorized = false;
        assert userRolesInGroup != null;
        for (UserWithRole userWithRole : userRolesInGroup) {
            if (Objects.equals(userWithRole.getUsername(), user)) {
                isAuthorized = true;
                break;
            }
        }
        return isAuthorized;
    }

    /**
     * Adds a new Api id to a specified group
     *
     * @param apiWithActive The object containing the Api id and a boolean representing if the Api should be active
     * @param group         The group to add the api id to
     */
    public void addApiToGroup(String group, ApiWithActive apiWithActive) {
        if (!StringUtils.hasText(group)) {
            throw new IllegalArgumentException("Group cannot be empty");
        }

        try {
            // Add the Api to the group
            client.post()
                    .uri("/groups/{group}/apis", group)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(apiWithActive)
                    .retrieve()
                    .body(ApiWithActive.class);

        } catch (OAuth2AuthenticationException e) {
            logger.error("Authentication Exception: ", e);
            throw new ClientAuthenticationException("Authentication Failed");

        } catch (HttpClientErrorException e) {
            logger.error("Client Error Exception: ", e);
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.UNAUTHORIZED) throw new ClientAuthenticationException("Authentication Failed");
            else if (status == HttpStatus.NOT_FOUND) throw new ClientNotFoundException("Resource not found");
            else throw new ClientErrorException(e.getMessage());

        } catch (Exception e) {
            logger.error("Other Exception: ", e);
            throw new ClientErrorException(e.getMessage());
        }

    }

    /**
     * Adds a new Api id to a user
     *
     * @param apiWithActive The object containing the Api id and a boolean representing if the Api should be active
     * @param user          The user to add the api id to
     */
    public void addApiToUser(String user, ApiWithActive apiWithActive) {
        if (!StringUtils.hasText(user)) {
            throw new IllegalArgumentException("User cannot be empty");
        }

        try {
            // Add the Api to the user
            client.post()
                    .uri("/users/{user}/apis", user)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(apiWithActive)
                    .retrieve()
                    .body(ApiWithActive.class);

        } catch (OAuth2AuthenticationException e) {
            logger.error("Authentication Exception: ", e);
            throw new ClientAuthenticationException("Authentication Failed");

        } catch (HttpClientErrorException e) {
            logger.error("Client Error Exception: ", e);
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.UNAUTHORIZED) throw new ClientAuthenticationException("Authentication Failed");
            else if (status == HttpStatus.NOT_FOUND) throw new ClientNotFoundException("Resource not found");
            else throw new ClientErrorException(e.getMessage());

        } catch (Exception e) {
            logger.error("Other Exception: ", e);
            throw new ClientErrorException(e.getMessage());
        }

    }

    /**
     * Deletes an Api id from a group
     *
     * @param group The group to remove the api from
     * @param apiId The id of the api to remove
     */
    public void deleteApiFromGroup(String group, int apiId) {
        if (!StringUtils.hasText(group)) {
            throw new IllegalArgumentException("Group cannot be empty");
        }

        try {
            // Remove the Api from the group
            client.delete()
                    .uri("/groups/{group}/apis/{apiId}", group, apiId)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .retrieve()
                    .toBodilessEntity();

        } catch (OAuth2AuthenticationException e) {
            logger.error("Authentication Exception: ", e);
            throw new ClientAuthenticationException("Authentication Failed");

        } catch (HttpClientErrorException e) {
            logger.error("Client Error Exception: ", e);
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.UNAUTHORIZED) throw new ClientAuthenticationException("Authentication Failed");
            else if (status == HttpStatus.NOT_FOUND) throw new ClientNotFoundException("Resource not found");
            else throw new ClientErrorException(e.getMessage());

        } catch (Exception e) {
            logger.error("Other Exception: ", e);
            throw new ClientErrorException(e.getMessage());
        }

    }

    /**
     * Deletes an Api from a user
     *
     * @param user  The user to remove the api from
     * @param apiId The id of the api to remove
     */
    public void deleteApiFromUser(String user, int apiId) {
        if (!StringUtils.hasText(user)) {
            throw new IllegalArgumentException("User cannot be empty");
        }

        try {
            // Remove the Api from the user
            client.delete()
                    .uri("/users/{user}/apis/{apiId}", user, apiId)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .retrieve()
                    .toBodilessEntity();

        } catch (OAuth2AuthenticationException e) {
            logger.error("Authentication Exception: ", e);
            throw new ClientAuthenticationException("Authentication Failed");

        } catch (HttpClientErrorException e) {
            logger.error("Client Error Exception: ", e);
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.UNAUTHORIZED) throw new ClientAuthenticationException("Authentication Failed");
            else if (status == HttpStatus.NOT_FOUND) throw new ClientNotFoundException("Resource not found");
            else throw new ClientErrorException(e.getMessage());

        } catch (Exception e) {
            logger.error("Other Exception: ", e);
            throw new ClientErrorException(e.getMessage());
        }
    }

    /**
     * Fetches the APIs of a group and returns them.
     *
     * @param group The group to get the APIs from
     * @return      The APIs belonging to a user or a group
     */
    public ApiWithActive[] getApisOfGroup(String group) {
        try {
            return client.get()
                    .uri("/groups/{group}/apis", group)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .retrieve()
                    .body(ApiWithActive[].class);

        } catch (OAuth2AuthenticationException e) {
            logger.error("Authentication Exception: ", e);
            throw new ClientAuthenticationException("Authentication Failed");

        } catch (HttpClientErrorException e) {
            logger.error("Client Error Exception: ", e);
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.UNAUTHORIZED) throw new ClientAuthenticationException("Authentication Failed");
            else if (status == HttpStatus.NOT_FOUND) throw new ClientNotFoundException("Resource not found");
            else throw new ClientErrorException(e.getMessage());

        } catch (Exception e) {
            logger.error("Other Exception: ", e);
            throw new ClientErrorException(e.getMessage());
        }

    }

    /**
     * Fetches the APIs of a user and returns them.
     *
     * @param user The user to get the APIs from
     * @return      The APIs belonging to a user or a group
     */
    public ApiWithActive[] getApisOfUser(String user) {
       try {
           return client.get()
                   .uri(uriBuilder -> uriBuilder
                           .path("/users/{user}/apis")
                           .queryParam("accessViaGroup", false)
                           .build(user))
                   .header("Authorization", "Bearer " + tokenProvider.getToken())
                   .retrieve()
                   .body(ApiWithActive[].class);

       } catch (OAuth2AuthenticationException e) {
           logger.error("Authentication Exception: ", e);
           throw new ClientAuthenticationException("Authentication Failed");

       } catch (HttpClientErrorException e) {
           logger.error("Client Error Exception: ", e);
           HttpStatusCode status = e.getStatusCode();
           if (status == HttpStatus.UNAUTHORIZED) throw new ClientAuthenticationException("Authentication Failed");
           else if (status == HttpStatus.NOT_FOUND) throw new ClientNotFoundException("Resource not found");
           else throw new ClientErrorException(e.getMessage());

       } catch (Exception e) {
           logger.error("Other Exception: ", e);
           throw new ClientErrorException(e.getMessage());
       }
    }

    public boolean doesUserExist(String username) {
        try {
            client.get()
                    .uri("/users/{username}", username)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .retrieve()
                    .toBodilessEntity();
            return true;

        } catch (OAuth2AuthenticationException e) {
            logger.error("Authentication Exception: ", e);
            throw new ClientAuthenticationException("Authentication Failed");

        } catch (HttpClientErrorException e) {
            logger.error("Client Error Exception: ", e);
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.UNAUTHORIZED) throw new ClientAuthenticationException("Authentication Failed");
            else if (status == HttpStatus.NOT_FOUND) throw new ClientNotFoundException("Resource not found");
            else throw new ClientErrorException(e.getMessage());

        } catch (Exception e) {
            logger.error("Other Exception: ", e);
            throw new ClientErrorException(e.getMessage());
        }
    }

    public boolean doesGroupExist(String group) {
        try {
            client.get()
                    .uri("/groups/{group}", group)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .retrieve()
                    .toBodilessEntity();
            return true;

        } catch (OAuth2AuthenticationException e) {
            logger.error("Authentication Exception: ", e);
            throw new ClientAuthenticationException("Authentication Failed");

        } catch (HttpClientErrorException e) {
            logger.error("Client Error Exception: ", e);
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.UNAUTHORIZED) throw new ClientAuthenticationException("Authentication Failed");
            else if (status == HttpStatus.NOT_FOUND) throw new ClientNotFoundException("Resource not found");
            else throw new ClientErrorException(e.getMessage());

        } catch (Exception e) {
            logger.error("Other Exception: ", e);
            throw new ClientErrorException(e.getMessage());
        }
    }
}
