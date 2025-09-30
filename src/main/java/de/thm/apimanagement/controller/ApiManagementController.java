package de.thm.apimanagement.controller;
import de.thm.apimanagement.entity.Api;
import de.thm.apimanagement.entity.InvokeQuery;
import de.thm.apimanagement.entity.InvokeResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@Tag(name = "APIs", description = "CRUD & invocation for API definitions")

public interface ApiManagementController {

    @Operation(
            summary = "List APIs",
            description = "Returns all API definitions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of APIs returned",
                    content = {@Content(mediaType = "application/json",
                    array = @ArraySchema(schema = @Schema(implementation = Api.class)))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized")
    })
    @GetMapping("/apis")
    ResponseEntity<List<Api>> getApis(
            @Parameter(description = "Requesting user", required = true) String user,
            @Parameter(description = "Requesting group") String group);

    @Operation(
            summary = "Create an API",
            description = "Creates a new API definition."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API created",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Api.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "500", description = "Internal Server error")
    })
    @PostMapping("/apis")
    ResponseEntity<Api> postApi(
            @RequestBody Api api,
            @Parameter(description = "Requesting user", required = true) String user,
            @Parameter(description = "Requesting group") String group
    );

    @Operation(
            summary = "Get API by id",
            description = "Fetch a single API definition by its id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API returned",
                    content = { @Content(mediaType= "application/json",
                    schema = @Schema(implementation = Api.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "404", description = "Not found"),
    })
    @GetMapping("/apis/{id}")
    ResponseEntity<Api> getApi(
            @PathVariable int id,
            @Parameter(description = "Requesting user", required = true) String user,
            @Parameter(description = "Requesting group") String group
    );

    @Operation(
            summary = "Replace an API",
            description = "Replaces an existing API definition by id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "API updated",
                    content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Api.class))}),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "404", description = "API or related service not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/apis/{id}")
    ResponseEntity<Api> putApi(
            @RequestBody Api api,
            @PathVariable int id,
            @Parameter(description = "Requesting user", required = true) String user,
            @Parameter(description = "Optional group") String group
    );

    @Operation(
            summary = "Delete an API",
            description = "Deletes an API definition by id."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "404", description = "API not found"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/apis/{id}")
    ResponseEntity<?> deleteApi(
            @PathVariable int id,
            @Parameter(description = "Requesting user", required = true) String user,
            @Parameter(description = "Optional group") String group
    );

    @Operation(
            summary = "Invoke an API",
            description = "Invokes the configured upstream API using the provided query."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Invocation result",
                    content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = InvokeResult.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "403", description = "Not allowed"),
            @ApiResponse(responseCode = "404", description = "API not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server error")
    })
    @PostMapping("/apis/{id}/invoke")
    ResponseEntity<InvokeResult> invokeApi(
            @RequestBody InvokeQuery query,
            @PathVariable int id,
            @Parameter(description = "Requesting user", required = true) String user,
            @Parameter(description = "Optional group") String group
    );
}

