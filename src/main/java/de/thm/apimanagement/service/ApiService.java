package de.thm.apimanagement.service;

import de.thm.apimanagement.entity.Api;
import de.thm.apimanagement.entity.InvokeQuery;
import de.thm.apimanagement.entity.InvokeResult;

import java.util.List;

/**
 * Defines an API service which contains the business logic of the microservice.
 * Meant to be used by a RESTController
 *
 * @author Justin Wolek
 */
public interface ApiService {

    /**
     * Handles saving an API
     *
     * @param api   The API to save
     * @return      The saved API
     */
    public Api saveApi(Api api, String user, String group);

    /**
     * Handles updating an API
     *
     * @param apiId The id of the API to update
     * @param api   The API object which should be used to update its current instance
     * @return      The updated API
     */
    public Api updateApi(int apiId, Api api, String user, String group);

    /**
     * Handles deleting an API
     *
     * @param apiId The id of the API which should be deleted
     */
    public void deleteApiById(int apiId, String user, String group);

    /**
     * Gets an array which contains all APIs
     *
     * @return  An array with every current API
     */
    public List<Api> fetchApiList();

    /**
     * Gets one {@link Api} or {@code null} with a matching id.
     *
     * @param apiId The id of the API which should be fetched.
     * @return      The {@link Api} with a matching id or {@code null}
     */
    public Api fetchApiById(int apiId);

    /**
     * Handles querying an HTTP request to an API according to the {@link InvokeQuery}
     *
     * @param query The query which should be performed
     * @return      A {@link InvokeResult} which wraps the HTTP response
     */
    public InvokeResult invoke(InvokeQuery query);
}
