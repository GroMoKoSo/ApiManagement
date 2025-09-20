package de.thm.apimanagement.client;

import de.thm.apimanagement.entity.Api;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * UserManagementClient is responsible for communicating with the UserManagementClient subsystem
 *
 * @author Justin Wolek
 */
@Component
public class UserManagementClient {
    private final RestClient client;

    @Value("${spring.subservices.user-management.url}")
    private String baseUrl;

    public UserManagementClient() {
        this.client = RestClient.create();
    }

    /**
     * Checks if a user is authenticated to perform certain actions for users or groups
     *
     * @param user  The user which is performing an action
     * @param group The group which action MAY be performed on
     * @return      a {@code boolean} representing if the action can be performed on or not.
     */
    public boolean isUserAuthorized(String user, String group) {
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User cannot be empty");
        }

        // TODO: Implement user validation

        return false;
    }

    /**
     * Fetches the APIs of either a user or a group and returns them
     *
     * @param user  The user to get the APIs from
     * @param group The group to get the APIs from
     * @return      The APIs belonging to a user or a group
     */
    public Api[] fetchApis(String user, String group) {
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User cannot be empty");
        }

        if (group.isEmpty()) {  // When group == null, query the APIs of a user
            return client.get()
                    .uri(baseUrl + "/users/" + user + "/apis")
                    .retrieve()
                    .body(Api[].class);

        } else {  // When group is present, query the APIs of a group
            return client.get()
                    .uri(baseUrl + "/groups/" + group + "/apis")
                    .retrieve()
                    .body(Api[].class);
        }

    }
}
