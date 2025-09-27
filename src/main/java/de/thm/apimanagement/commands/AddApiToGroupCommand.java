package de.thm.apimanagement.commands;

import de.thm.apimanagement.client.UserManagementClient;
import de.thm.apimanagement.entity.ApiWithActive;

public class AddApiToGroupCommand implements Command {
    private final UserManagementClient userManagementClient;
    private final String group;
    private final ApiWithActive apiWithActive;

    public AddApiToGroupCommand(UserManagementClient userManagementClient, String group, ApiWithActive apiWithActive) {
        this.userManagementClient = userManagementClient;
        this.group = group;
        this.apiWithActive = apiWithActive;
    }

    @Override
    public void execute() {
        userManagementClient.addApiToGroup(group, apiWithActive);
    }

    @Override
    public void undo() {
        userManagementClient.deleteApiFromGroup(group, apiWithActive.getApiId());
    }
}
