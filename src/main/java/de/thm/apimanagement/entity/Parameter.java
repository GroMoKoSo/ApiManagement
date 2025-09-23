package de.thm.apimanagement.entity;

import lombok.Data;

@Data
public class Parameter {
    private String name;
    private String description;
    private String type;
    private boolean required;
}

