package de.thm.apimanagement.client;

import de.thm.apimanagement.entity.ToolDefinition;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;

/**
 * Spec2ToolClient is responsible for communicating with the Spec2Tool subsystem
 *
 * @author Justin Wolek
 */
@Component
public class Spec2ToolClient {
    private static final String[] ALLOWED_FILE_TYPES = {"YAML", "JSON"};
    private static final String[] ALLOWED_FORMATS = {"OpenAPI", "RAML"};

    private final RestClient client;

    @Value("${spring.subservices.spec2tool.url}")
    private String baseUrl;

    public Spec2ToolClient() {
        this.client = RestClient.create();
    }

    /**
     * Takes an API specification with a given format and fileType and returns a {@link ToolDefinition} containing
     * a definition for an MCP tool.
     *
     * @param format    Specification format. Can be OpenAPI or RAML
     * @param fileType  The filetype which the string is formatted in. Can be JSON or YAML
     * @param spec      The string containing the specification
     * @return          The definition of an MCP tool as a {@link ToolDefinition} object
     */
    public ToolDefinition convertSpec2Tool(String format, String fileType, String spec) {
        if (format.isEmpty() || fileType.isEmpty() || spec.isEmpty()) {
            throw new IllegalArgumentException("Format, File Type and Spec cannot be empty");
        } else if (!Arrays.asList(ALLOWED_FILE_TYPES).contains(fileType)) {
            throw new IllegalArgumentException("File Type not supported");
        } else if (!Arrays.asList(ALLOWED_FORMATS).contains(format)) {
            throw new IllegalArgumentException("Format not supported");
        }

        return client.post()
                .uri(baseUrl + "/convert" )
                .contentType(MediaType.APPLICATION_JSON)
                .body(new Spec2ToolBody(format, fileType, spec))
                .retrieve()
                .body(ToolDefinition.class);
    }

    /**
     * Represents the body which is sent in an http request to convert an
     * API specification to a {@link ToolDefinition}
     */
    @Data
    public static class Spec2ToolBody {
        private String format;
        private String fileType;
        private String spec;

        public Spec2ToolBody(String format, String fileType, String spec) {
            this.format = format;
            this.fileType = fileType;
            this.spec = spec;
        }
    }
}
