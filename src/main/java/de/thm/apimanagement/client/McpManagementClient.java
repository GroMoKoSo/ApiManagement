package de.thm.apimanagement.client;

import de.thm.apimanagement.entity.ToolDefinition;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
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

    public McpManagementClient(@Value("${spring.subservices.mcp-management.url}") String baseUrl) {
        this.baseUrl = baseUrl;
        this.client = RestClient.create();
    }

    /**
     * Gets one tool with a specified id
     *
     * @param toolId    The id of the tool to get
     * @return          a {@link ToolDefinition} representing the tool
     */
    public ToolDefinition getToolWithId(int toolId) {
        return client.get()
                .uri(baseUrl + "toolsets/{toolId}", toolId)
                .retrieve()
                .body(ToolDefinition.class);
    }

    /**
     * Adds or Updates one MCP tool defined as a {@link ToolDefinition} to the MCPManagement subsystem
     *
     * @param toolId         The id of the API to add tools for
     * @param definition    The API specification represented as a {@link ToolDefinition}
     */
    public void addOrUpdateTool(int toolId, ToolDefinition definition) {
        client.put()
                .uri(baseUrl + "/toolsets/{toolId}", toolId)
                .contentType(MediaType.APPLICATION_JSON)
                .body(definition)
                .retrieve()
                .body(ToolDefinition.class);
    }

    /**
     * Removes one MCP tool in the MCPManagement subsystem
     *
     * @param apiId The id of the API to delete the tools of
     */
    public void deleteTool(int apiId) {
        client.delete().uri(baseUrl + "/toolsets/{apiId}", apiId);
    }
}
