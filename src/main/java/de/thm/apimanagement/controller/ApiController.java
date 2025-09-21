package de.thm.apimanagement.controller;
import de.thm.apimanagement.entity.Api;
import de.thm.apimanagement.service.ApiService;
import jakarta.persistence.EntityNotFoundException;
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
    public List<Api> getApis() {
        return apiService.fetchApiList();
    }

    /**
     * Handles POST requests for /apis
     *
     * @param api   The {@link Api} object to POST
     * @return      The newly created object
     */
    @PostMapping("/apis")
    public Api postApi(@Validated @RequestBody Api api) {
        return apiService.saveApi(api);
    }

    /**
     * Handles GET requests for /apis/{id}
     *
     * @param id    The id of the {@link Api} object to get
     * @return      The queried {@link Api} object
     */
    @GetMapping("/apis/{id}")
    public ResponseEntity<Api> getApi(@PathVariable int id) {
        try {
            return ResponseEntity.ok(apiService.fetchApiById(id));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Handles PUT requests for /apis/{id}
     *
     * @param api   The new {@link Api} object
     * @param id    The current {@link Api} object to be replaced
     * @return      The updated {@link Api} object
     */
    @PutMapping("/apis/{id}")
    public ResponseEntity<Api> putApi(@RequestBody Api api, @PathVariable("id") int id) {
        try {
            return ResponseEntity.ok(apiService.updateApi(id, api));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Handles DELETE requests for /apis/{id}
     *
     * @param id    The id of the {@link Api} to delete
     * @return      An http response with code 204 - No content on success
     */
    @DeleteMapping("/apis/{id}")
    public ResponseEntity<Void> deleteApi(@PathVariable("id") int id) {
        try {
            apiService.deleteApiById(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
