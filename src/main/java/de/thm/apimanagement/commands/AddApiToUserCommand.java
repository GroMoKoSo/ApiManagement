package de.thm.apimanagement.commands;

import de.thm.apimanagement.client.UserManagementClient;
import de.thm.apimanagement.entity.ApiWithActive;

public class AddApiToUserCommand implements Command {
    private final UserManagementClient userManagementClient;
    private final String user;
    private final ApiWithActive apiWithActive;

    public AddApiToUserCommand(UserManagementClient userManagementClient, String user, ApiWithActive apiWithActive) {
        this.userManagementClient = userManagementClient;
        this.user = user;
        this.apiWithActive = apiWithActive;
    }

    @Override
    public void execute() {
        userManagementClient.addApiToUser(user, apiWithActive);
    }

    @Override
    public void undo() {
        userManagementClient.deleteApiFromUser(user, apiWithActive.getApiId());
    }
}
