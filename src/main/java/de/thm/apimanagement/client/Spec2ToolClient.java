package de.thm.apimanagement.client;

import de.thm.apimanagement.client.exceptions.ClientExceptionHandler;
import de.thm.apimanagement.entity.ToolDefinition;
import de.thm.apimanagement.security.TokenProvider;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
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

    private final Logger logger = LoggerFactory.getLogger(Spec2ToolClient.class);
    private final TokenProvider tokenProvider;
    private final RestClient client;

    public Spec2ToolClient(TokenProvider tokenProvider, @Value("${spring.subservices.spec2tool.url}") String baseUrl) {
        this.tokenProvider = tokenProvider;
        this.client = RestClient.builder()
                .baseUrl(baseUrl)
                .build();
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
        if (!StringUtils.hasText(format) || !StringUtils.hasText(fileType) || !StringUtils.hasText(spec)) {
            throw new IllegalArgumentException("Format, File Type and Spec cannot be empty");
        } else if (!Arrays.asList(ALLOWED_FILE_TYPES).contains(fileType)) {
            throw new IllegalArgumentException("File Type not supported");
        } else if (!Arrays.asList(ALLOWED_FORMATS).contains(format)) {
            throw new IllegalArgumentException("Format not supported");
        }

        try {
            return client.post()
                    .uri("/convert" )
                    .header("Authorization", "Bearer " + tokenProvider.getToken())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(new Spec2ToolBody(format, fileType, spec))
                    .retrieve()
                    .body(ToolDefinition.class);
        } catch (Exception e) {
            throw ClientExceptionHandler.handleException(e);
        }
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
