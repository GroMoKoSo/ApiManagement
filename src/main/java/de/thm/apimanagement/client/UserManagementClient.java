package de.thm.apimanagement.client;

import de.thm.apimanagement.client.exceptions.ClientExceptionHandler;
import de.thm.apimanagement.entity.ApiWithActive;
import de.thm.apimanagement.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

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

        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
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

        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
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

        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
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

        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
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

        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
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

       } catch (Exception e) {
           throw ClientExceptionHandler.handleException(e);
       }
    }
}
