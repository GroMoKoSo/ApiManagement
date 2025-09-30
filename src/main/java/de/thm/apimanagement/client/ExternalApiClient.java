package de.thm.apimanagement.client;

import de.thm.apimanagement.entity.InvokeQuery;
import de.thm.apimanagement.entity.InvokeResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ExternalApiClient is responsible for communicating with external APIs
 *
 * @author Justin Wolek
 */
@Component
public class ExternalApiClient {
    private final Logger logger = LoggerFactory.getLogger(ExternalApiClient.class);
    private final RestClient client;

    public ExternalApiClient() {
        this.client = RestClient.create();
    }

    /**
     * Takes a {@link InvokeQuery}, calls the API with the query and wraps the result as an {@link InvokeResult}
     *
     * @param token A token which can be used as a bearer token when requesting
     * @param query The {@link InvokeQuery} to execute
     * @return      The result of the external API call wrapped inside a {@link InvokeResult}
     */
    public InvokeResult invoke(String token, InvokeQuery query) {
        ResponseEntity<String> response = sendRequest(token, query);
        return new InvokeResult(
                response.getStatusCode().value(),
                response.getHeaders().asSingleValueMap(),
                response.getBody());
    }

    /**
     * Formats a URL to include defined pathParameters and requestParameters
     *
     * @param requestPath       A raw URL containing no requestParameters. pathParameters should be designated
     *                          with {@code <>}. These placeholders will be replaced by values
     *                          inside {@code pathParameter}.
     * @param pathParameter     contains the values to replace {@code <>} inside {@code requestPath} with.
     * @param requestParameter  contains a mapping of key-values to append to the URL. These represent f.e.
     *                          options for sorting {@code ?sortBy=date}. In this case, {@code sortBy} is the
     *                          key and {@code date} the value.
     * @return                  A formatted URL path which contains all path- and request parameters.
     */
    private String formatPath(String requestPath, Map<String, String> pathParameter, Map<String, String> requestParameter) {
        // Replace path parameter placeholders with actual values
        for (Map.Entry<String, String> entry : pathParameter.entrySet()) {
            requestPath = requestPath.replace("<" + entry.getKey() + ">", entry.getValue());
        }

        // URLEncoder makes handling spaces in requestParameters WAY easier.
        // First concat the values of requestParameter and then join them with "&" as a separator
        if (!requestParameter.isEmpty()) {
            requestPath += "?" + requestParameter.entrySet().stream()
                    .map(e -> URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8) + "=" +
                            URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8))
                    .collect(Collectors.joining("&"));
        }

        return requestPath;
    }

    /**
     * Uses a {@link InvokeQuery} to send an http request
     *
     * @param query An {@link InvokeQuery} which will be used to send an http query.
     * @param token When not empty, will be added as a Bearer token to the request headers
     * @return      A {@link ResponseEntity<String>} which contains the response of the external http request
     */
    private ResponseEntity<String> sendRequest(String token, InvokeQuery query) {
        Map<String, String> pathParam = query.getPathParam() != null ? query.getPathParam() : Map.of();
        Map<String, String> requestParameter = query.getRequestParam() != null ? query.getRequestParam() : Map.of();
        Map<String, String> headers = query.getHeader() != null ? query.getHeader() : Map.of();

        String formattedPath = formatPath(query.getRequestPath(), pathParam, requestParameter);

        // If the token has content, add it as a request header
        if (StringUtils.hasText(token)) {
            logger.info("Using bearer token: {}", token);
            query.getHeader().put("Authorization", "Bearer " + token);
        }

        HttpMethod method = switch (query.getRequestType()) {
            case GET -> HttpMethod.GET;
            case POST -> HttpMethod.POST;
            case PUT -> HttpMethod.PUT;
            case PATCH -> HttpMethod.PATCH;
            case DELETE -> HttpMethod.DELETE;
        };


        return client.method(method)
                .uri(formattedPath)
                .headers(httpHeaders -> headers.forEach(httpHeaders::add))
                .body(query.getBody())
                .exchange((request, response) -> ResponseEntity
                        .status(response.getStatusCode())
                        .headers(response.getHeaders())
                        .body(response.bodyTo(String.class)));
    }
}
