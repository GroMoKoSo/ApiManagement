package de.thm.apimanagement.controller;
import de.thm.apimanagement.entity.Api;
import de.thm.apimanagement.entity.InvokeQuery;
import de.thm.apimanagement.entity.InvokeResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import java.util.List;


@Tag(name = "APIs", description = "CRUD & invocation for API definitions")
public class ApiManagementController {

    @Operation(
            summary = "List APIs",
            description = "Returns all API definitions."
    )
    @ApiResponse(responseCode = "200", description = "List of APIs returned",
            content = @Content(schema = @Schema(implementation = Api.class)))
    ResponseEntity<List<Api>> getApis();

    @Operation(
            summary = "Create an API",
            description = "Creates a new API definition."
    )
    @ApiResponse(responseCode = "200", description = "API created",
            content = @Content(schema = @Schema(implementation = Api.class)))
    @ApiResponse(responseCode = "403", description = "Not allowed")
    @ApiResponse(responseCode = "404", description = "Related service not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    ResponseEntity<Api> postApi(
            @RequestBody(
                    description = "API payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Api.class))
            ) Api api,
            @Parameter(description = "Requesting user", required = true) String user,
            @Parameter(description = "Optional group") String group
    );

    @Operation(
            summary = "Get API by id",
            description = "Fetch a single API definition by its id."
    )
    @ApiResponse(responseCode = "200", description = "API returned",
            content = @Content(schema = @Schema(implementation = Api.class)))
    ResponseEntity<Api> getApi(
            @Parameter(description = "API id", required = true) int id
    );

    @Operation(
            summary = "Replace an API",
            description = "Replaces an existing API definition by id."
    )
    @ApiResponse(responseCode = "200", description = "API updated",
            content = @Content(schema = @Schema(implementation = Api.class)))
    @ApiResponse(responseCode = "403", description = "Not allowed")
    @ApiResponse(responseCode = "404", description = "API or related service not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    ResponseEntity<Api> putApi(
            @RequestBody(
                    description = "New API payload",
                    required = true,
                    content = @Content(schema = @Schema(implementation = Api.class))
            ) Api api,
            @Parameter(description = "Existing API id", required = true) int id,
            @Parameter(description = "Requesting user", required = true) String user,
            @Parameter(description = "Optional group") String group
    );

    @Operation(
            summary = "Delete an API",
            description = "Deletes an API definition by id."
    )
    @ApiResponse(responseCode = "204", description = "Deleted")
    @ApiResponse(responseCode = "403", description = "Not allowed")
    @ApiResponse(responseCode = "404", description = "API not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    ResponseEntity<?> deleteApi(
            @Parameter(description = "API id", required = true) int id,
            @Parameter(description = "Requesting user", required = true) String user,
            @Parameter(description = "Optional group") String group
    );

    @Operation(
            summary = "Invoke an API",
            description = "Invokes the configured upstream API using the provided query."
    )
    @ApiResponse(responseCode = "200", description = "Invocation result",
            content = @Content(schema = @Schema(implementation = InvokeResult.class)))
    @ApiResponse(responseCode = "403", description = "Not allowed")
    @ApiResponse(responseCode = "404", description = "API not found")
    @ApiResponse(responseCode = "500", description = "Server error")
    ResponseEntity<InvokeResult> invokeApi(
            @RequestBody(
                    description = "Invocation parameters",
                    required = true,
                    content = @Content(schema = @Schema(implementation = InvokeQuery.class))
            ) InvokeQuery query,
            @Parameter(description = "API id", required = true) int id,
            @Parameter(description = "Requesting user", required = true) String user,
            @Parameter(description = "Optional group") String group
    );
}
}
