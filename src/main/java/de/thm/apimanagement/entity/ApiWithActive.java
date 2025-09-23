package de.thm.apimanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ApiWithActive {
    private int apiId;
    private boolean active;
}
