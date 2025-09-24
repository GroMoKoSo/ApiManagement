package de.thm.apimanagement.entity;

import lombok.Data;

import java.util.Map;


@Data
public class InvokeQuery {
    private RequestType requestType;
    private String requestPath;
    private Map<String, String> header;
    private String body;
    private Map<String, String> requestParam;
    private Map<String, String> pathParam;
}

