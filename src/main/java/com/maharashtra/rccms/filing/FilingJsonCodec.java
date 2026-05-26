package com.maharashtra.rccms.filing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maharashtra.rccms.dto.filing.Notice9ResolvedPayload;
import com.maharashtra.rccms.dto.filing.OrderSearchLocationPayload;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class FilingJsonCodec {

    private final ObjectMapper objectMapper;

    public FilingJsonCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public String toJson(Object value) {
        if (value == null) {
            return null;
        }
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Could not serialize filing JSON field.", ex);
        }
    }

    public Map<String, Object> readMap(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    public Object readObject(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Object.class);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    public OrderSearchLocationPayload readLocation(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, OrderSearchLocationPayload.class);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }

    public <T> T readValue(String json, Class<T> type) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, type);
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Could not deserialize filing JSON.", ex);
        }
    }

    public Notice9ResolvedPayload readNotice9(String json) {
        if (json == null || json.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(json, Notice9ResolvedPayload.class);
        } catch (JsonProcessingException ex) {
            return null;
        }
    }
}
