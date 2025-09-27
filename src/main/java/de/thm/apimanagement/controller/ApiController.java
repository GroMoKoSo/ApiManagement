package de.thm.apimanagement.controller;
import de.thm.apimanagement.entity.Api;
import de.thm.apimanagement.entity.InvokeQuery;
import de.thm.apimanagement.entity.InvokeResult;
import de.thm.apimanagement.service.ApiService;
import de.thm.apimanagement.service.exceptions.ServiceNotAllowed;
import de.thm.apimanagement.service.exceptions.ServiceNotFound;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * ApiController provides endpoints to enable CRUD functionality for {@link Api} entities
 * in line with REST
 *
 * @author Benjamin Michael Lange-Hermstädt
 */
@RestController
public class ApiController {
    private final ApiService apiService;

    ApiController(ApiService apiService) {
        this.apiService = apiService;
    }

    /**
     * Handles GET requests for /apis
     *
     * @return  A list of {@link Api}s
     */
    @GetMapping("/apis")
    public ResponseEntity<List<Api>> getApis() {
        return ResponseEntity.ok(apiService.fetchApiList());
    }

    /**
     * Handles POST requests for /apis
     *
     * @param api   The {@link Api} object to POST
     * @return      The newly created object
     */
    @PostMapping("/apis")
    public ResponseEntity<Api> postApi(
            @Validated @RequestBody Api api,
            @RequestParam("user") String user,
            @RequestParam(value = "group", required = false) String group) {
        try {
            return ResponseEntity.ok(apiService.saveApi(api, user, group));
        } catch (Exception e) {
            if (e instanceof ServiceNotAllowed) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            else if (e instanceof ServiceNotFound) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    /**
     * Handles GET requests for /apis/{id}
     *
     * @param id    The id of the {@link Api} object to get
     * @return      The queried {@link Api} object
     */
    @GetMapping("/apis/{id}")
    public ResponseEntity<Api> getApi(@PathVariable int id) {
        return ResponseEntity.ok(apiService.fetchApiById(id));
    }

    /**
     * Handles PUT requests for /apis/{id}
     *
     * @param api   The new {@link Api} object
     * @param id    The current {@link Api} object to be replaced
     * @return      The updated {@link Api} object
     */
    @PutMapping("/apis/{id}")
    public ResponseEntity<Api> putApi(
            @RequestBody Api api,
            @PathVariable("id") int id,
            @RequestParam("user") String user,
            @RequestParam(value = "group", required = false) String group) {
        try {
            return ResponseEntity.ok(apiService.updateApi(id, api, user, group));
        } catch (Exception e) {
            if (e instanceof ServiceNotAllowed) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            else if (e instanceof ServiceNotFound) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    /**
     * Handles DELETE requests for /apis/{id}
     *
     * @param id    The id of the {@link Api} to delete
     * @return      An http response with code 204 - No content on success
     */
    @DeleteMapping("/apis/{id}")
    public ResponseEntity<?> deleteApi(
            @PathVariable("id") int id,
            @RequestParam("user") String user,
            @RequestParam(value = "group", required = false) String group) {
        try {
            apiService.deleteApiById(id, user, group);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            if (e instanceof ServiceNotAllowed) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            else if (e instanceof ServiceNotFound) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/apis/{id}/invoke")
    public ResponseEntity<InvokeResult> invokeApi(
            @RequestBody InvokeQuery query,
            @PathVariable("id") int id,
            @RequestParam("user") String user,
            @RequestParam(value = "group", required = false) String group) {
        try {
            return ResponseEntity.ok(apiService.invoke(id, user, group, query));
        } catch (Exception e) {
            if (e instanceof ServiceNotAllowed) return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
            else if (e instanceof ServiceNotFound) return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            else return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
