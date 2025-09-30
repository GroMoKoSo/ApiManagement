package de.thm.apimanagement.entity;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Converts a JSON structure to a string when saved inside the database and to a JSON structure
 * when read from the database
 *
 * @author Justin Wolek
 */
@Converter
public class JsonToMapConverter implements AttributeConverter<Map<String, Object>, String> {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Converts from a {@code Map<String, Object>} to a String
     *
     * @param stringObjectMap   The JSON structure to convert
     * @return                  A String representing the input
     */
    @Override
    public String convertToDatabaseColumn(Map<String, Object> stringObjectMap) {
        if (stringObjectMap == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(stringObjectMap);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error converting map to JSON", e);
        }
    }

    /**
     * Converts a String to a {@code Map<String, Object>}
     *
     * @param s The String to convert
     * @return  A {@code Map<String, Object>} as defined in the String
     */
    @Override
    public Map<String, Object> convertToEntityAttribute(String s) {
        if (s == null) {
            return new HashMap<>();
        }
        try {
            return objectMapper.readValue(s, Map.class);
        } catch (IOException e) {
            throw new RuntimeException("Error converting JSON to map", e);
        }
    }
}
