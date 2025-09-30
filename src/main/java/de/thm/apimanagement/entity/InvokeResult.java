package de.thm.apimanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class InvokeResult {
    private int responseCode;
    private Map<String, String> header;
    private Map<String, Object> body;
}
