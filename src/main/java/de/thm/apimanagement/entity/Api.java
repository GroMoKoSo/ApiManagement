package de.thm.apimanagement.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Api {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    private String name;
    private String description;
    private String version;
    private String format;
    private String fileType;
    @Convert(converter = JsonToMapConverter.class)
    @Lob
    private Map<String, Object> spec;
    @Lob
    private String token;

    public Api(Api other) {
        this.id = other.id;
        this.name = other.name;
        this.description = other.description;
        this.version = other.version;
        this.format = other.format;
        this.fileType = other.fileType;
        this.spec = other.spec;
        this.token = other.token;
    }

    @PrePersist
    @PreUpdate
    public void normalizeFields() {
        if (format != null) {
            format = format.toLowerCase();
        }
        if (fileType != null) {
            fileType = fileType.toLowerCase();
        }
    }
}


