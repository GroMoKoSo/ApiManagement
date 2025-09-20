package de.thm.apimanagement.client;

import de.thm.apimanagement.entity.ToolDefinition;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * McpManagementClient is responsible for communicating with the McpManagementClient subsystem
 *
 * @author Justin Wolek
 */
@Component
public class McpManagementClient {
    private final RestClient client;
    private final String baseUrl;

    public McpManagementClient(String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = RestClient.create();
    }

    /**
     * Adds an MCP tool defined as a {@link ToolDefinition} to the MCPManagement subsystem
     *
     * @param apiId         The id of the API to add tools for
     * @param definition    The API specification represented as a {@link ToolDefinition}
     */
    public void addTool(int apiId, ToolDefinition definition) {
        // TODO: Implement
    }

    /**
     * Removes one MCP tool in the MCPManagement subsystem
     *
     * @param apiId The id of the API to delete the tools of
     */
    public void deleteTool(int apiId) {
        // TODO: Implement
    }
}
