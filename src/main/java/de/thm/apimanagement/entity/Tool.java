package de.thm.apimanagement.entity;

import lombok.Data;

import java.util.Map;

@Data
public class Tool {
    private String name;
    private String description;
    private String requestMethod;
    private String endpoint;
    private Map<String, Object> inputSchema;
}
