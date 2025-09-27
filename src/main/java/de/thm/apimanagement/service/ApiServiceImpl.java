package de.thm.apimanagement.service;

import de.thm.apimanagement.client.ExternalApiClient;
import de.thm.apimanagement.client.McpManagementClient;
import de.thm.apimanagement.client.Spec2ToolClient;
import de.thm.apimanagement.client.UserManagementClient;
import de.thm.apimanagement.commands.*;
import de.thm.apimanagement.entity.*;
import de.thm.apimanagement.repository.ApiRepository;
import de.thm.apimanagement.service.exceptions.ServiceError;
import de.thm.apimanagement.service.exceptions.ServiceNotAllowed;
import de.thm.apimanagement.service.exceptions.ServiceNotFound;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

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
    public Api saveApi(Api api, String user, String group) {
        Stack<Command> commands = new Stack<>();

        if (!StringUtils.hasText(user)) {
            throw new ServiceError("A user must be provided!");
        }

        logger.info("====== Starting Save Api Transaction ======");
        try {
            logger.debug("Checking if operation is authorized...");
            if (!userManagementClient.isUserAuthorized(user, group)) {
                logger.error("Operation is not allower! Aborting...");
                throw new HttpClientErrorException(HttpStatus.FORBIDDEN, "Operation not allowed");
            }

            logger.debug("Saving api to repository...");
            SaveApiToRepositoryCommand saveApiToRepositoryCommand = new SaveApiToRepositoryCommand(
                    apiRepository, api);
            saveApiToRepositoryCommand.execute();
            commands.add(saveApiToRepositoryCommand);

            // If no group has been provided, add the api to the user, otherwise to the group
            if (!StringUtils.hasText(group)) {
                logger.debug("Adding api to user...");
                AddApiToUserCommand addApiToUserCommand = new AddApiToUserCommand(
                        userManagementClient, user, new ApiWithActive(api.getId(), true));
                addApiToUserCommand.execute();
                commands.add(addApiToUserCommand);
            } else {
                logger.debug("Adding api to group...");
                AddApiToGroupCommand addApiToGroupCommand = new AddApiToGroupCommand(
                        userManagementClient, group, new ApiWithActive(api.getId(), true));
                addApiToGroupCommand.execute();
                commands.add(addApiToGroupCommand);
            }

            logger.debug("Converting api specification to MCP tool...");
            ToolDefinition toolDef = spec2ToolClient.convertSpec2Tool(
                    api.getFormat(),
                    api.getFileType(),
                    api.getSpec());

            logger.debug("Updating MCP tool in MCP server...");
            AddOrUpdateMcpToolCommand addOrUpdateMcpToolCommand = new AddOrUpdateMcpToolCommand(
                    mcpManagementClient, toolDef, api.getId());
            addOrUpdateMcpToolCommand.execute();
            commands.add(addOrUpdateMcpToolCommand);

        } catch (HttpClientErrorException e) {
            handleFailure(e, commands);
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.FORBIDDEN) throw new ServiceNotAllowed("Operation not allowed!");
            else if (status == HttpStatus.NOT_FOUND) throw new ServiceNotFound("Resource not found");
            else throw new ServiceError(e.getMessage());

        } catch (Exception e) {
            handleFailure(e, commands);
            throw new ServiceError(e.getMessage());
        }

        logger.info("====== Ending Transaction: SUCCESS ======");
        return api;
    }

    @Override
    public Api updateApi(int apiId, Api api, String user, String group) {
        Stack<Command> commands = new Stack<>();

        if (!StringUtils.hasText(user)) {
            throw new ServiceError("A user must be provided!");
        }

        logger.info("====== Starting Update Api Transaction ======");
        try {
            logger.debug("Checking if api exists in repository...");
            if (apiRepository.findById(apiId).orElse(null) == null) {
                logger.error("Api with id " + apiId + " does not exist!");
                throw new ResponseStatusException(NOT_FOUND, "Api does not exist!");
            }

            logger.debug("Checking if operation is authorized...");
            if (!userManagementClient.isUserAuthorized(user, group)) {
                logger.error("Operation is not authorized! Aborting...");
                throw new ResponseStatusException(FORBIDDEN, "Operation is not authorized!");
            }

            logger.debug("Checking if api exists in userManagement...");
            List<ApiWithActive> apiWithActive = Collections.emptyList();
            if (StringUtils.hasText(user) && !StringUtils.hasText(group)) {
                apiWithActive = Arrays.asList(userManagementClient.getApisOfUser(user));
            } else if (StringUtils.hasText(user) && StringUtils.hasText(group)) {
                apiWithActive = Arrays.asList(userManagementClient.getApisOfGroup(group));
            }

            boolean isApiPresent = apiWithActive.stream()
                    .anyMatch(a -> a.getApiId() == apiId);

            if (!isApiPresent) {
                throw new ResponseStatusException(NOT_FOUND, "Api does not exist!");
            }

            logger.debug("Updating api in repository...");
            UpdateApiInRepositoryCommand updateApiInRepositoryCommand = new UpdateApiInRepositoryCommand(
                    apiRepository, apiId, api);
            updateApiInRepositoryCommand.execute();
            commands.add(updateApiInRepositoryCommand);

            logger.debug("Converting api specification to MCP tool...");
            ToolDefinition toolDef = spec2ToolClient.convertSpec2Tool(
                    api.getFormat(),
                    api.getFileType(),
                    api.getSpec());

            logger.debug("Updating MCP tool in McpManagement...");
            AddOrUpdateMcpToolCommand addOrUpdateMcpToolCommand = new AddOrUpdateMcpToolCommand(
                    mcpManagementClient, toolDef, apiId);
            addOrUpdateMcpToolCommand.execute();
            commands.add(addOrUpdateMcpToolCommand);

        } catch (HttpClientErrorException e) {
            handleFailure(e, commands);
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.FORBIDDEN) throw new ServiceNotAllowed("Operation not allowed!");
            else if (status == HttpStatus.NOT_FOUND) throw new ServiceNotFound("Resource not found");
            else throw new ServiceError(e.getMessage());

        } catch (Exception e) {
            handleFailure(e, commands);
            throw new ServiceError(e.getMessage());
        }

        logger.info("====== Ending Transaction: SUCCESS ======");
        return api;
    }

    @Override
    public void deleteApiById(int apiId, String user, String group) {
        Stack<Command> commands = new Stack<>();

        if (!StringUtils.hasText(user)) {
            throw new ServiceError("A user must be provided!");
        }

        logger.info("====== Starting Delete Api Transaction ======");
        try {
            logger.debug("Checking if api exists...");
            Api api = apiRepository.findById(apiId).orElse(null);
            if (api == null) throw new ResponseStatusException(NOT_FOUND, "Api does not exist!");

            logger.debug("Checking if operation is authorized...");
            if (!userManagementClient.isUserAuthorized(user, group)) {
                logger.error("Operation is not authorized! Aborting...");
                throw new ResponseStatusException(FORBIDDEN, "Operation is not authorized!");
            }

            logger.debug("Checking if api exists in userManagement...");
            List<ApiWithActive> apiWithActive = Collections.emptyList();
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
                throw new ResponseStatusException(NOT_FOUND, "Api does not exist!");
            }

            if (!StringUtils.hasText(group)) {
                logger.debug("Deleting api in user in UserManagement...");
                DeleteApiFromUserCommand deleteApiFromUserCommand = new DeleteApiFromUserCommand(
                        userManagementClient, user, userManagementApiBackup);
                deleteApiFromUserCommand.execute();
                commands.add(deleteApiFromUserCommand);
            } else {
                logger.debug("Deleting api in group in UserManagement...");
                DeleteApiFromGroupCommand deleteApiFromGroupCommand = new DeleteApiFromGroupCommand(
                        userManagementClient, group, userManagementApiBackup);
                deleteApiFromGroupCommand.execute();
                commands.add(deleteApiFromGroupCommand);
            }


            logger.debug("Deleting MCP tool in MCP server...");
            ToolDefinition mcpManagementToolBackup = mcpManagementClient.getToolWithId(apiId);
            if (mcpManagementToolBackup != null) {
                DeleteMcpToolCommand deleteMcpToolCommand = new DeleteMcpToolCommand(
                        mcpManagementClient, apiId, mcpManagementToolBackup);
                deleteMcpToolCommand.execute();
                commands.add(deleteMcpToolCommand);
            }

            logger.debug("Deleting api in repository...");
            DeleteApiFromRepositoryCommand deleteApiFromRepositoryCommand = new DeleteApiFromRepositoryCommand(
                    apiRepository, api);
            deleteApiFromRepositoryCommand.execute();
            commands.add(deleteApiFromRepositoryCommand);

        } catch (HttpClientErrorException e) {
            handleFailure(e, commands);
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.FORBIDDEN) throw new ServiceNotAllowed("Operation not allowed!");
            else if (status == HttpStatus.NOT_FOUND) throw new ServiceNotFound("Resource not found");
            else throw new ServiceError(e.getMessage());

        } catch (Exception e) {
            handleFailure(e, commands);
            throw new ServiceError(e.getMessage());
        }

        logger.info("====== Ending Transaction: SUCCESS ======");
    }

    @Override
    public List<Api> fetchApiList() {
        return (List<Api>) apiRepository.findAll();
    }

    @Override
    public Api fetchApiById(int apiId) {
        Api api = apiRepository.findById(apiId).orElse(null);
        if (api == null) throw new ServiceNotFound("Resource not found");
        else return api;
    }

    @Override
    public InvokeResult invoke(int apiId, String user, String group, InvokeQuery query) {
        logger.debug("Checking if api exists...");
        Api api = apiRepository.findById(apiId).orElse(null);
        if (api == null) throw new ServiceNotFound("Api does not exist!");

        try {
            logger.debug("Checking if invoke is allowed...");
            if (!userManagementClient.canInvoke(user, group)) {
                throw new ServiceNotAllowed("Invoke not allowed!");
            }

            logger.debug("Checking if api exists in userManagement...");
            List<ApiWithActive> apiWithActive = Collections.emptyList();
            if (StringUtils.hasText(user) && !StringUtils.hasText(group)) {
                apiWithActive = Arrays.asList(userManagementClient.getApisOfUser(user));
            } else if (StringUtils.hasText(user) && StringUtils.hasText(group)) {
                apiWithActive = Arrays.asList(userManagementClient.getApisOfGroup(group));
            }

            boolean isApiPresent = apiWithActive.stream()
                    .anyMatch(a -> a.getApiId() == apiId);

            if (!isApiPresent) {
                throw new ResponseStatusException(NOT_FOUND, "Api does not exist!");
            }
        } catch (HttpClientErrorException e) {
            HttpStatusCode status = e.getStatusCode();
            if (status == HttpStatus.FORBIDDEN) throw new ServiceNotAllowed("Operation not allowed!");
            else if (status == HttpStatus.NOT_FOUND) throw new ServiceNotFound("Resource not found");
            else throw new ServiceError(e.getMessage());

        } catch (Exception e) {
            throw new ServiceError(e.getMessage());
        }

        return externalApiClient.invoke(query);
    }

    private void rollback(Stack<Command> commands) {
        for (Command command : commands) {
            logger.warn("Reverting: {}", command.toString());
            command.undo();
        }
    }

    private void handleFailure(Exception e, Stack<Command> commands) {
        logger.error("Error in Transaction! Reason: {}", e.getMessage());
        rollback(commands);
        logger.info("====== Ending Transaction: FAILED ======");
    }
}
