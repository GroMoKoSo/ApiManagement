package de.thm.apimanagement.entity;

import lombok.Data;

@Data
public class Tool {
    private String title;
    private String description;
    private String endpoint;
    private String inputSchema;
    private String requestMethod;
}
