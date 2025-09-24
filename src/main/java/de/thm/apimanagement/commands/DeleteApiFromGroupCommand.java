package de.thm.apimanagement.commands;

import de.thm.apimanagement.client.UserManagementClient;
import de.thm.apimanagement.entity.ApiWithActive;

public class DeleteApiFromGroupCommand implements Command {
    private final UserManagementClient userManagementClient;
    private final String group;
    private final ApiWithActive apiWithActive;

    public DeleteApiFromGroupCommand(UserManagementClient userManagementClient, String group, ApiWithActive apiWithActive) {
        this.userManagementClient = userManagementClient;
        this.group = group;
        this.apiWithActive = apiWithActive;
    }

    @Override
    public void execute() {
        userManagementClient.deleteApiFromGroup(group, apiWithActive.getApiId());
    }

    @Override
    public void undo() {
        userManagementClient.addApiToGroup(group, apiWithActive);
    }
}
