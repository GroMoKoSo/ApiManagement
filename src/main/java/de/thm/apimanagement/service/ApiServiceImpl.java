package de.thm.apimanagement.service;

import de.thm.apimanagement.client.Spec2ToolClient;
import de.thm.apimanagement.client.UserManagementClient;
import de.thm.apimanagement.entity.Api;
import de.thm.apimanagement.entity.InvokeQuery;
import de.thm.apimanagement.entity.InvokeResult;
import de.thm.apimanagement.repository.ApiRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Implementation of ApiService. Contains the business logic of the microservice
 * Meant to be used by a RESTController
 *
 * @author Justin Wolek
 */
@Service
public class ApiServiceImpl implements de.thm.apimanagement.service.ApiService {
    private ApiRepository apiRepository;
    private Spec2ToolClient spec2ToolClient;
    private UserManagementClient userManagementClient;

    ApiServiceImpl(ApiRepository apiRepository) {
        this.apiRepository = apiRepository;
        this.spec2ToolClient = new Spec2ToolClient();
        this.userManagementClient = new UserManagementClient();
    }

    @Override
    public Api saveApi(Api api) {
        // TODO:
        // - Check if user is authorized
        // - When not authorized:
        //      - return 401
        // - When authorized:
        //      - Call convertSpec2Tool()
        //      - Call addTool()

        return apiRepository.save(api);
    }

    @Override
    public Api updateApi(int apiId, Api api) {
        // TODO:
        // - Check if user is authorized
        // - When not authorized:
        //      - return 401
        // - When authorized:
        //      - Call convertSpec2Tool()
        //      - Call removeTool()
        //      - Call addTool()

        return apiRepository.findById(apiId)
                .map(apiDb -> {
                    if (api.getName() != null && !api.getName().isBlank()) {
                        apiDb.setName(api.getName());
                    }
                    if (api.getDescription() != null && !api.getDescription().isBlank()) {
                        apiDb.setDescription(api.getDescription());
                    }
                    if (api.getVersion() != null && !api.getVersion().isBlank()) {
                        apiDb.setVersion(api.getVersion());
                    }
                    if (api.getDataFormat() != null && !api.getDataFormat().isBlank()) {
                        apiDb.setDataFormat(api.getDataFormat());
                    }
                    if (api.getSpec() != null && !api.getSpec().isBlank()) {
                        apiDb.setSpec(api.getSpec());
                    }
                    if (api.getToken() != null && !api.getToken().isBlank()) {
                        apiDb.setToken(api.getToken());
                    }
                    return apiRepository.save(apiDb);
                })
                .orElseThrow(() -> new EntityNotFoundException("API with id " + apiId + " not found"));
    }

    @Override
    public void deleteApiById(int apiId) {
        // TODO:
        // - Check if user is authorized
        // - When not authorized:
        //      - return 401
        // - When authorized:
        //      - Call removeTool()

        apiRepository.findById(apiId)
                .ifPresentOrElse(
                        apiRepository::delete,
                        () -> { throw new EntityNotFoundException("API with id " + apiId + " not found"); }
                );
    }

    @Override
    public List<Api> fetchApiList() {
        return (List<Api>) apiRepository.findAll();
    }

    @Override
    public Api fetchApiById(int apiId) {
        return apiRepository.findById(apiId)
                .orElseThrow(() -> new EntityNotFoundException("API with id " + apiId + " not found"));
    }

    @Override
    public InvokeResult invoke(InvokeQuery query) {
        // TODO: Implement
        return null;
    }

    @Override
    public String formatRequestPath(InvokeQuery query) {
        // TODO: Implement
        return "";
    }
}
