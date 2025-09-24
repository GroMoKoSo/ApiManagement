package de.thm.apimanagement.service;

import de.thm.apimanagement.client.ExternalApiClient;
import de.thm.apimanagement.client.McpManagementClient;
import de.thm.apimanagement.client.Spec2ToolClient;
import de.thm.apimanagement.client.UserManagementClient;
import de.thm.apimanagement.commands.*;
import de.thm.apimanagement.entity.*;
import de.thm.apimanagement.repository.ApiRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

/**
 * Implementation of ApiService. Contains the business logic of the microservice
 * Meant to be used by a RESTController
 *
 * @author Justin Wolek
 */
@Service
public class ApiServiceImpl implements ApiService {
    private final ApiRepository apiRepository;

    @Autowired private Spec2ToolClient spec2ToolClient;
    @Autowired private UserManagementClient userManagementClient;
    @Autowired private McpManagementClient mcpManagementClient;
    @Autowired private ExternalApiClient externalApiClient;

    Logger logger = LoggerFactory.getLogger(ApiServiceImpl.class);

    ApiServiceImpl(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
    }

    @Override
    public ResponseEntity<Api> saveApi(Api api, String user, String group) {
        Stack<Command> commands = new Stack<>();

        if (!StringUtils.hasText(user)) {
            return ResponseEntity.badRequest().build();
        }

        logger.info("====== Starting Save Api Transaction ======");
        try {
            logger.info("Checking if operation is authorized...");
            if (!userManagementClient.isUserAuthorized(user, group)) {
                logger.error("Operation is not authorized! Aborting...");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operation is not authorized!");
            }

            logger.info("Saving api to repository...");
            SaveApiToRepositoryCommand saveApiToRepositoryCommand = new SaveApiToRepositoryCommand(
                    apiRepository, api);
            saveApiToRepositoryCommand.execute();
            commands.add(saveApiToRepositoryCommand);

            // If no group has been provided, add the api to the user, otherwise to the group
            if (!StringUtils.hasText(group)) {
                logger.info("Adding api to user...");
                AddApiToUserCommand addApiToUserCommand = new AddApiToUserCommand(
                        userManagementClient, user, new ApiWithActive(api.getId(), true));
                addApiToUserCommand.execute();
                commands.add(addApiToUserCommand);
            } else {
                logger.info("Adding api to group...");
                AddApiToGroupCommand addApiToGroupCommand = new AddApiToGroupCommand(
                        userManagementClient, group, new ApiWithActive(api.getId(), true));
                addApiToGroupCommand.execute();
                commands.add(addApiToGroupCommand);
            }

            logger.info("Converting api specification to MCP tool...");
            ToolDefinition toolDef = spec2ToolClient.convertSpec2Tool(
                    api.getFormat(),
                    api.getFileType(),
                    api.getSpec());

            logger.info("Updating MCP tool in MCP server...");
            AddOrUpdateMcpToolCommand addOrUpdateMcpToolCommand = new AddOrUpdateMcpToolCommand(
                    mcpManagementClient, toolDef, api.getId());
            addOrUpdateMcpToolCommand.execute();
            commands.add(addOrUpdateMcpToolCommand);

        } catch (Exception e) {
            logger.error("Error in Transaction! Reason: {}", e.getMessage());
            rollback(commands);
            logger.info("====== Ending Transaction: FAILED ======");
            throw e;
        }

        logger.info("====== Ending Transaction: SUCCESS ======");
        return ResponseEntity.ok(api);
    }

    @Override
    public ResponseEntity<Api> updateApi(int apiId, Api api, String user, String group) {
        Stack<Command> commands = new Stack<>();

        if (!StringUtils.hasText(user)) {
            return ResponseEntity.badRequest().build();
        }

        logger.info("====== Starting Update Api Transaction ======");
        try {
            logger.info("Checking if api exists...");
            if (apiRepository.findById(apiId).orElse(null) == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Api does not exist!");
            }

            logger.info("Checking if operation is authorized...");
            if (!userManagementClient.isUserAuthorized(user, group)) {
                logger.error("Operation is not authorized! Aborting...");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operation is not authorized!");
            }

            // Save the current state of the api in userManagement so it can be recovered later
            ArrayList<ApiWithActive> apiWithActive = null;
            if (StringUtils.hasText(user) && !StringUtils.hasText(group)) {
                apiWithActive = new ArrayList<>(Arrays.asList(userManagementClient.getApisOfUser(user)));
            } else if (StringUtils.hasText(user) && StringUtils.hasText(group)) {
                apiWithActive = new ArrayList<>(Arrays.asList(userManagementClient.getApisOfGroup(group)));
            }

            boolean isApiPresent = false;
            for (ApiWithActive a : apiWithActive) {
                if (a.getApiId() == apiId) isApiPresent = true;
            }

            if (!isApiPresent) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Api does not exist!");
            }

            logger.info("Updating api in repository...");
            UpdateApiInRepositoryCommand updateApiInRepositoryCommand = new UpdateApiInRepositoryCommand(
                    apiRepository, apiId, api);
            updateApiInRepositoryCommand.execute();
            commands.add(updateApiInRepositoryCommand);

            logger.info("Converting api specification to MCP tool...");
            ToolDefinition toolDef = spec2ToolClient.convertSpec2Tool(
                    api.getFormat(),
                    api.getFileType(),
                    api.getSpec());

            logger.info("Updating MCP tool in MCP server...");
            AddOrUpdateMcpToolCommand addOrUpdateMcpToolCommand = new AddOrUpdateMcpToolCommand(
                    mcpManagementClient, toolDef, apiId);
            addOrUpdateMcpToolCommand.execute();
            commands.add(addOrUpdateMcpToolCommand);

        } catch (Exception e) {
            logger.error("Error in Transaction! Reason: {}", e.getMessage());
            rollback(commands);
            logger.info("====== Ending Transaction: FAILED ======");
            throw e;
        }

