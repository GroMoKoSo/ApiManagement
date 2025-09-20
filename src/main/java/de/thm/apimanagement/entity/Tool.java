package de.thm.apimanagement.entity;

import lombok.Data;

import java.util.List;

@Data
public class Tool {
    private String name;
    private String description;
    private String endpoint;
    private List<Parameter> parameters;
}
