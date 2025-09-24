package de.thm.apimanagement.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String systemRole;
}
