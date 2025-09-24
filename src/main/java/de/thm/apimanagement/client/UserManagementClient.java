package de.thm.apimanagement.client;

import de.thm.apimanagement.entity.ApiWithActive;
import de.thm.apimanagement.entity.UserWithRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.util.Objects;

/**
 * UserManagementClient is responsible for communicating with the UserManagementClient subsystem
 *
 * @author Justin Wolek
 */
@Component
public class UserManagementClient {
    private final RestClient client;
    private final String baseUrl;

    public UserManagementClient(@Value("${spring.subservices.user-management.url}") String baseUrl ) {
        this.baseUrl = baseUrl;
        this.client = RestClient.create();
    }

    /**
     * Checks if a user is authenticated to perform actions for a certain user or group
     *
     * @param user  The user which is performing an action
     * @param group The group which an action MAY be performed on
     * @return      a {@code boolean} representing if actions are allowed or not.
     */
    public boolean isUserAuthorized(String user, String group) {
        if (!StringUtils.hasText(user)) {
            throw new IllegalArgumentException("User cannot be empty");
        } else if (!StringUtils.hasText(group)) {
            return true;  // Users can always edit its own tools
        }

        // First, get all users inside the group
        UserWithRole[] userRolesInGroup = client.get()
                .uri(baseUrl + "/groups/" + group + "/users")
                .retrieve()
                .body(UserWithRole[].class);

        // Handle case where no users are currently in the group
        if (userRolesInGroup == null) {
            return false;
        }

        // Iterate through every user in the group.
        // Only when the given user has the Editor or Admin role APIs can be changed by the user in the group
        boolean isAuthorized = false;
        for (UserWithRole userWithRole : userRolesInGroup) {
            if (Objects.equals(userWithRole.getUser().getUserName(), user)
                    && ( Objects.equals(userWithRole.getRole(), "Editor")
                    || Objects.equals(userWithRole.getRole(), "Admin"))) {
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

        // Add the Api to the group
        client.post()
                .uri(baseUrl + "/groups/" + group + "/apis")
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiWithActive)
                .retrieve()
                .body(ApiWithActive.class);
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

        // Add the Api to the user
        client.post()
                .uri(baseUrl + "/users/" + user + "/apis")
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiWithActive)
                .retrieve()
                .body(ApiWithActive.class);
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

        // Remove the Api from the group
        client.delete()
                .uri(baseUrl + "/groups/" + group + "/apis/" + apiId)
                .retrieve()
                .toBodilessEntity();
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

        // Remove the Api from the user
        client.delete()
                .uri(baseUrl + "/users/" + user + "/apis/" + apiId)
                .retrieve()
                .toBodilessEntity();
    }

    /**
     * Fetches the APIs of either a user or a group and returns them.
     *
     * @param user  The user to get the APIs from
     * @param group The group to get the APIs from
     * @return      The APIs belonging to a user or a group
     */
    public ApiWithActive[] fetchApis(String user, String group) {
        if (!StringUtils.hasText(user)) {
            throw new IllegalArgumentException("User cannot be empty");
        }

        if (!StringUtils.hasText(group)) {  // When group == null, query the APIs of a user
            return client.get()
                    .uri(baseUrl + "/users/" + user + "/apis")
                    .retrieve()
                    .body(ApiWithActive[].class);

        } else {  // When group is present, query the APIs of a group
            return client.get()
                    .uri(baseUrl + "/groups/" + group + "/apis")
                    .retrieve()
                    .body(ApiWithActive[].class);
        }
    }
}
