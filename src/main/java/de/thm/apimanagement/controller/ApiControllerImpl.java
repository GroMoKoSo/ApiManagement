package de.thm.apimanagement.controller;

import de.thm.apimanagement.entity.Api;
import de.thm.apimanagement.entity.InvokeQuery;
import de.thm.apimanagement.entity.InvokeResult;
import de.thm.apimanagement.security.TokenProvider;
import de.thm.apimanagement.service.ApiService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * ApiController provides endpoints to enable CRUD functionality for {@link Api} entities
 * in line with REST
 *
 * @author Benjamin Michael Lange-Hermstädt
 */
@RestController
public class ApiControllerImpl implements ApiManagementController {
    private final ApiService apiService;
    private final TokenProvider tokenProvider;

    ApiControllerImpl(TokenProvider tokenProvider, ApiService apiService) {
        this.tokenProvider = tokenProvider;
        this.apiService = apiService;
    }

    /**
     * Handles GET requests for /apis
     *
     * @return  A list of {@link Api}s
     */
    public ResponseEntity<List<Api>> getApis(
            @RequestParam("user") String user,
            @RequestParam(value = "group", required = false) String group) {
        tokenProvider.getToken();
        return ResponseEntity.ok(apiService.fetchApiList(user, group));
    }

    /**
     * Handles POST requests for /apis
     *
     * @param api   The {@link Api} object to POST
     * @return      The newly created object
     */
    public ResponseEntity<Api> postApi(
            @Validated @RequestBody Api api,
            @RequestParam("user") String user,
            @RequestParam(value = "group", required = false) String group) {
        tokenProvider.getToken();
        return ResponseEntity.ok(apiService.saveApi(api, user, group));
    }

    /**
     * Handles GET requests for /apis/{id}
     *
     * @param id    The id of the {@link Api} object to get
     * @return      The queried {@link Api} object
     */
    public ResponseEntity<Api> getApi(
            @PathVariable int id,
            @RequestParam("user") String user,
            @RequestParam(value = "group", required = false) String group) {
        tokenProvider.getToken();
        return ResponseEntity.ok(apiService.fetchApiById(id, user, group));
    }

    /**
     * Handles PUT requests for /apis/{id}
     *
     * @param api   The new {@link Api} object
     * @param id    The current {@link Api} object to be replaced
     * @return      The updated {@link Api} object
     */
    public ResponseEntity<Api> putApi(
            @RequestBody Api api,
            @PathVariable("id") int id,
            @RequestParam("user") String user,
            @RequestParam(value = "group", required = false) String group) {
        tokenProvider.getToken();
        return ResponseEntity.ok(apiService.updateApi(id, api, user, group));
    }

    /**
     * Handles DELETE requests for /apis/{id}
     *
     * @param id    The id of the {@link Api} to delete
     * @return      An http response with code 204 - No content on success
     */
    public ResponseEntity<?> deleteApi(
            @PathVariable("id") int id,
            @RequestParam("user") String user,
            @RequestParam(value = "group", required = false) String group) {
        tokenProvider.getToken();
        apiService.deleteApiById(id, user, group);
        return ResponseEntity.noContent().build();
    }


    public ResponseEntity<InvokeResult> invokeApi(
            @RequestBody InvokeQuery query,
            @PathVariable("id") int id,
            @RequestParam("user") String user,
            @RequestParam(value = "group", required = false) String group) {
        tokenProvider.getToken();
        return ResponseEntity.ok(apiService.invoke(id, user, group, query));
    }
}
