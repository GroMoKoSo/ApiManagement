package de.thm.apimanagement.controller;

import de.thm.apimanagement.model.Api;
import de.thm.apimanagement.model.InvokeQuery;
import de.thm.apimanagement.model.InvokeResult;
import de.thm.apimanagement.service.ApiService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
        import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/apis")
public class ApiController {

    private final ApiService apiService;

    public ApiController(ApiService apiService) {
        this.apiService = apiService;
    }


    @GetMapping
    public List<Api> getApis(@RequestParam(required = false) String query,
                             @RequestParam(required = false) String dataFormat,
                             @RequestParam(required = false) String users,
                             @RequestParam(required = false) String group) {
        return apiService.findApis(query, dataFormat, users, group);
    }


    @PostMapping
    public ResponseEntity<Api> addApi(@Valid @RequestBody Api api) {
        Api created = apiService.create(api);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(created.getId())
                .toUri();
        return ResponseEntity.created(location).body(created);
    }


    @GetMapping("/{id}")
    public Api getApi(@PathVariable("id") int apiId) {
        return apiService.get(apiId);
    }


    @PutMapping("/{id}")
    public Api editApi(@PathVariable("id") int apiId,
                       @Valid @RequestBody Api api) {
        return apiService.update(apiId, api);
    }


    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteApi(@PathVariable("id") int apiId) {
        apiService.delete(apiId);
    }


    @PostMapping("/{id}/invoke")
    public InvokeResult invoke(@PathVariable("id") int apiId,
                               @Valid @RequestBody InvokeQuery query) {
        return apiService.invoke(apiId, query);
    }
}