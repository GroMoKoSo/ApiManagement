package de.thm.apimanagement.client;

import de.thm.apimanagement.client.exceptions.ClientExceptionHandler;
import de.thm.apimanagement.entity.ToolDefinition;
import de.thm.apimanagement.security.TokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private final Logger logger = LoggerFactory.getLogger(McpManagementClient.class);
    private final TokenProvider tokenProvider;
    private final RestClient client;

    public McpManagementClient(TokenProvider tokenProvider, @Value("${spring.subservices.mcp-management.url}") String baseUrl) {
        this.tokenProvider = tokenProvider;
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
    }

    /**
     * Gets one tool with a specified id
     *
     * @param toolId    The id of the tool to get
     * @return          a {@link ToolDefinition} representing the tool
     */
    public ToolDefinition getToolWithId(int toolId) {
        try {
            return client.get()
                    .uri("/toolsets/{toolId}", toolId)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .retrieve()
                    .body(ToolDefinition.class);
        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
        }
    }

    /**
     * Adds or Updates one MCP tool defined as a {@link ToolDefinition} to the MCPManagement subsystem
     *
     * @param toolId         The id of the API to add tools for
     * @param definition    The API specification represented as a {@link ToolDefinition}
     */
    public void addOrUpdateTool(int toolId, ToolDefinition definition) {
        try {
            client.put()
                    .uri("/toolsets/{toolId}", toolId)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(definition)
                    .retrieve()
                    .body(ToolDefinition.class);
        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
        }
    }

    /**
     * Removes one MCP tool in the MCPManagement subsystem
     *
     * @param apiId The id of the API to delete the tools of
     */
    public void deleteTool(int apiId) {
        try {
            client.delete()
                    .uri("/toolsets/{apiId}", apiId)
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .retrieve()
                    .toBodilessEntity();
        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
        }
    }
}
