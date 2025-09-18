package de.thm.apimanagement.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.util.Base64;

@Entity
public class Api {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    int id;
    String name;
    String description;
    String version;
    String dataFormat;
    String spec;
    String token;


}
