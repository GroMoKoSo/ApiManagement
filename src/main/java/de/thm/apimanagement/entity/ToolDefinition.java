package de.thm.apimanagement.entity;

import lombok.Data;

import java.util.List;

@Data
public class ToolDefinition {
    private String name;
    private String description;
    private List<Tool> tools;
}
