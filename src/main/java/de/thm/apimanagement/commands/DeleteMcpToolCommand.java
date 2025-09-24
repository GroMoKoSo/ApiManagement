package de.thm.apimanagement.commands;

import de.thm.apimanagement.client.McpManagementClient;
import de.thm.apimanagement.entity.ToolDefinition;

public class DeleteMcpToolCommand implements Command {
    private final McpManagementClient mcpManagementClient;
    private final int apiId;
    private final ToolDefinition toolDefinition;

    public DeleteMcpToolCommand(McpManagementClient mcpManagementClient, int apiId, ToolDefinition toolDefinition) {
        this.mcpManagementClient = mcpManagementClient;
        this.apiId = apiId;
        this.toolDefinition = toolDefinition;
    }

    @Override
    public void execute() {
        mcpManagementClient.deleteTool(apiId);
    }

    @Override
    public void undo() {
        mcpManagementClient.addOrUpdateTool(apiId, toolDefinition);
    }
}
