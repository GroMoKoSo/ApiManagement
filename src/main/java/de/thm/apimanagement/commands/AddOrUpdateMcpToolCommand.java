package de.thm.apimanagement.commands;

import de.thm.apimanagement.client.McpManagementClient;
import de.thm.apimanagement.entity.ToolDefinition;

public class AddOrUpdateMcpToolCommand implements Command {
    private final McpManagementClient mcpManagementClient;
    private final ToolDefinition toolDefinition;
    private final int apiId;

    public AddOrUpdateMcpToolCommand(McpManagementClient mcpManagementClient, ToolDefinition toolDefinition, int apiId) {
        this.mcpManagementClient = mcpManagementClient;
        this.toolDefinition = toolDefinition;
        this.apiId = apiId;
    }

    @Override
    public void execute() {
        mcpManagementClient.addOrUpdateTool(apiId, toolDefinition);
    }

    @Override
    public void undo() {
        mcpManagementClient.deleteTool(apiId);
    }
}
