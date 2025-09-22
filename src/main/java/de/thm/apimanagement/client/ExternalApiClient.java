package de.thm.apimanagement.client;

import de.thm.apimanagement.entity.InvokeQuery;
import de.thm.apimanagement.entity.InvokeResult;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * ExternalApiClient is responsible for communicating with external APIs
 *
 * @author Justin Wolek
 */
@Component
public class ExternalApiClient {
    private final RestClient client;

    public ExternalApiClient() {
        this.client = RestClient.create();
    }

    /**
     * Takes a {@link InvokeQuery}, calls the API with the query and wraps the result as an {@link InvokeResult}
     *
     * @param query The {@link InvokeQuery} to execute
     * @return      The result of the external API call wrapped inside a {@link InvokeResult}
     */
    public InvokeResult invoke(InvokeQuery query) {
        // TODO: Implement
        return null;
    }
}