        logger.info("====== Ending Transaction: SUCCESS ======");
        return ResponseEntity.ok(apiRepository.findById(apiId).orElse(null));
    }

    @Override
    public ResponseEntity<?> deleteApiById(int apiId, String user, String group) {
        Stack<Command> commands = new Stack<>();

        if (!StringUtils.hasText(user)) {
            return ResponseEntity.badRequest().build();
        }

        logger.info("====== Starting Delete Api Transaction ======");
        try {
            logger.info("Checking if api exists...");
            Api api = apiRepository.findById(apiId).orElse(null);
            if (api == null) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Api does not exist!");

            logger.info("Checking if operation is authorized...");
            if (!userManagementClient.isUserAuthorized(user, group)) {
                logger.error("Operation is not authorized! Aborting...");
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Operation is not authorized!");
            }

            // Save the current state of the api in userManagement so it can be recovered later
            ArrayList<ApiWithActive> apiWithActive = null;
            if (StringUtils.hasText(user) && !StringUtils.hasText(group)) {
                apiWithActive = new ArrayList<>(Arrays.asList(userManagementClient.getApisOfUser(user)));
            } else if (StringUtils.hasText(user) && StringUtils.hasText(group)) {
                apiWithActive = new ArrayList<>(Arrays.asList(userManagementClient.getApisOfGroup(group)));
            }

            ApiWithActive userManagementApiBackup = null;
            for (ApiWithActive a : apiWithActive) {
                if (a.getApiId() == api.getId()) userManagementApiBackup = a;
            }

            if (userManagementApiBackup == null) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Api does not exist!");
            }

            if (!StringUtils.hasText(group)) {
                logger.info("Deleting api in user in UserManagement...");
                DeleteApiFromUserCommand deleteApiFromUserCommand = new DeleteApiFromUserCommand(
                        userManagementClient, user, userManagementApiBackup);
                deleteApiFromUserCommand.execute();
                commands.add(deleteApiFromUserCommand);
            } else {
                logger.info("Deleting api in group in UserManagement...");
                DeleteApiFromGroupCommand deleteApiFromGroupCommand = new DeleteApiFromGroupCommand(
                        userManagementClient, group, userManagementApiBackup);
                deleteApiFromGroupCommand.execute();
                commands.add(deleteApiFromGroupCommand);
            }


            logger.info("Deleting MCP tool in MCP server...");
            ToolDefinition mcpManagementToolBackup = mcpManagementClient.getToolWithId(apiId);
            if (mcpManagementToolBackup != null) {
                DeleteMcpToolCommand deleteMcpToolCommand = new DeleteMcpToolCommand(
                        mcpManagementClient, apiId, mcpManagementToolBackup);
                deleteMcpToolCommand.execute();
                commands.add(deleteMcpToolCommand);
            }

            logger.info("Deleting api in repository...");
            DeleteApiFromRepositoryCommand deleteApiFromRepositoryCommand = new DeleteApiFromRepositoryCommand(
                    apiRepository, api);
            deleteApiFromRepositoryCommand.execute();
            commands.add(deleteApiFromRepositoryCommand);

        } catch (Exception e) {
            logger.error("Error in Transaction! Reason: {}", e.getMessage());
            rollback(commands);
            logger.info("====== Ending Transaction: FAILED ======");
            throw e;
        }

        logger.info("====== Ending Transaction: SUCCESS ======");
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<List<Api>> fetchApiList() {
        return ResponseEntity.ok((List<Api>) apiRepository.findAll());
    }

    @Override
    public ResponseEntity<Api> fetchApiById(int apiId) {
        return apiRepository.findById(apiId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<InvokeResult> invoke(InvokeQuery query) {
        return ResponseEntity.ok(externalApiClient.invoke(query));
    }

    private void rollback(Stack<Command> commands) {
        for (Command command : commands) {
            logger.warn("Reverting: {}", command.toString());
            command.undo();
        }
    }
}
