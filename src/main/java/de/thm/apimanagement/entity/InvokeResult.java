package de.thm.apimanagement.entity;

import lombok.Data;

import java.util.Map;

@Data
public class InvokeResult {
    private int responseCode;
    private Map<String, String> header;
    private String body;
}
